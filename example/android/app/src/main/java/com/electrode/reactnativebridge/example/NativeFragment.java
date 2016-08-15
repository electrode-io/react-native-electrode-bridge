package com.electrode.reactnativebridge.example;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.PromiseImpl;
import com.walmartlabs.electrode.reactnative.bridge.DefaultEventDispatcher;
import com.walmartlabs.electrode.reactnative.bridge.DefaultRequestDispatcher;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridge;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeInstanceHolder;
import com.walmartlabs.electrode.reactnative.bridge.ExistingHandlerException;

public class NativeFragment extends Fragment {

    private static final String TAG = NativeFragment.class.getSimpleName();

    private Button mSendRequestButton;
    private Button mResolveRequestButton;
    private Button mRejectRequestButton;
    private SeekBar mSeekBar;
    private ElectrodeBridge mElectrodeBridge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ElectrodeBridgeInstanceHolder.setOnBridgeReadyListener(new ElectrodeBridgeInstanceHolder.OnBridgeReadyListener() {
            @Override
            public void onBridgeReady(ElectrodeBridge electrodeBridge) {
                mElectrodeBridge = electrodeBridge;

                electrodeBridge.eventRegistrar().registerEventListener("reactnative.seekbar.value.update", new DefaultEventDispatcher.EventListener() {
                    @Override
                    public void onEvent(final Bundle payload) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().setBackgroundColor(Color.rgb(0, (int)payload.getDouble("value"), 0));
                            }
                        });

                    }
                });

                try {
                    electrodeBridge.requestRegistrar().registerRequestHandler("coremodule.request", new DefaultRequestDispatcher.RequestHandler() {
                        @Override
                        public void onRequest(Bundle payload, DefaultRequestDispatcher.RequestCompletion requestCompletion) {
                            showRequestCompletionButtons(requestCompletion);
                        }
                    });
                } catch (ExistingHandlerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void hideRequestCompletionButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResolveRequestButton.setVisibility(View.GONE);
                mRejectRequestButton.setVisibility(View.GONE);
            }
        });
    }

    private void showRequestCompletionButtons(final DefaultRequestDispatcher.RequestCompletion requestCompletion) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResolveRequestButton.setVisibility(View.VISIBLE);
                mRejectRequestButton.setVisibility(View.VISIBLE);

                mResolveRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCompletion.success();
                        hideRequestCompletionButtons();
                    }
                });

                mRejectRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCompletion.error("Code", "Error");
                        hideRequestCompletionButtons();
                    }
                });
            }
        });
    }

    private void enableSendRequestButton() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button sendRequestButton = (Button)getView().findViewById(R.id.button_send_request);
                sendRequestButton.setEnabled(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.main_native_activity, container, false);

        mSendRequestButton = (Button)v.findViewById(R.id.button_send_request);
        mSeekBar = (SeekBar)v.findViewById(R.id.seekBar);
        mResolveRequestButton = (Button)v.findViewById(R.id.button_resolve_request);
        mRejectRequestButton = (Button)v.findViewById(R.id.button_reject_request);

        mSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    mSendRequestButton.setEnabled(false);

                    mElectrodeBridge.sendRequestToJs("coremodule.request", new Bundle(), new PromiseImpl(new Callback() {
                        @Override
                        public void invoke(Object... args) {
                            Log.d(TAG, "SUCCESS RESOLVE");
                            enableSendRequestButton();
                        }
                    }, new Callback() {
                        @Override
                        public void invoke(Object... args) {
                            Log.d(TAG, "FAILURE REJECT");
                            enableSendRequestButton();
                        }
                    }));
                }
            }
        });

        mSeekBar.setMax(255);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bundle map = new Bundle();
                map.putInt("value", progress);

                if (mElectrodeBridge != null) {
                    mElectrodeBridge.emitEventToJs("native.seekbar.value.update", map);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return v;
    }

}
