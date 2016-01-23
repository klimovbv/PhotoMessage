package com.spb.kbv.messageapp.fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.spb.kbv.messageapp.services.Account;
import com.squareup.otto.Subscribe;

public class RegisterGcmFragment extends BaseFragment {
    private final static String TAG = "RegisterGcmFragment";

    private static final int PLAY_SERVICES_UNABLABLE = 0;
    private static final int PLAY_SERVICES_SHOWING_DIALOG = 1;
    private static final int PLAY_SERVICES_AVAILABLE = 2;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final static String PROPERTY_REGISTRATION_ID = "REGISTRATION_ID";
    private final static String PROPERTY_APP_VERSION = "APP_VERSION";
    private static final String SENDER_ID = "564017263991";

    public static RegisterGcmFragment get(GcmRegistrationCallback callback, boolean isNewUser, FragmentManager fragmentManager){
        RegisterGcmFragment fragment = (RegisterGcmFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment != null){
            return fragment;
        }

        fragment = new RegisterGcmFragment();
        fragment.currentCallback = callback;
        fragment.isNewUser = isNewUser;
        fragmentManager.beginTransaction().add(fragment, TAG).commit();
        return fragment;
    }

    private GoogleCloudMessaging cloudMessaging;
    private GcmRegistrationCallback currentCallback;
    private SharedPreferences sharedPreferences;
    private boolean isNewUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cloudMessaging = GoogleCloudMessaging.getInstance(application);
        sharedPreferences = application.getSharedPreferences(RegisterGcmFragment.class.getSimpleName(), Context.MODE_PRIVATE);

        int status = checkPlayStatus();
        if (status == PLAY_SERVICES_AVAILABLE) {
            String registrationId = gcmRegistrationId();

            if (registrationId.isEmpty()){
                doRegistration();
            } else {
                bus.post(new Account.UpdateGcmRegistrationRequest(registrationId));
                /*if (isNewUser){
                    bus.post(new Account.UpdateGcmRegistrationRequest(registrationId));
                } else {
                    currentCallback.gcmFinished();
                }*/
            }
        } else if (status == PLAY_SERVICES_UNABLABLE){
            currentCallback.gcmFinished();
        }
    }

    protected int checkPlayStatus() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (result == ConnectionResult.SUCCESS){
            return PLAY_SERVICES_AVAILABLE;
        }

        if (!GooglePlayServicesUtil.isUserRecoverableError(result)){
            return PLAY_SERVICES_UNABLABLE;
        }

        Dialog playDialog = GooglePlayServicesUtil.getErrorDialog(result, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST);
        playDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                currentCallback.gcmFinished();
            }
        });

        playDialog.show();
        return PLAY_SERVICES_SHOWING_DIALOG;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST){
            currentCallback.gcmFinished();
        }
    }

    private String gcmRegistrationId() {
        String registrationId = sharedPreferences.getString(PROPERTY_REGISTRATION_ID, "");
        if (registrationId == null || registrationId.isEmpty()){
            Log.d("myLogs", "===registrationId.isEmpty in gcmRegistrationId== " + registrationId);
            return "";
        }

        int registeredVersion = sharedPreferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion){
            Log.d("myLogs", "===registeredVersion != currentVersion== ");
            return "";
        }

        Log.d("myLogs", "===registrationId in gcmRegistrationId== " + registrationId);

        return registrationId;
    }

    private int getAppVersion() {
        try {
            return application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            throw new RuntimeException("Could not get package name", e);
        }
    }

    private void doRegistration() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    return cloudMessaging.register(SENDER_ID);
                } catch (Exception e){
                    Log.e(TAG, "Could not register", e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                updateGcmRegistration(s);
            }
        }.execute();
    }

    private void updateGcmRegistration(String registrationId) {
        Log.d("myLogs", "===registrationId in doRegistration== " + registrationId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REGISTRATION_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, getAppVersion());
        editor.apply();

        if (registrationId != null){
            bus.post(new Account.UpdateGcmRegistrationRequest(registrationId));
        } else {
            currentCallback.gcmFinished();
        }
    }

    @Subscribe
    public void gcmRegistrationUpdated(Account.UpdateGcmRegistrationResponse response) {
        Log.d("myLogs", "UpdateGcmRegistrationResponse=======");
        if (!response.didSucceed()) {
            response.showErrorToast(getActivity());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PROPERTY_REGISTRATION_ID, "");
            editor.apply();
        }

        currentCallback.gcmFinished();
    }

    public interface GcmRegistrationCallback {
        void gcmFinished();
    }
}
