// @flow

import { NativeModules, DeviceEventEmitter } from "react-native";
import uuid from "uuid";
const EventEmitter = require("EventEmitter");

const ELECTRODE_BRIDGE_EVENT_EVENT_TYPE = "electrode.bridge.event";
const ELECTRODE_BRIDGE_REQUEST_EVENT_TYPE = "electrode.bridge.request";
const ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE = "electrode.bridge.response";
const DEFAULT_REQUEST_TIMEOUT_IN_MS = 5000;

class ElectrodeBridge extends EventEmitter {

  constructor() {
    super();

    DeviceEventEmitter.addListener(ELECTRODE_BRIDGE_EVENT_EVENT_TYPE,
      this._onEventFromNative.bind(this));
    DeviceEventEmitter.addListener(ELECTRODE_BRIDGE_REQUEST_EVENT_TYPE,
      this._onRequestFromNative.bind(this));

    this.requestHandlerByRequestType = {};
  }

  emitEvent(
      type: string, {
      payload = {},
      dispatchMode = EventDispatchMode.NATIVE_WITH_JS_FALLBACK
    }) {
    const id = uuid.v4();
    switch (dispatchMode) {
      case EventDispatchMode.NATIVE_WITH_JS_FALLBACK:
        NativeModules.ElectrodeBridge
          .canHandleEventType(type)
          .then((canHandleEventOnNativeSide) => {
            if (canHandleEventOnNativeSide) {
              NativeModules.ElectrodeBridge.dispatchEvent(type, id, payload);
            } else {
              this.emit(type, payload);
            }
          });
        break;
      case EventDispatchMode.JS_WITH_NATIVE_FALLBACK:
        if (this.listeners(type).length > 0) {
          this.emit(type, payload);
        } else {
          NativeModules.ElectrodeBridge.dispatchEvent(type, id, payload);
        }
        break;
      case EventDispatchMode.GLOBAL:
        this.emit(type, payload);
        NativeModules.ElectrodeBridge.dispatchEvent(type, id, payload);
        break;
    }
  }

  sendRequest(
      type: string, {
      payload = {},
      timeout= DEFAULT_REQUEST_TIMEOUT_IN_MS,
      dispatchMode = RequestDispatchMode.NATIVE_WITH_JS_FALLBACK
    }) {
    let requestPromise;

    switch (dispatchMode) {
      case RequestDispatchMode.NATIVE_WITH_JS_FALLBACK:
        requestPromise = NativeModules.ElectrodeBridge
          .canHandleRequestType(type)
          .then((canHandleRequestOnNativeSide) => {
            if (canHandleRequestOnNativeSide) {
              return NativeModules.ElectrodeBridge.dispatchRequest(type, uuid.v4(), payload);
            } else {
              return this.requestHandlerByRequestType[type](payload);
            }
          });
        break;
      case RequestDispatchMode.JS_WITH_NATIVE_FALLBACK:
        requestPromise = this.requestHandlerByRequestType[type] ?
          this.requestHandlerByRequestType[type](payload) :
          NativeModules.ElectrodeBridge.dispatchRequest(type, uuid.v4(), payload);
        break;
    }

    const timeoutPromise = new Promise((resolve, reject) => {
      setTimeout(reject, timeout, {
        code: "EREQUESTIMEOUT",
        message: "Request timeout"
      });
    });

    return Promise.race([requestPromise, timeoutPromise]);
  }

  /**
   * Registers a request handler for a given request type
   * @param {string} type - The type of request associated to the handler
   * @param {Function} handler - The handler promise
   */
  registerRequestHandler(type: string, handler: Function) {
    if (this.requestHandlerByRequestType[type]) {
      throw new Error(`A handler is already registered for type ${type}`);
    }

    this.requestHandlerByRequestType[type] = handler;
  }

  /**
   * Called whenever an event from the native side has been received
   * @param {Object} event - The raw event received
   */
  _onEventFromNative(event: Object) {
    this.emit(event.type, event.data);
  }

  /**
   * Called whenever a request from the native side has been received
   * @param {Object} request - The raw request received
   */
  _onRequestFromNative(request: Object) {
    this._dispatchNativeOriginatingRequest(request.type, request.id, request.data);
  }

  /**
   * Dispatch a request to the react native JS side
   * @param {string} type - The type of the request
   * @param {string} id - The request id
   * @param {Object} payload - The payload of the request
   */
  _dispatchNativeOriginatingRequest(type: string, id: string, payload: Object) {
    if (!this.requestHandlerByRequestType[type]) {
      this._sendErrorResponseToNative(
        "ENOHANDLER", `No registered request handler for type ${type}` );
      return;
    }

    this.requestHandlerByRequestType[type](payload)
      .then((data) => {
        this._sendSuccessResponseToNative(id, data)
      })
      .catch((err) => {
        this._sendErrorResponseToNative(err.code, err.message);
      });
  }

  _sendSuccessResponseToNative(requestId, payload) {
    NativeModules.ElectrodeBridge.dispatchEvent(
      ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE,
      uuid.v4(),
      { id: requestId, data: payload});
  }

  _sendErrorResponseToNative(requestId, code, message) {
    NativeModules.ElectrodeBridge.dispatchEvent(
      ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE,
      uuid.v4(),
      { id, error: { code, message} });
  }

  _dispatchJsOriginatingRequest(type: string, id: string, payload: Object) {
    if (!this.requestHandlerByRequestType[type]) {
      throw {
        code:"ENOHANDLER",
        message: `No registered request handler for type ${type}`
      };
    }
    return this.requestHandlerByRequestType[type](payload);
  }
}

export const RequestDispatchMode = {
  NATIVE_WITH_JS_FALLBACK: 0,
  JS_WITH_NATIVE_FALLBACK: 1
};

export const EventDispatchMode = {
  NATIVE_WITH_JS_FALLBACK: 0,
  JS_WITH_NATIVE_FALLBACK: 1,
  GLOBAL: 2
};

export const electrodeBridge = new ElectrodeBridge();
