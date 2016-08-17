package com.electrode.reactnativebridge.example;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button mResolveRequestButton;
    private Button mRejectRequestButton;
    private TextView mLoggerTextView;
    private ElectrodeBridge mElectrodeBridge;
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
                mLoggerTextView.setText(String.format(">>> %s", text));
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

    private void showRequestCompletionButtons(final RequestDispatcherImpl.RequestCompletioner requestCompletioner) {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.main_native_activity, container, false);

        mSendRequestWithDataButton = (Button)v.findViewById(R.id.button_send_request_with_data);
        mSendRequestWithoutDataButton = (Button)v.findViewById(R.id.button_send_request_wo_data);
        mSendEventWithDataButton = (Button)v.findViewById(R.id.button_send_event_with_data);
        mSendEventWithoutDataButton = (Button)v.findViewById(R.id.button_send_event_wo_data);
        mLoggerTextView = (TextView)v.findViewById(R.id.tv_logger);

        mResolveRequestButton = (Button)v.findViewById(R.id.button_resolve_request);
        mRejectRequestButton = (Button)v.findViewById(R.id.button_reject_request);

        mSendRequestWithDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    setRequestButtonsEnabled(false);
                    Bundle data = new Bundle();
                    data.putInt("randInt", mRand.nextInt());

                    ElectrodeBridgeRequest request =
                        new ElectrodeBridgeRequest.Builder(
                                REQUEST_EXAMPLE_TYPE,
                                mCompletionListener)
                                .withData(data)
                                .withTimeout(SAMPLE_REQUEST_TIMEOUT_IN_MS)
                                .build();

                    mElectrodeBridge.sendRequestToJs(request);
                }
            }
        });

        mSendRequestWithoutDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    ElectrodeBridgeRequest request =
                            new ElectrodeBridgeRequest.Builder(
                                    REQUEST_EXAMPLE_TYPE,
                                    mCompletionListener)
                                    .withTimeout(SAMPLE_REQUEST_TIMEOUT_IN_MS)
                                    .build();

                    mElectrodeBridge.sendRequestToJs(request);
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
                            .build();

                    mElectrodeBridge.emitEventToJs(event);
                }
            }
        });

        mSendEventWithoutDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    ElectrodeBridgeEvent event = new ElectrodeBridgeEvent.Builder(
                            EVENT_EXAMPLE_TYPE)
                            .build();

                    mElectrodeBridge.emitEventToJs(event);
                }
            }
        });

        return v;
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
