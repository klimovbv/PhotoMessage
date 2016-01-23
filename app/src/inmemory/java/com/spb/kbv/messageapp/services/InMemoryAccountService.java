package com.spb.kbv.messageapp.services;

import com.spb.kbv.messageapp.infrastructure.Auth;
import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.infrastructure.User;
import com.squareup.otto.Subscribe;

public class InMemoryAccountService extends BaseInMemoryService {
    private MessageApplication application;

    public InMemoryAccountService (MessageApplication application){
        super(application);
        this.application = application;
    }

    @Subscribe
    public void updateProfile(final Account.UpdateProfileRequest request){
        final Account.UpdateProfileResponse response = new Account.UpdateProfileResponse();
        if (request.displayName.equals("b"))
            response.setPropertyError("displayName", "ERROR");

        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                User user = application.getAuth().getUser();
                user.setDisplayName(request.displayName);
                user.setEmail(request.email);

                bus.post(response);
                bus.post(new Account.UserDetailsUpdateEvent(user));

            }
        }, 2000, 3000);


    }

    @Subscribe
    public void updateAvatar(final Account.ChangeAvatarRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                User user = application.getAuth().getUser();
                user.setAvatarUrl(request.newAvatarUri.toString());


                bus.post(new Account.ChangeAvatarResponse());
                /*bus.post(new Account.UserDetailsUpdateEvent(user));*/
                bus.post(new Account.AvatarUpdateEvent(request.newAvatarUri));
            }
        }, 4000, 5000);
    }

    @Subscribe
    public void changePassword(Account.ChangePasswordRequest request){
        Account.ChangePasswordResponse response = new Account.ChangePasswordResponse();

        if (!request.newPassword.equals(request.confirmNewPassword)){
            response.setPropertyError("confirmNewPassword", "Passwords must match");
        }

        if (request.newPassword.length() < 3){
            response.setPropertyError("newPassword", "Password must be longer than 3 characters");
        }

        postDelayed(response);
    }

    @Subscribe
    public void loginWithUserName(final Account.LoginWithUsernameRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithUsernameResponse response = new Account.LoginWithUsernameResponse();
                if (request.userName.equals("b"))
                    response.setPropertyError("userName", "Invalid username or password");

                loginUser(new Account.UserResponse());
                bus.post(response);


            }
        }, 1000, 2000);
    }

    @Subscribe
    public void loginWithExternalToken(Account.LoginWithExternalTokenRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithExternalTokenResponse response = new Account.LoginWithExternalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void register(Account.RegisterRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.RegisterResponse response = new Account.RegisterResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void externalRegister(Account.RegisterWithExternalTokenRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.RegisterWithExternalTokenResponse response = new Account.RegisterWithExternalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void loginWithLocalToken(Account.LoginWithLocalTokenRequest request){
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                Account.LoginWithLocalTokenResponse response = new Account.LoginWithLocalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void updateGcmRegistration (Account.UpdateGcmRegistrationRequest request){
        postDelayed(new Account.UpdateGcmRegistrationResponse());
    }

    private void loginUser(Account.UserResponse response) {
        Auth auth = application.getAuth();
        User user = auth.getUser();

        user.setDisplayName("Default Display Name");
        user.setUserName("Default User Name");
        user.setEmail("default@mail.ru");
        user.setAvatarUrl("http://www.gravatar.com/avatar/1?d=identicon");
        user.setIsLoggedIn(true);
        user.setId(123);
        bus.post(new Account.UserDetailsUpdateEvent(user));

        auth.setAuthToken("fakeAuthToken");

        response.displayName = user.getDisplayName();
        response.userName = user.getUserName();
        response.email = user.getEmail();
        response.avatarUrl = user.getAvatarUrl();
        response.id = user.getId();
        response.authToken = auth.getAuthToken();
    }

}
