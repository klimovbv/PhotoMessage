package com.spb.kbv.messageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.fragments.LoginFragment;
import com.spb.kbv.messageapp.fragments.RegisterGcmFragment;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginFragment.Callbacks {

    private static final int REQUEST_NARROW_LOGIN = 1;
    private static final int REQUEST_REGISTER = 2;
    private static final int REQUEST_EXTERNAL_LOGIN = 3;
    private View loginButton;
    private View registerButton;
    private View googleLoginButton;
    private View facebookLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.activity_login_login);
        registerButton = findViewById(R.id.activity_login_register);
        googleLoginButton = findViewById(R.id.activity_login_google);
        facebookLoginButton = findViewById(R.id.activity_login_facebook);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        registerButton.setOnClickListener(this);
        googleLoginButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == loginButton)
            startActivityForResult(new Intent(this, LoginNarrowActivity.class), REQUEST_NARROW_LOGIN);
        else if (view == registerButton)
            startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_REGISTER);
        else if (view == googleLoginButton)
            doExternalLogin("Google");
        else if (view == facebookLoginButton)
            doExternalLogin("Facebook");
    }

    private void doExternalLogin(String externalService) {
        Intent intent = new Intent(this, ExternalLoginActivity.class);
        intent.putExtra(ExternalLoginActivity.EXTRA_EXTERNAL_SERVICE, externalService);
        startActivityForResult(intent, REQUEST_EXTERNAL_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_NARROW_LOGIN ||
                requestCode == REQUEST_REGISTER ||
                requestCode == REQUEST_EXTERNAL_LOGIN)

            RegisterGcmFragment.get(new RegisterGcmFragment.GcmRegistrationCallback() {
                @Override
                public void gcmFinished() {
                    finishLogin();
                }
            }, requestCode == REQUEST_REGISTER, getFragmentManager());
    }

    private void finishLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onLoggedIn() {
        finishLogin();
    }
}
