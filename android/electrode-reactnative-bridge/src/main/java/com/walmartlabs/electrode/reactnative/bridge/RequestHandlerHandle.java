package com.walmartlabs.electrode.reactnative.bridge;

/**
 * Interface that is returned by the request handler processors when a request is being registered.
 * <p>
 * Use this handle to properly unregister the request handler when not in use.
 */
public interface RequestHandlerHandle {
    /**
     * Unregisters a request handler.
     *
     * @return
     */
    boolean unregister();
}
