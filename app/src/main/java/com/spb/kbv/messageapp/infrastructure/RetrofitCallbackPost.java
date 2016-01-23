package com.spb.kbv.messageapp.infrastructure;

import android.util.Log;

import com.squareup.otto.Bus;

public class RetrofitCallbackPost<T extends ServiceResponse> extends RetrofitCallback<T> {
    private static final String TAG = "RetrofitCallbackPost";

    private final Bus bus;

    public RetrofitCallbackPost(Class<T> resultType, Bus bus) {
        super(resultType);
        this.bus = bus;
    }

    @Override
    protected void onResponse(T t) {
        if (t == null) {
            try {
                t = resultType.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Could not create blank result object", e);
            }
        }

        bus.post(t);
    }
}
