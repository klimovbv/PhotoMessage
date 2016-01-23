package com.spb.kbv.messageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.Account;
import com.squareup.otto.Subscribe;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_EXTERNAL_PROVIDER = "EXTRA_EXTERNAL_PROVIDER";
    public static final String EXTRA_EXTERNAL_USERNAME = "EXTRA_EXTERNAL_USERNAME";
    public static final String EXTRA_EXTERNAL_TOKEN = "EXTRA_EXTERNAL_TOKEN";

    private EditText usernameText;
    private EditText emailText;
    private EditText passwordText;
    private Button registerButton;
    private View progressBar;
    private String defaultRegisterButtonText;

    private boolean isExternalLogin;
    private String externalToken;
    private String externalProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameText = (EditText)findViewById(R.id.activity_register_userMame);
        emailText = (EditText)findViewById(R.id.activity_register_email);
        passwordText = (EditText)findViewById(R.id.activity_register_password);
        registerButton = (Button)findViewById(R.id.activity_register_button);
        progressBar = findViewById(R.id.activity_register_progressBar);
        defaultRegisterButtonText = registerButton.getText().toString();

        registerButton.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        externalToken = intent.getStringExtra(EXTRA_EXTERNAL_TOKEN);
        externalProvider = intent.getStringExtra(EXTRA_EXTERNAL_PROVIDER);
        isExternalLogin = externalToken != null;

        if (isExternalLogin) {
            passwordText.setVisibility(View.GONE);
            usernameText.setText(intent.getStringExtra(EXTRA_EXTERNAL_USERNAME));
        }
    }

    @Override
    public void onClick(View view) {

        if (passwordText.getText().toString().isEmpty()){
            passwordText.setError("Enter a password");
            return;
        }

        if (view == registerButton) {
            progressBar.setVisibility(View.VISIBLE);
            registerButton.setText("");
            registerButton.setEnabled(false);
            usernameText.setEnabled(false);
            emailText.setEnabled(false);
            passwordText.setEnabled(false);

            if (isExternalLogin) {
                bus.post(new Account.RegisterWithExternalTokenRequest(
                        usernameText.getText().toString(),
                        emailText.getText().toString(),
                        externalProvider,
                        externalToken));
            } else {
                bus.post(new Account.RegisterRequest(
                        usernameText.getText().toString(),
                        emailText.getText().toString(),
                        passwordText.getText().toString()));
            }
        }
    }

    @Subscribe
    public void registerResponse(Account.RegisterResponse response){
        onUserResponse(response);
    }

    @Subscribe
    public void externalRegisterResponse(Account.RegisterWithExternalTokenResponse response){
        onUserResponse(response);
    }

    private void onUserResponse(Account.UserResponse response) {
        if (response.didSucceed()){
            setResult(RESULT_OK);
            finish();
            return;
        }

        response.showErrorToast(this);
        usernameText.setError(response.getPropertyError("userName"));
        emailText.setError(response.getPropertyError("email"));
        passwordText.setError(response.getPropertyError("password"));

        registerButton.setEnabled(true);
        usernameText.setEnabled(true);
        emailText.setEnabled(true);
        passwordText.setEnabled(true);

        progressBar.setVisibility(View.GONE);

        registerButton.setText(defaultRegisterButtonText);

    }


}
