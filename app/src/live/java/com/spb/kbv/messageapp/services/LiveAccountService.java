package com.spb.kbv.messageapp.services;

import android.util.Log;

import com.spb.kbv.messageapp.infrastructure.Auth;
import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.infrastructure.RetrofitCallback;
import com.spb.kbv.messageapp.infrastructure.RetrofitCallbackPost;
import com.spb.kbv.messageapp.infrastructure.User;
import com.squareup.otto.Subscribe;

import java.io.File;

import retrofit.mime.TypedFile;

public class LiveAccountService extends BaseLiveService {
    private final Auth auth;

    public LiveAccountService(MessageApplication application, WebService api) {
        super(application, api);
        auth = application.getAuth();
    }

    @Subscribe
    public void register(Account.RegisterRequest request){
        api.register(request.userName,
                request.email,
                request.password, new RetrofitCallback<Account.RegisterResponse>(Account.RegisterResponse.class) {

            @Override
            protected void onResponse(Account.RegisterResponse registerResponse) {
                if (registerResponse.didSucceed()) {
                    loginUser(registerResponse);
                }

                bus.post(registerResponse);
            }
        });
    }


    @Subscribe
    public void loginWithUsername(final Account.LoginWithUsernameRequest request){
        api.login(
                request.userName,
                request.password,
                new RetrofitCallback<WebService.LoginResponse>(WebService.LoginResponse.class) {
                    @Override
                    protected void onResponse(WebService.LoginResponse loginResponse) {
                        if(!loginResponse.didSucceed()){
                            Account.LoginWithUsernameResponse response = new Account.LoginWithUsernameResponse();
                            response.setPropertyError("userName", loginResponse.errorDescription);
                            bus.post(response);
                            return;
                        }

                        auth.setAuthToken(loginResponse.token);
                        api.getAccount(new RetrofitCallback<Account.LoginWithLocalTokenResponse>(Account.LoginWithLocalTokenResponse.class) {
                            @Override
                            protected void onResponse(Account.LoginWithLocalTokenResponse loginWithLocalTokenResponse) {
                                if (!loginWithLocalTokenResponse.didSucceed()){
                                    Account.LoginWithUsernameResponse response = new Account.LoginWithUsernameResponse();
                                    response.setOperationError(loginWithLocalTokenResponse.getOperationError());
                                    bus.post(response);
                                    return;
                                }

                                loginUser(loginWithLocalTokenResponse);
                                bus.post(new Account.LoginWithUsernameResponse());
                            }
                        });
                    }
                });
    }

    @Subscribe
    public void loginWithLocalToken(final Account.LoginWithLocalTokenRequest request){
        api.getAccount(new RetrofitCallbackPost<Account.LoginWithLocalTokenResponse>(Account.LoginWithLocalTokenResponse.class, bus) {
            @Override
            protected void onResponse(Account.LoginWithLocalTokenResponse loginWithLocalTokenResponse) {
                loginUser(loginWithLocalTokenResponse);
                super.onResponse(loginWithLocalTokenResponse);
            }
        });
    }

    @Subscribe
    public void updateProfile(final Account.UpdateProfileRequest request){
        api.updateProfile(request.displayName, request.email, new RetrofitCallbackPost<Account.UpdateProfileResponse>(Account.UpdateProfileResponse.class, bus) {
            @Override
            protected void onResponse(Account.UpdateProfileResponse response) {
                User user = auth.getUser();
                user.setDisplayName(response.displayName);
                user.setEmail(response.email);
                super.onResponse(response);
                bus.post(new Account.UserDetailsUpdateEvent(user));
            }
        });
    }

    @Subscribe
    public void updateAvatar(final Account.ChangeAvatarRequest request){
        api.updateAvatar(
                new TypedFile("image/jpeg", new File(request.newAvatarUri.getPath())),
                new RetrofitCallbackPost<Account.ChangeAvatarResponse>(Account.ChangeAvatarResponse.class, bus) {
                    @Override
                    protected void onResponse(Account.ChangeAvatarResponse response) {
                        User user = auth.getUser();
                        user.setAvatarUrl(response.avatarUrl);
                        super.onResponse(response);
                        bus.post(new Account.UserDetailsUpdateEvent(user));
                    }
                }
        );
    }

    @Subscribe
    public void changePassword(Account.ChangePasswordRequest request) {
        api.updatePassword(
        request.newPassword,
        new RetrofitCallbackPost<Account.ChangePasswordResponse>(Account.ChangePasswordResponse.class, bus) {
            @Override
            protected void onResponse(Account.ChangePasswordResponse response) {
                if (response.didSucceed()) {
                }
                super.onResponse(response);
            }
        });
    }

    @Subscribe
    public void loginWithExternalToken(Account.LoginWithExternalTokenRequest request) {
        api.loginWithExternalToken(request, new RetrofitCallbackPost<Account.LoginWithExternalTokenResponse>(Account.LoginWithExternalTokenResponse.class, bus) {
            @Override
            protected void onResponse(Account.LoginWithExternalTokenResponse response) {
                if (response.didSucceed()) {
                    loginUser(response);
                }

                super.onResponse(response);
            }
        });
    }

    @Subscribe
    public void registerWithExternalToken(Account.RegisterWithExternalTokenRequest request) {
        api.registerExternal(request, new RetrofitCallbackPost<Account.RegisterWithExternalTokenResponse>(Account.RegisterWithExternalTokenResponse.class, bus) {
            @Override
            protected void onResponse(Account.RegisterWithExternalTokenResponse response) {
                if (response.didSucceed()){
                    loginUser(response);
                }

                super.onResponse(response);
            }
        });
    }

    @Subscribe
    public void registerGcm(Account.UpdateGcmRegistrationRequest request) {
        api.updateGcmRegistration(request, new RetrofitCallbackPost<>(Account.UpdateGcmRegistrationResponse.class, bus));
    }

    protected void loginUser(Account.UserResponse response) {
        if (response.authToken != null && !response.authToken.isEmpty()) {
            auth.setAuthToken(response.authToken);
        }

        User user = auth.getUser();
        user.setId(response.id);
        user.setDisplayName(response.displayName);
        user.setUserName(response.userName);
        user.setEmail(response.email);
        user.setAvatarUrl(response.avatarUrl);
        user.setHasPassword(response.hasPassword);
        user.setIsLoggedIn(true);

        bus.post(new Account.UserDetailsUpdateEvent(user));
    }
}
