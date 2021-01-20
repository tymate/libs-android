package com.tymate.core.databinding

import android.os.Parcelable
import androidx.databinding.*
import androidx.databinding.Observable.OnPropertyChangedCallback
import com.tymate.core.optional
import io.reactivex.Observable
import io.reactivex.Observable.create
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.Math.max
import java.lang.Math.min
import androidx.databinding.Observable as DataBindingObservable

@Suppress("UNCHECKED_CAST")
private inline fun <T : DataBindingObservable, R : Any?> T.observe(crossinline block: (T) -> R): Observable<R> = create<R> { subscriber ->
//    block(this)?.let(subscriber::onNext)
    object : OnPropertyChangedCallback() {
        override fun onPropertyChanged(observable: DataBindingObservable, id: Int) = try {
            subscriber.onNext(block(observable as T))
//            block(observable as T)?.run { subscriber.onNext(it) }
        } catch (e: Exception) {
            Timber.e(e)
            subscriber.onError(e)
        }
    }.let {
        subscriber.setCancellable { this.removeOnPropertyChangedCallback(it) }
        this.addOnPropertyChangedCallback(it)
        block(this)?.let(subscriber::onNext)
    }
}.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

class ObservableString(initialValue: String?) : ObservableField<String?>(initialValue)

fun <T : androidx.databinding.Observable> T.observe() = observe { this }
fun <T : BaseObservable> T.observe() = observe { this }
fun ObservableInt.observe() = observe { it.get() }
fun ObservableByte.observe() = observe { it.get() }
fun ObservableChar.observe() = observe { it.get() }
fun ObservableLong.observe() = observe { it.get() }
fun ObservableShort.observe() = observe { it.get() }
fun ObservableFloat.observe() = observe { it.get() }
fun ObservableDouble.observe() = observe { it.get() }
fun ObservableBoolean.observe() = observe { it.get() }
fun <T : Any> ObservableField<T>.observe() = observe { it.get() }
fun <T : Parcelable> ObservableParcelable<T>.observe() = observe { it.get() }
fun <T : Any> ObservableField<T?>.observeNullable() = observe { it.get().optional() }
fun <T : Parcelable> ObservableParcelable<T?>.observeNullable() = observe { it.get().optional() }

operator fun ObservableInt.invoke() = get()
operator fun ObservableByte.invoke() = get()
operator fun ObservableChar.invoke() = get()
operator fun ObservableLong.invoke() = get()
operator fun ObservableShort.invoke() = get()
operator fun ObservableFloat.invoke() = get()
operator fun ObservableDouble.invoke() = get()
operator fun ObservableBoolean.invoke() = get()
operator fun <T : Any?> ObservableField<T>.invoke(): T? = get()
operator fun <T : Parcelable?> ObservableParcelable<T>.invoke(): T? = get()

fun ObservableInt.inc() = apply { set(get().inc()) }
fun ObservableByte.inc() = apply { set(get().inc()) }
fun ObservableChar.inc() = apply { set(get().inc()) }
fun ObservableLong.inc() = apply { set(get().inc()) }
fun ObservableShort.inc() = apply { set(get().inc()) }
fun ObservableFloat.inc() = apply { set(get().inc()) }
fun ObservableDouble.inc() = apply { set(get().inc()) }

fun ObservableInt.inc(max: Int) = apply { set(min(get().inc(), max)) }
fun ObservableByte.inc(max: Byte) = apply { set(min(get().inc(), max)) }
fun ObservableChar.inc(max: Char) = apply { set(min(get().inc(), max)) }
fun ObservableLong.inc(max: Long) = apply { set(min(get().inc(), max)) }
fun ObservableShort.inc(max: Short) = apply { set(min(get().inc(), max)) }
fun ObservableFloat.inc(max: Float) = apply { set(min(get().inc(), max)) }
fun ObservableDouble.inc(max: Double) = apply { set(min(get().inc(), max)) }

fun ObservableInt.dec() = apply { set(get().dec()) }
fun ObservableByte.dec() = apply { set(get().dec()) }
fun ObservableChar.dec() = apply { set(get().dec()) }
fun ObservableLong.dec() = apply { set(get().dec()) }
fun ObservableShort.dec() = apply { set(get().dec()) }
fun ObservableFloat.dec() = apply { set(get().dec()) }
fun ObservableDouble.dec() = apply { set(get().dec()) }

fun ObservableInt.dec(min: Int) = apply { set(max(get().dec(), min)) }
fun ObservableByte.dec(min: Byte) = apply { set(max(get().dec(), min)) }
fun ObservableChar.dec(min: Char) = apply { set(max(get().dec(), min)) }
fun ObservableLong.dec(min: Long) = apply { set(max(get().dec(), min)) }
fun ObservableShort.dec(min: Short) = apply { set(max(get().dec(), min)) }
fun ObservableFloat.dec(min: Float) = apply { set(max(get().dec(), min)) }
fun ObservableDouble.dec(min: Double) = apply { set(max(get().dec(), min)) }

private fun min(a: Short, b: Short) = if (a > b) b else a
private fun min(a: Byte, b: Byte) = if (a > b) b else a
private fun min(a: Char, b: Char) = if (a > b) b else a

private fun max(a: Short, b: Short) = if (a < b) b else a
private fun max(a: Byte, b: Byte) = if (a < b) b else a
private fun max(a: Char, b: Char) = if (a < b) b else a


inline fun <T> dependantObservableField(vararg dependencies: androidx.databinding.Observable, crossinline mapper: () -> T?) =
        object : ObservableField<T>(*dependencies) {
            override fun get(): T? {
                return mapper()
            }
        }

inline fun dependantObservableBoolean(vararg dependencies: androidx.databinding.Observable, crossinline mapper: () -> Boolean) =
    object : ObservableBoolean(*dependencies) {
        override fun get(): Boolean {
            return mapper()
        }
    }

inline fun dependantObservableInt(vararg dependencies: androidx.databinding.Observable, crossinline mapper: () -> Int) =
    object : ObservableInt(*dependencies) {
        override fun get(): Int {
            return mapper()
        }
    }