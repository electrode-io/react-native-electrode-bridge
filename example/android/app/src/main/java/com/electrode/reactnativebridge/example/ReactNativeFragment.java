package com.electrode.reactnativebridge.example;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.common.logging.FLog;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

public class ReactNativeFragment extends Fragment implements DefaultHardwareBackBtnHandler {

    private static final String REDBOX_PERMISSION_MESSAGE =
            "Overlay permissions needs to be granted in order for react native apps to run in dev mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            // Get permission to show redbox in dev builds.
            if (!Settings.canDrawOverlays(getContext())) {
                Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(serviceIntent);
                FLog.w(ReactConstants.TAG, REDBOX_PERMISSION_MESSAGE);
                Toast.makeText(getContext(), REDBOX_PERMISSION_MESSAGE, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ReactRootView reactRootView = new ReactRootView(getActivity().getApplicationContext());
        reactRootView.startReactApplication(
                ((ReactApplication)getActivity().getApplication()).getReactNativeHost().getReactInstanceManager(),
                "ElectrodeBridgeExample",
                null);
        return reactRootView;
    }

    private ReactNativeHost getReactNativeHost() {
        return ((ReactApplication)getActivity().getApplication()).getReactNativeHost();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostResume(getActivity(), this);
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        getActivity().onBackPressed();
    }
}
