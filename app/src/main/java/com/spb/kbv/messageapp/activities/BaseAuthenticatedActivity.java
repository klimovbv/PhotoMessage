package com.spb.kbv.messageapp.activities;

import android.content.Intent;
import android.os.Bundle;

public abstract class BaseAuthenticatedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!application.getAuth().getUser().isLoggedIn()){
            if (application.getAuth().hasAuthToken()){
                Intent intent = new Intent(this, AuthenticationActivity.class);
                intent.putExtra(AuthenticationActivity.EXTRA_RETURN_TO_ACTIVITY, getClass().getName());
                startActivity(intent);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
            return;
        }
        onMessageAppCreate(savedInstanceState);
    }

    protected abstract void onMessageAppCreate(Bundle savedInstanceState);
}
