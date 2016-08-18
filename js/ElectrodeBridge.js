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

  /**
   * Emits an event
   *
   * @param {string} type - The type of the event to emit
   * @param {Object} obj - Options
   * @param {Object} obj.data - The data of this event [DEFAULT : {}]
   * @param {number} obj.dispatchMode - The dispatch mode [DEFAULT: DispatchMode.NATIVE]
   */
  emitEvent(
      type /*: string */, {
      data = {},
      dispatchMode = DispatchMode.NATIVE
    } /*: Object */) {
    const id = uuid.v4();
    switch (dispatchMode) {
      case DispatchMode.NATIVE:
        NativeModules.ElectrodeBridge.dispatchEvent(type, id, data);
        break;
      case DispatchMode.JS:
        this.emit(type, data);
        break;
      case DispatchMode.GLOBAL:
        NativeModules.ElectrodeBridge.dispatchEvent(type, id, data);
        this.emit(type, data);
        break;
    }
  }

  /**
   * Sends a request
   *
   * @param {string} type - The type of the request to send
   * @param {Object} obj - Options
   * @param {Object} obj.data - The data of this request [DEFAULT: {}]
   * @param {number} obj.timeout - The timeout of the request in ms [DEFAULT:5000]
   * @param {number} obj.dispatchMode - The dispatch mode [DEFAULT : DispatchMode.NATIVE]
   */
  sendRequest(
      type /*: string */, {
      data = {},
      timeout = DEFAULT_REQUEST_TIMEOUT_IN_MS,
      dispatchMode = DispatchMode.NATIVE
    } /*: Object */) /*: Promise<*> */ {
    let requestPromise;
    switch (dispatchMode) {
      case DispatchMode.NATIVE:
        requestPromise = NativeModules.ElectrodeBridge.dispatchRequest(type, uuid.v4(), data);
        break;
      case DispatchMode.JS:
        requestPromise = this._dispatchJsOriginatingRequest(type, uuid.v4(), data);
        break;
      default:
        throw new Error(`Unknown dispatchMode : ${dispatchMode}`);
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
  registerRequestHandler(
    type /*: string */,
    handler /*: Promise<*> */) {
    if (this.requestHandlerByRequestType[type]) {
      throw new Error(`A handler is already registered for type ${type}`);
    }

    this.requestHandlerByRequestType[type] = handler;
  }

  /**
   * Called whenever an event from the native side has been received
   *
   * @param {Object} event - The raw event received
   */
  _onEventFromNative(event /*: ElectrodeBridgeMessage */) {
    this.emit(event.type, event.data);
  }

  /**
   * Called whenever a request from the native side has been received
   *
   * @param {Object} request - The raw request received
   */
  _onRequestFromNative(request /*: ElectrodeBridgeMessage */) {
    this._dispatchNativeOriginatingRequest(request.type, request.id, request.data);
  }

  /**
   * Dispatch a request originating from the native side
   *
   * @param {string} type - The type of the request
   * @param {string} id - The request id
   * @param {Object} data - The data associated to the request
   */
  _dispatchNativeOriginatingRequest(
    type /*: string */,
    id /*: string */,
    data /*: Object */) {
    if (!this.requestHandlerByRequestType[type]) {
      this._sendErrorResponseToNative(id, {
        code: "ENOHANDLER",
        message: `No registered request handler for type ${type}`
      });
      return;
    }

    this.requestHandlerByRequestType[type](data)
      .then((data) => {
        this._sendSuccessResponseToNative(id, data)
      })
      .catch((err) => {
        this._sendErrorResponseToNative(id, {
          code: err.code,
          message: err.message
        });
      });
  }

  /**
   * Dispatch a request originating from the JS side
   *
   * @param {string} type - The type of the request
   * @param {string} id - The request id
   * @param {Object} data - The data associated to the request
   */
  _dispatchJsOriginatingRequest(
    type /* : string */,
    id /*: string */,
    data /*: Object */) {
    if (!this.requestHandlerByRequestType[type]) {
      throw {
        code:"ENOHANDLER",
        message: `No registered request handler for type ${type}`
      };
    }
    return this.requestHandlerByRequestType[type](data);
  }

  /**
   * Sends a success response to the native side for a given request
   *
   * @param {string} requestId - The id of the request associated to this response
   * @param {Object} data - The response data
   */
  _sendSuccessResponseToNative(
    requestId /*: string */,
    data /*: Object*/) {
    NativeModules.ElectrodeBridge.dispatchEvent(
      ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE,
      uuid.v4(),
      { requestId, data });
  }

  /**
   * Sends an error response to the native for a given request
   *
   * @param {string} requestId - The id of the request associated to this response
   * @param {Object} error - The response error
   */
  _sendErrorResponseToNative(
    requestId /*: string */,
    error /*: ElectrodeBridgeError */) {
    NativeModules.ElectrodeBridge.dispatchEvent(
      ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE,
      uuid.v4(),
      { requestId, error });
  }
}

type ElectrodeBridgeMessage = {
  id: string;
  type: string;
  data: Object;
};

type ElectrodeBridgeError = {
  code: string;
  message: string;
};

export const DispatchMode = {
  NATIVE: 0,
  JS: 1,
  GLOBAL: 2
};

export const electrodeBridge = new ElectrodeBridge();
