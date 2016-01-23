package com.spb.kbv.messageapp.activities;

import android.os.Bundle;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.fragments.LoginFragment;

public class LoginNarrowActivity extends BaseActivity implements LoginFragment.Callbacks{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_narrow);
    }

    @Override
    public void onLoggedIn() {
        setResult(RESULT_OK);
        finish();
    }
}
