package com.electrode.reactnativebridge.example;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridge;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEvent;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeHolder;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequest;
import com.walmartlabs.electrode.reactnative.bridge.EventDispatcherImpl;
import com.walmartlabs.electrode.reactnative.bridge.ExistingHandlerException;
import com.walmartlabs.electrode.reactnative.bridge.RequestCompletionListener;
import com.walmartlabs.electrode.reactnative.bridge.RequestDispatcherImpl;

import java.util.Random;

public class NativeFragment extends Fragment {

    private static final String TAG = NativeFragment.class.getSimpleName();

    static final String EVENT_EXAMPLE_TYPE = "event.example";
    static final String REQUEST_EXAMPLE_TYPE = "request.example";

    static final int SAMPLE_REQUEST_TIMEOUT_IN_MS = 8000;

    final RequestCompletionListener mCompletionListener = new ExampleRequestCompletionListener();

    private Button mSendRequestWithDataButton;
    private Button mSendRequestWithoutDataButton;
    private Button mSendEventWithDataButton;
    private Button mSendEventWithoutDataButton;
    private Button mResolveRequestWithoutDataButton;
    private Button mResolveRequestWithDataButton;
    private Button mRejectRequestButton;
    private TextView mLoggerTextView;
    private ElectrodeBridge mElectrodeBridge;
    private LinearLayout mLayoutRequestCompletion;
    private RadioGroup mRadioGroupRequest;
    private RadioGroup mRadioGroupEvent;
    private Random mRand = new Random();

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
                        .registerEventListener(EVENT_EXAMPLE_TYPE,
                                new EventDispatcherImpl.EventListener() {
                    @Override
                    public void onEvent(final Bundle data) {
                        setLoggerText(String.format("Event received. %s", data.toString()));
                    }
                });

                try {
                    electrodeBridge
                            .requestRegistrar()
                            .registerRequestHandler(REQUEST_EXAMPLE_TYPE,
                                    new RequestDispatcherImpl.RequestHandler() {
                        @Override
                        public void onRequest(Bundle data,
                                              RequestDispatcherImpl.RequestCompletioner requestCompletioner) {
                            setLoggerText(String.format("Request received. %s", data.toString()));
                            showRequestCompletionButtons(requestCompletioner);
                        }
                    });
                } catch (ExistingHandlerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLoggerText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoggerTextView.setText(String.format("[NATIVE] >>> %s", text));
            }
        });
    }

    private void hideRequestCompletionButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutRequestCompletion.setVisibility(View.GONE);
            }
        });
    }

    private void showRequestCompletionButtons(final RequestDispatcherImpl.RequestCompletioner requestCompletioner) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutRequestCompletion.setVisibility(View.VISIBLE);

                mResolveRequestWithoutDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCompletioner.success();
                        hideRequestCompletionButtons();
                    }
                });

                mResolveRequestWithDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle data = new Bundle();
                        data.putInt("randInt", mRand.nextInt(1000));
                        requestCompletioner.success(data);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.main_native_activity, container, false);

        mSendRequestWithDataButton = (Button)v.findViewById(R.id.button_send_request_with_data);
        mSendRequestWithoutDataButton = (Button)v.findViewById(R.id.button_send_request_wo_data);
        mSendEventWithDataButton = (Button)v.findViewById(R.id.button_send_event_with_data);
        mSendEventWithoutDataButton = (Button)v.findViewById(R.id.button_send_event_wo_data);
        mLoggerTextView = (TextView)v.findViewById(R.id.tv_logger);

        mResolveRequestWithoutDataButton = (Button)v.findViewById(R.id.button_resolve_request_without_data);
        mResolveRequestWithDataButton = (Button)v.findViewById(R.id.button_resolve_request_with_data);
        mRejectRequestButton = (Button)v.findViewById(R.id.button_reject_request);

        mRadioGroupRequest = (RadioGroup)v.findViewById(R.id.radio_group_request);
        mRadioGroupEvent = (RadioGroup)v.findViewById(R.id.radio_group_event);

        mLayoutRequestCompletion = (LinearLayout)v.findViewById(R.id.layout_request_completion);

        mSendRequestWithDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    setRequestButtonsEnabled(false);
                    Bundle data = new Bundle();
                    data.putInt("randInt", mRand.nextInt());

                    ElectrodeBridgeRequest request =
                        new ElectrodeBridgeRequest.Builder(
                                REQUEST_EXAMPLE_TYPE)
                                .withData(data)
                                .withTimeout(SAMPLE_REQUEST_TIMEOUT_IN_MS)
                                .withDispatchMode(getCurrentRequestDispatchMode())
                                .build();

                    mElectrodeBridge.sendRequest(request, mCompletionListener);
                }
            }
        });

        mSendRequestWithoutDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    ElectrodeBridgeRequest request =
                            new ElectrodeBridgeRequest.Builder(
                                    REQUEST_EXAMPLE_TYPE)
                                    .withTimeout(SAMPLE_REQUEST_TIMEOUT_IN_MS)
                                    .withDispatchMode(getCurrentRequestDispatchMode())
                                    .build();

                    mElectrodeBridge.sendRequest(request, mCompletionListener);
                }
            }
        });

        mSendEventWithDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    Bundle data = new Bundle();
                    data.putInt("randint", mRand.nextInt());

                    ElectrodeBridgeEvent event = new ElectrodeBridgeEvent.Builder(
                            EVENT_EXAMPLE_TYPE)
                            .withData(data)
                            .withDispatchMode(getCurrentEventDispatchMode())
                            .build();

                    mElectrodeBridge.emitEvent(event);
                }
            }
        });

        mSendEventWithoutDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    ElectrodeBridgeEvent event = new ElectrodeBridgeEvent.Builder(
                            EVENT_EXAMPLE_TYPE)
                            .withDispatchMode(getCurrentEventDispatchMode())
                            .build();

                    mElectrodeBridge.emitEvent(event);
                }
            }
        });

        return v;
    }

    private ElectrodeBridgeRequest.DispatchMode getCurrentRequestDispatchMode() {
        if (mRadioGroupRequest.getCheckedRadioButtonId() == R.id.radio_button_request_js) {
            return ElectrodeBridgeRequest.DispatchMode.JS;
        } else {
            return ElectrodeBridgeRequest.DispatchMode.NATIVE;
        }
    }

    private ElectrodeBridgeEvent.DispatchMode getCurrentEventDispatchMode() {
        if (mRadioGroupEvent.getCheckedRadioButtonId() == R.id.radio_button_event_js) {
            return ElectrodeBridgeEvent.DispatchMode.JS;
        } else if (mRadioGroupEvent.getCheckedRadioButtonId() == R.id.radio_button_event_native) {
            return ElectrodeBridgeEvent.DispatchMode.NATIVE;
        } else {
            return ElectrodeBridgeEvent.DispatchMode.GLOBAL;
        }
    }

    private class ExampleRequestCompletionListener implements RequestCompletionListener {
        @Override
        public void onSuccess(@NonNull Bundle data) {
            setLoggerText("Response success. Data : " + data.toString());
            setRequestButtonsEnabled(true);
        }

        @Override
        public void onError(@NonNull String code, @NonNull String message) {
            setLoggerText(String.format("Response failure {code:%s,message:%s}", code, message));
            setRequestButtonsEnabled(true);
        }
    }

    private void setRequestButtonsEnabled(final boolean enabled) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSendRequestWithoutDataButton.setEnabled(enabled);
                mSendRequestWithDataButton.setEnabled(enabled);
            }
        });
    }


}
