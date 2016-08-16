// @flow

import { NativeModules, DeviceEventEmitter} from "react-native";
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
   * Emits an event with some payload to the native side
   * @param {string} type - The type of the event
   * @param {Object} payload - The event payload
   */
  emitEventToNative(type: string, payload: Object = {}) {
    NativeModules.ElectrodeBridge.dispatchEvent(type, uuid.v4(), payload);
  }

  /**
   * Sends a request without any payload to the native side
   * @param {string} type - The type of the request
   * @param {Object} payload - The request payload [Default: {}]
   * @param {number} timeout - Request timeout delay in milliseconds [Default: 5000]
   */
  sendRequestToNative(type: string,
                      payload: Object = {},
                      timeout: number = DEFAULT_REQUEST_TIMEOUT_IN_MS) {
    const requestPromise = NativeModules.ElectrodeBridge
      .dispatchRequest(type, uuid.v4(), payload);

    const timeoutPromise = new Promise((resolve, reject) => {
      setTimeout(reject, timeout, {
        code: "EREQUESTIMEOUT",
        message: "Request timeout"
      });
    });

    return Promise.race([requestPromise, timeoutPromise]);
  }

  /**
   * Called whenever an event from the native side has been received
   * @param {Object} event - The raw event received
   */
  _onEventFromNative(event: Object) {
    this.dispatchEvent(event.type, event.id, event.data);
  }

  /**
   * Dispatch an event to the react native JS side
   * @param {string} type - The type of the event
   * @param {string} id - The event id
   * @param {Object} payload - The event payload
   */
  dispatchEvent(type: string, id: string, payload: Object) {
    this.emit(type, payload);
  }

  /**
   * Called whenever a request from the native side has been received
   * @param {Object} request - The raw request received
   */
  _onRequestFromNative(request: Object) {
    this.dispatchRequest(request.type, request.id, request.data);
  }

  /**
   * Dispatch a request to the react native JS side
   * @param {string} type - The type of the request
   * @param {string} id - The request id
   * @param {Object} payload - The payload of the request
   */
  dispatchRequest(type: string, id: string, payload: Object) {
    this.requestHandlerByRequestType[type](payload)
      .then((data) => {
        this.emitEventToNative(ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE, { id, data });
      })
      .catch((err) => {
        const error = { code: err.code, message: err.message };
        this.emitEventToNative(ELECTRODE_BRIDGE_RESPONSE_EVENT_TYPE, { id, error });
      });
  }

  /**
   * Registers a request handler for a given request type
   * @param {string} type - The type of request associated to the handler
   * @param {Function} handler - The handler function
   */
  registerRequestHandler(type: string, handler: Function) {
    if (this.requestHandlerByRequestType[type]) {
      throw new Error(`A handler is already registered for type ${type}`);
    }

    this.requestHandlerByRequestType[type] = handler;
  }
}

export const electrodeBridge = new ElectrodeBridge();
