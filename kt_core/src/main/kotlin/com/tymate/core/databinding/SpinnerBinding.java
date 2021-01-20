package com.tymate.core.databinding;

import android.os.Build;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import androidx.databinding.BindingAdapter;

/**
 * Created by Aurélien COCQ
 * aurelien@tymate.com
 */
public class SpinnerBinding {

    @BindingAdapter(value = {"adapter", "selection", "listener"}, requireAll = false)
    public static void initSpinner(final Spinner spinner,
                                   final BaseAdapter baseAdapter,
                                   final int selection,
                                   final AdapterView.OnItemSelectedListener listener) {
        if (baseAdapter == null) {
            return;
        }
        spinner.setAdapter(baseAdapter);
        int count = baseAdapter.getCount();
        if (selection >= count || count == 0 || selection < 0) {
            return;
        }
        spinner.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 21 && !spinner.isAttachedToWindow()) {
                    return;
                }
                spinner.setSelection(selection);
            }
        });
        AdapterView.OnItemSelectedListener currentListener = spinner.getOnItemSelectedListener();
        if (currentListener == null || currentListener != listener) {
            spinner.setOnItemSelectedListener(listener);
        }
    }
}
