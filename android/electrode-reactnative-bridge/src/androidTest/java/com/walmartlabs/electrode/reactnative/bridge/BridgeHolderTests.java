package com.walmartlabs.electrode.reactnative.bridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class BridgeHolderTests extends BaseBridgeTestCase {

    @Before
    public void setUp() {
        Logger.overrideLogLevel(Logger.LogLevel.DEBUG);
    }

    @Test
    public void testRequestHandlerQueuing() {
        String KEY_HANDLER = "requestHandler";

        ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler1 = new ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object>() {
            @Override
            public void onRequest(@Nullable ElectrodeBridgeRequest payload, @NonNull ElectrodeBridgeResponseListener<Object> responseListener) {

            }
        };

        UUID requestHandler1Uuid = ElectrodeBridgeHolder.registerRequestHandler(KEY_HANDLER, requestHandler1);
        assertEquals(1, ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.size());
        assertTrue(ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.containsKey(KEY_HANDLER));
        assertNotNull(ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.get(KEY_HANDLER));
        assertEquals(requestHandler1, ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.get(KEY_HANDLER).getRequestHandler());


        ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler2 = new ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object>() {
            @Override
            public void onRequest(@Nullable ElectrodeBridgeRequest payload, @NonNull ElectrodeBridgeResponseListener<Object> responseListener) {

            }
        };

        //Add second request handler, this should replace the first one.
        UUID requestHandler2Uuid = ElectrodeBridgeHolder.registerRequestHandler(KEY_HANDLER, requestHandler2);
        assertEquals(1, ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.size());
        assertTrue(ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.containsKey(KEY_HANDLER));
        assertNotNull(ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.get(KEY_HANDLER));
        assertEquals(requestHandler2, ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.get(KEY_HANDLER).getRequestHandler());


        //Try removing the first request handler which is already replaced by second
        assertNull(ElectrodeBridgeHolder.unregisterRequestHandler(requestHandler1Uuid));

        //Remove the second request handler
        assertEquals(requestHandler2, ElectrodeBridgeHolder.unregisterRequestHandler(requestHandler2Uuid));
        assertEquals(0, ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.size());
        assertFalse(ElectrodeBridgeHolder.mQueuedRequestHandlersRegistration.containsKey(KEY_HANDLER));
    }

    @Test
    public void testEventListenerQueuing() {
        String KEY_LISTENER = "eventListener";
        ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener1 = new ElectrodeBridgeEventListener<ElectrodeBridgeEvent>() {
            @Override
            public void onEvent(@Nullable ElectrodeBridgeEvent eventPayload) {

            }
        };

        UUID eventListener1Uuid = ElectrodeBridgeHolder.addEventListener(KEY_LISTENER, eventListener1);

        assertEquals(1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.size());
        assertTrue(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.containsKey(KEY_LISTENER));
        assertNotNull(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER));
        assertEquals(1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).size());
        assertEquals(eventListener1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).get(0).getEventListener());


        //Add the second listener
        ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener2 = new ElectrodeBridgeEventListener<ElectrodeBridgeEvent>() {
            @Override
            public void onEvent(@Nullable ElectrodeBridgeEvent eventPayload) {
            }
        };

        UUID eventListener2Uuid = ElectrodeBridgeHolder.addEventListener(KEY_LISTENER, eventListener2);
        assertEquals(1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.size());
        assertTrue(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.containsKey(KEY_LISTENER));
        assertNotNull(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER));
        assertEquals(2, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).size());
        assertEquals(eventListener1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).get(0).getEventListener());
        assertEquals(eventListener2, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).get(1).getEventListener());

        //Remove one listener
        assertEquals(eventListener2, ElectrodeBridgeHolder.removeEventListener(eventListener2Uuid));
        assertEquals(1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.size());
        assertTrue(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.containsKey(KEY_LISTENER));
        assertNotNull(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER));
        assertEquals(1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).size());
        assertEquals(eventListener1, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER).get(0).getEventListener());

        //Remove all listeners
        assertEquals(eventListener1, ElectrodeBridgeHolder.removeEventListener(eventListener1Uuid));
        assertEquals(0, ElectrodeBridgeHolder.mQueuedEventListenersRegistration.size());
        assertFalse(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.containsKey(KEY_LISTENER));
        assertNull(ElectrodeBridgeHolder.mQueuedEventListenersRegistration.get(KEY_LISTENER));
    }

}
