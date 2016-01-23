package com.spb.kbv.messageapp.infrastructure;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.TreeMap;

public abstract class ServiceResponse {
    private static final String TAG = "ServiceResponse";

    @SerializedName("operationError")
    private String operationError;

    @SerializedName("propertyErrors")
    private HashMap<String, String> propertyErrors;

    private boolean isCritical;
    private TreeMap<String, String> propertyErrorsCaseInsensitive;

    public ServiceResponse(){
        propertyErrors = new HashMap<>();
    }

    public ServiceResponse(String operationError){
        this.operationError = operationError;
    }

    public ServiceResponse(String operationError, boolean isCritical){
        this.operationError = operationError;
        this.isCritical = isCritical;
    }

    public String getOperationError(){
        return operationError;
    }

    public void setOperationError(String operationError){
        this.operationError = operationError;
    }

    public boolean isCritical(){
        return isCritical;
    }

    public void setIsCritical(boolean isCritical){
        this.isCritical = isCritical;
    }

    public void setCriticalError(String criticalError){
        isCritical = true;
        operationError = criticalError;
    }

    public void setPropertyError(String property, String error){
        propertyErrors.put(property, error);
    }

    public String getPropertyError(String property){
        if (propertyErrorsCaseInsensitive == null || propertyErrorsCaseInsensitive.size() != propertyErrors.size()){
            propertyErrorsCaseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            propertyErrorsCaseInsensitive.putAll(propertyErrors);
        }
        return propertyErrorsCaseInsensitive.get(property);
    }

    public boolean didSucceed(){
        return (operationError == null || operationError.isEmpty()) && (propertyErrors.size() == 0);
    }

    public void showErrorToast(Context context){
        if (context == null || operationError == null || operationError.isEmpty())
            return;
        try {
            Toast.makeText(context, operationError, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Can't create error toast", e);
        }
    }



}
