package com.spb.kbv.messageapp.infrastructure;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;


public class ActionScheduler {
    private final MessageApplication application;
    private final Handler handler;
    private final ArrayList<TimedCallback> timedCallbacks;
    private final HashMap<Class, Runnable> onResumeActions;
    private boolean isPaused;

    public ActionScheduler(MessageApplication application) {
        this.application = application;
        handler = new Handler();
        timedCallbacks = new ArrayList<>();
        onResumeActions = new HashMap<>();
    }

    public void onPause(){
        isPaused = true;
    }

    public void onResume(){
        isPaused = false;

        for (TimedCallback callback : timedCallbacks){
            callback.schedule();
        }

        for (Runnable runnable : onResumeActions.values()){
            runnable.run();
        }

        onResumeActions.clear();
    }


    public void invokeOnResume (Class cls, Runnable runnable){
        if (!isPaused) {
            runnable.run();
            return;
        }

        onResumeActions.put(cls, runnable);
    }

    public void postDelayed(Runnable runnable, long milliseconds){
        handler.postDelayed(runnable, milliseconds);
    }

    public void invokeEveryMilliseconds(Runnable runnable, long milliseconds){
        invokeEveryMilliseconds(runnable, milliseconds, true);

    }

    public void invokeEveryMilliseconds(Runnable runnable, long milliseconds, boolean runImmediately){
        TimedCallback callback = new TimedCallback(runnable, milliseconds);
        timedCallbacks.add(callback);
        if (runImmediately) {
            callback.run();
        } else {
            postDelayed(callback, milliseconds);
        }
    }

    public void postEveryMilliseconds(Object request, long milliseconds){
        postEveryMilliseconds(request, milliseconds, true);
    }

    public void postEveryMilliseconds(final Object request, long milliseconds, boolean postImmediately){
        invokeEveryMilliseconds(new Runnable() {
            @Override
            public void run() {
                application.getBus().post(request);
            }
        }, milliseconds, postImmediately);
    }

    private class TimedCallback implements Runnable{
        private final Runnable runnable;
        private final long delay;
        public TimedCallback(Runnable runnable, long delay) {
            this.runnable = runnable;
            this.delay = delay;
        }

        @Override
        public void run() {
            if (isPaused)
                return;

            runnable.run();
            schedule();
        }

        public void schedule(){
            handler.postDelayed(this, delay);
        }
    }


}
