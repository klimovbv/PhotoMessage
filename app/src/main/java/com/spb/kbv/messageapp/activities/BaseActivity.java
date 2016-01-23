package com.spb.kbv.messageapp.activities;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.infrastructure.ActionScheduler;
import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.views.NavDrawer;
import com.squareup.otto.Bus;

public class BaseActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private boolean isRegisteredWithBus;

    protected MessageApplication application;
    protected Toolbar toolbar;
    protected NavDrawer navDrawer;
    protected boolean isTablet;
    protected Bus bus;
    protected ActionScheduler scheduler;
    protected SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (MessageApplication)getApplication();
        bus = application.getBus();
        scheduler = new ActionScheduler(application);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels / metrics.density) >= 600;
        bus.register(this);
        isRegisteredWithBus = true;
    }

    public ActionScheduler getScheduler() {
        return scheduler;
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduler.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scheduler.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisteredWithBus){
            bus.unregister(this);
            isRegisteredWithBus = false;
        }

        if (navDrawer != null)
            navDrawer.destroy();
    }

    @Override
    public void finish() {
        super.finish();

        if (isRegisteredWithBus){
            bus.unregister(this);
            isRegisteredWithBus = false;
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar)findViewById(R.id.include_toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
        }

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh);
        if (swipeRefresh != null){
            swipeRefresh.setOnRefreshListener(this);

            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444")
            );
        }

    }

    public void fadeOut(final FadeOutListener listener){
        View rootView = findViewById(android.R.id.content);
        rootView.animate()
                .alpha(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        listener.onFadeOutEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                })
                .setDuration(300)
                .start();
    }

    @Override
    public void onRefresh() {

    }

    public interface FadeOutListener {
        void onFadeOutEnd();
    }
    protected void setNavDrawer(NavDrawer navDrawer){
        this.navDrawer = navDrawer;
        this.navDrawer.create();

        overridePendingTransition(0, 0);
        View rootView = findViewById(android.R.id.content);
        rootView.setAlpha(0);
        rootView.animate().alpha(1).setDuration(450).start();
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    public MessageApplication getMessageAppApplication(){
        return application;
    }
}
