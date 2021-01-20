package com.tymate.core.adapter

import com.tymate.core.ui.BR
import com.tymate.core.ui.R
import com.tymate.core.widget.Loader


class LoaderBinder :
    Binder<Loader>(R.layout.list_item_loader, BR.loader) {

    override fun isValid(item: Any): Boolean {
        return item is Loader
    }
}