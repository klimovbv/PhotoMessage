package com.spb.kbv.messageapp.services;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.spb.kbv.messageapp.infrastructure.ServiceResponse;
import com.spb.kbv.messageapp.infrastructure.User;

public final class Account {
    private Account(){
    }

    public static class UserResponse extends ServiceResponse{

        @SerializedName("username")
        public String userName;

        @SerializedName("email")
        public String email;

        @SerializedName("token")
        public String authToken;

        @SerializedName("displayName")
        public String displayName;

        public String id;

        @SerializedName("avatarUrl")
        public String avatarUrl;

        public boolean hasPassword;
    }

    public static class LoginWithUsernameRequest {
        public String userName;
        public String password;

        public LoginWithUsernameRequest(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }

    public static class LoginWithUsernameResponse extends ServiceResponse {
    }

    public static class LoginWithLocalTokenRequest {
        public String authToken;

        public LoginWithLocalTokenRequest(String authToken) {
            this.authToken = authToken;
        }
    }

    public static class LoginWithLocalTokenResponse extends UserResponse {
    }

    public static class RegisterRequest {
        public String userName;
        public String email;
        public String password;

        public RegisterRequest(String userName, String email, String password) {
            this.userName = userName;
            this.email = email;
            this.password = password;
        }
    }

    public static class RegisterResponse extends UserResponse {
    }

    public static class RegisterWithExternalTokenRequest {
        public String userName;
        public String email;
        public String provider;
        public String token;
        public String clientId;

        public RegisterWithExternalTokenRequest(String userName, String email, String provider, String token) {
            this.userName = userName;
            this.email = email;
            this.provider = provider;
            this.token = token;
            clientId = "android";
        }
    }

    public static class RegisterWithExternalTokenResponse extends UserResponse{
    }

    public static class ChangeAvatarRequest{
        public Uri newAvatarUri;

        public ChangeAvatarRequest(Uri newAvatarUri){
            this.newAvatarUri = newAvatarUri;
        }
    }

    public static class ChangeAvatarResponse extends ServiceResponse {
        @SerializedName("url")
        public String avatarUrl;
    }


    public static class LoginWithExternalTokenRequest {
        public String provider;
        public String token;
        public String clientId;

        public LoginWithExternalTokenRequest(String provider, String token) {
            this.provider = provider;
            this.token = token;
            clientId = "android";
        }
    }

    public static class LoginWithExternalTokenResponse extends UserResponse {
    }



    public static class UpdateProfileRequest{
        public String displayName;
        public String email;

        public UpdateProfileRequest(String displayName, String email){
            this.displayName = displayName;
            this.email = email;
        }
    }
    public static class UpdateProfileResponse extends ServiceResponse {

        @SerializedName("displayName")
        public String displayName;

        @SerializedName("email")
        public String email;
    }

    public static class ChangePasswordRequest {
        public String newPassword;

        public ChangePasswordRequest(String newPassword){
            this.newPassword = newPassword;
        }

    }
    public static class ChangePasswordResponse extends ServiceResponse {
    }

    public static class UserDetailsUpdateEvent {
        public User user;

        public UserDetailsUpdateEvent(User user) {
            this.user = user;
        }
    }

    public static class AvatarUpdateEvent {
        public Uri uri;
        public AvatarUpdateEvent(Uri newAvatarUri) {
            uri = newAvatarUri;
        }
    }

    public static class UpdateGcmRegistrationRequest {
        public String registrationId;

        public UpdateGcmRegistrationRequest(String registrationId) {
            this.registrationId = registrationId;
        }
    }

    public static class UpdateGcmRegistrationResponse extends ServiceResponse{

    }
}
