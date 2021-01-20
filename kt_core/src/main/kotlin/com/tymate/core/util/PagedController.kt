package com.tymate.core.util

import androidx.annotation.CheckResult
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tymate.core.Paged
import com.tymate.core.databinding.observe
import com.tymate.core.state.CollectionState
import com.tymate.core.state.loadMore
import com.tymate.core.state.pullData
import com.tymate.core.state.refreshPaged
import com.tymate.core.widget.Loader
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class PagedCollectionController<Data> constructor(
    private val request: (page: Int) -> Single<Paged<List<Data>>>
) : RecyclerView.OnScrollListener(), Disposable, androidx.databinding.Observable {

    @Transient
    private val observableCallbacks = PropertyChangeRegistry()

    private val subject = CollectionState.binding<Data>(paginationEnabled = true)
    private var disposable: Disposable? = null
    private val compositeDisposable = CompositeDisposable()

    val state: ObservableField<CollectionState<Any>> =
        CollectionState.binding(paginationEnabled = true)
    var transformer: ((CollectionState<Data>) -> CollectionState<*>)? = null

    init {
        state.addOnPropertyChangedCallback(object :
            androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(
                sender: androidx.databinding.Observable?,
                propertyId: Int
            ) {
                notifyChange()
            }
        })
        subject.observe()
            .map { (transformer?.invoke(it) ?: it) as CollectionState<Any> }
            .subscribe({ collectionState ->
                if (!(collectionState as CollectionState).loading) {
                    (collectionState.data as ArrayList).removeAll { item -> item is Loader }
                }
                state.set(collectionState)
            }, Timber::w)
            .addTo(compositeDisposable)
    }

    @CheckResult
    fun loadMore(): Disposable? {
        if (!canLoadMore()) {
            return null
        }
        return requestMore()
    }

    @CheckResult
    fun pullData(): Disposable {
        disposable?.dispose()
        disposable = request(1).pullData(subject)
        addLoader()
        disposable?.addTo(compositeDisposable)
        return disposable!!
    }

    private fun addLoader() {
        val list = state.get()?.data as ArrayList
        if (list.size == 0 || list[list.size - 1] !is Loader) {
            list.add(Loader())
        }
    }

    @CheckResult
    fun refresh(): Disposable {
        disposable?.dispose()
        disposable = request(1).refreshPaged(subject)
        addLoader()
        disposable?.addTo(compositeDisposable)
        return disposable!!
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager.reverseLayout) {
            if (dy > 0) {
                return
            }
        } else if (dy < 0) {
            return
        }
        if (!canLoadMore()) {
            return
        }
        val totalItemCount = layoutManager.itemCount
        val visibleItem: Int = layoutManager.findLastVisibleItemPosition()
        if (visibleItem >= totalItemCount - VISIBLE_THRESHOLD) {
            requestMore()
        }
    }

    fun canLoadMore(): Boolean {
        val state = state.get() ?: return false
        if (!state.canLoadMore()) return false
        return true
    }

    fun requestMore(): Disposable? {
        disposable?.dispose()
        val page = state.get()!!.currentPage + 1
        disposable = request(page).loadMore(this.subject)
        addLoader()
        disposable?.addTo(compositeDisposable)
        return disposable
    }

    override fun isDisposed(): Boolean {
        return compositeDisposable.isDisposed
    }

    override fun dispose() {
        compositeDisposable.clear()
    }


    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        observableCallbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        observableCallbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        observableCallbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        observableCallbacks.notifyCallbacks(this, fieldId, null)
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
}