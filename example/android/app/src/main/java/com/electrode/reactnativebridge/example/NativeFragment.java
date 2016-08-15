package com.electrode.reactnativebridge.example;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.walmartlabs.electrode.reactnative.bridge.DefaultEventDispatcher;
import com.walmartlabs.electrode.reactnative.bridge.DefaultRequestDispatcher;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridge;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeHolder;
import com.walmartlabs.electrode.reactnative.bridge.ExistingHandlerException;

public class NativeFragment extends Fragment {

    private static final String TAG = NativeFragment.class.getSimpleName();

    // Inbound event and request types
    static final String REACT_NATIVE_SEEKBAR_VALUE_UPDATE_EVENT_TYPE = "reactnative.seekbar.value.update";
    static final String REACT_NATIVE_REQUEST_EXAMPLE_TYPE = "reactnative.bridge.requestexample";

    // Outbound event and request types
    static final String NATIVE_REQUEST_EXAMPLE_TYPE = "native.bridge.requestexample";
    static final String NATIVE_SEEKBAR_VALUE_UPDATE_EVENT_TYPE = "native.seekbar.value.update";

    private Button mSendRequestButton;
    private Button mResolveRequestButton;
    private Button mRejectRequestButton;
    private SeekBar mSeekBar;
    private ElectrodeBridge mElectrodeBridge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ElectrodeBridgeHolder.setOnBridgeReadyListener(
                new ElectrodeBridgeHolder.OnBridgeReadyListener() {
            @Override
            public void onBridgeReady(ElectrodeBridge electrodeBridge) {
                mElectrodeBridge = electrodeBridge;

                electrodeBridge
                        .eventRegistrar()
                        .registerEventListener(REACT_NATIVE_SEEKBAR_VALUE_UPDATE_EVENT_TYPE,
                                new DefaultEventDispatcher.EventListener() {
                    @Override
                    public void onEvent(final Bundle payload) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().setBackgroundColor(
                                        Color.rgb(0, (int)payload.getDouble("value"), 0));
                            }
                        });

                    }
                });

                try {
                    electrodeBridge
                            .requestRegistrar()
                            .registerRequestHandler(REACT_NATIVE_REQUEST_EXAMPLE_TYPE,
                                    new DefaultRequestDispatcher.RequestHandler() {
                        @Override
                        public void onRequest(Bundle payload,
                                              DefaultRequestDispatcher.RequestCompletioner requestCompletioner) {
                            showRequestCompletionButtons(requestCompletioner);
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

    private void showRequestCompletionButtons(final DefaultRequestDispatcher.RequestCompletioner requestCompletioner) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResolveRequestButton.setVisibility(View.VISIBLE);
                mRejectRequestButton.setVisibility(View.VISIBLE);

                mResolveRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCompletioner.success();
                        hideRequestCompletionButtons();
                    }
                });

                mRejectRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCompletioner.error("ErrorCode", "ErrorMessage");
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

                    mElectrodeBridge.sendRequestToJs(
                            NATIVE_REQUEST_EXAMPLE_TYPE,
                            new Bundle(),
                            new ElectrodeBridge.RequestCompletionListener() {
                        @Override
                        public void onSuccess(@NonNull Bundle payload) {
                            Log.d(TAG, "Request was succesful");
                            enableSendRequestButton();
                        }

                        @Override
                        public void onError(@NonNull String code, @NonNull String message) {
                            Log.d(TAG, String.format("Request was rejected {code:%s,message:%s}", code, message));
                            enableSendRequestButton();
                        }
                    });
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
                    mElectrodeBridge.emitEventToJs(NATIVE_SEEKBAR_VALUE_UPDATE_EVENT_TYPE, map);
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
