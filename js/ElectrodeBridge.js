// @flow

import { NativeModules, DeviceEventEmitter} from "react-native";
import uuid from "uuid";
const EventEmitter = require("EventEmitter");

class ElectrodeBridge extends EventEmitter {

  constructor() {
    super();

    DeviceEventEmitter.addListener("electrode.bridge.event", this._onEventFromNative.bind(this));
    DeviceEventEmitter.addListener("electrode.bridge.request", this._onRequestFromNative.bind(this));

    this.requestHandlerByRequestType = {};
  }

  /**
   * Emits an event to the native side
   * @param {string} type - The type of the event
   * @param {Object} payload - The event payload
   */
  emitEventToNative(type: string, payload: Object) {
    NativeModules.ElectrodeBridge.dispatchEvent(type, uuid.v4(), payload);
  }

  /**
   * Sends a request to the native side
   * @param {string} type - The type of the request
   * @param {Object} payload - The request payload
   */
  sendRequestToNative(type: string, payload: Object) {
    return NativeModules.ElectrodeBridge.dispatchRequest(type, uuid.v4(), payload);
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
        this.emitEventToNative("electrode.bridge.response", { id, data });
      })
      .catch((err) => {
        const error = {name: err.name, message: err.message};
        this.emitEventToNative("electrode.bridge.response", { id, error });
      });
  }

  /**
   * Registers a request handler for a given request type
   * @param {string} type - The type of request associated to the handler
   * @param {Function} handler - The handler
   */
  registerRequestHandler(type: string, handler: Function) {
    this.requestHandlerByRequestType[type] = handler;
  }
}

export const electrodeBridge = new ElectrodeBridge();
