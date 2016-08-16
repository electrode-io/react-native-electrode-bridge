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

    // Inbound event and request types
    static final String REACT_NATIVE_EVENT_EXAMPLE_TYPE = "reactnative.event.example";
    static final String REACT_NATIVE_REQUEST_EXAMPLE_TYPE = "reactnative.request.example";

    // Outbound event and request types
    static final String NATIVE_REQUEST_EXAMPLE_TYPE = "native.request.example";
    static final String NATIVE_EVENT_EXAMPLE_TYPE = "native.event.example";

    static final int SAMPLE_REQUEST_TIMEOUT_IN_MS = 8000;

    final RequestCompletionListener mCompletionListener = new ExampleRequestCompletionListener();

    private Button mSendRequestWithPayloadButton;
    private Button mSendRequestWithoutPayloadButton;
    private Button mSendEventWithPayloadButton;
    private Button mSendEventWithoutPayloadButton;
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
                        .registerEventListener(REACT_NATIVE_EVENT_EXAMPLE_TYPE,
                                new EventDispatcherImpl.EventListener() {
                    @Override
                    public void onEvent(final Bundle payload) {
                        setLoggerText("Event received. Payload : " + payload.toString());
                    }
                });

                try {
                    electrodeBridge
                            .requestRegistrar()
                            .registerRequestHandler(REACT_NATIVE_REQUEST_EXAMPLE_TYPE,
                                    new RequestDispatcherImpl.RequestHandler() {
                        @Override
                        public void onRequest(Bundle payload,
                                              RequestDispatcherImpl.RequestCompletioner requestCompletioner) {
                            setLoggerText("Request received. Payload : " + payload.toString());
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
                mLoggerTextView.setText(">>> " + text);
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

        mSendRequestWithPayloadButton = (Button)v.findViewById(R.id.button_send_request_with_payload);
        mSendRequestWithoutPayloadButton = (Button)v.findViewById(R.id.button_send_request_wo_payload);
        mSendEventWithPayloadButton = (Button)v.findViewById(R.id.button_send_event_with_payload);
        mSendEventWithoutPayloadButton = (Button)v.findViewById(R.id.button_send_event_wo_payload);
        mLoggerTextView = (TextView)v.findViewById(R.id.tv_logger);

        mResolveRequestButton = (Button)v.findViewById(R.id.button_resolve_request);
        mRejectRequestButton = (Button)v.findViewById(R.id.button_reject_request);

        mSendRequestWithPayloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    setRequestButtonsEnabled(false);
                    Bundle payload = new Bundle();
                    payload.putString("Hello", "World");

                    ElectrodeBridgeRequest request =
                        new ElectrodeBridgeRequest.Builder(
                                NATIVE_REQUEST_EXAMPLE_TYPE,
                                mCompletionListener)
                                .withPayload(payload)
                                .withTimeout(SAMPLE_REQUEST_TIMEOUT_IN_MS)
                                .build();

                    mElectrodeBridge.sendRequestToJs(request);
                }
            }
        });

        mSendRequestWithoutPayloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    ElectrodeBridgeRequest request =
                            new ElectrodeBridgeRequest.Builder(
                                    NATIVE_REQUEST_EXAMPLE_TYPE,
                                    mCompletionListener)
                                    .withTimeout(SAMPLE_REQUEST_TIMEOUT_IN_MS)
                                    .build();

                    mElectrodeBridge.sendRequestToJs(request);
                }
            }
        });

        mSendEventWithPayloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    Bundle payload = new Bundle();
                    payload.putInt("randint", mRand.nextInt());

                    ElectrodeBridgeEvent event = new ElectrodeBridgeEvent.Builder(
                            NATIVE_EVENT_EXAMPLE_TYPE)
                            .withPayload(payload)
                            .build();

                    mElectrodeBridge.emitEventToJs(event);
                }
            }
        });

        mSendEventWithoutPayloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElectrodeBridge != null) {
                    ElectrodeBridgeEvent event = new ElectrodeBridgeEvent.Builder(
                            NATIVE_EVENT_EXAMPLE_TYPE)
                            .build();

                    mElectrodeBridge.emitEventToJs(event);
                }
            }
        });

        return v;
    }

    private class ExampleRequestCompletionListener implements RequestCompletionListener {
        @Override
        public void onSuccess(@NonNull Bundle payload) {
            setLoggerText("Response success. Payload : " + payload.toString());
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
                mSendRequestWithoutPayloadButton.setEnabled(enabled);
                mSendRequestWithPayloadButton.setEnabled(enabled);
            }
        });
    }


}
