// @flow

import { NativeModules, DeviceEventEmitter } from "react-native";
import uuid from "uuid";
var EventEmitter = require('events');

const ELECTRODE_BRIDGE_EVENT_EVENT_NAME = "electrode.bridge.event";
const ELECTRODE_BRIDGE_REQUEST_EVENT_NAME = "electrode.bridge.request";
const ELECTRODE_BRIDGE_RESPONSE_EVENT_NAME = "electrode.bridge.response";

const DEFAULT_REQUEST_TIMEOUT_IN_MS = 5000;

const ERROR_REQUEST_TIMEOUT = {
    code: "EREQUESTIMEOUT",
    message: "Request timeout"
}

const ERROR_NO_REQUEST_HANDLER = {
    code: "ENOHANDLER",
    message: `No registered request handler`
}

class ElectrodeBridge extends EventEmitter {

    constructor() {
        super();
        const emitter = Platform.OS == 'ios' ? NativeAppEventEmitter : DeviceEventEmitter;
        emitter.addListener(ELECTRODE_BRIDGE_EVENT_EVENT_NAME,
            this._onEventFromNative.bind(this));
        emitter.addListener(ELECTRODE_BRIDGE_REQUEST_EVENT_NAME,
            this._onRequestFromNative.bind(this));

        this.requestHandlerByRequestName = {};
    }

    //============================================================================
    // PUBLIC API
    //============================================================================

    /**
     * Emits an event
     *
     * @param {string} name - The name of the event to emit
     * @param {Object} obj - Options
     * @param {Object} obj.data - The data attached to this event [DEFAULT : {}]
     * @param {number} obj.dispatchMode - The dispatch mode [DEFAULT: DispatchMode.NATIVE]
     */
    emitEvent(
        name /*: string */ , {
            data = {},
            dispatchMode = DispatchMode.NATIVE
        } = {} /*: Object */ ) {
        switch (dispatchMode) {
            case DispatchMode.NATIVE:
                NativeModules.ElectrodeBridge.dispatchEvent(name, uuid.v4(), data);
                break;
            case DispatchMode.JS:
                this.emit(name, data);
                break;
            case DispatchMode.GLOBAL:
                NativeModules.ElectrodeBridge.dispatchEvent(name, uuid.v4(), data);
                this.emit(name, data);
                break;
        }
    }

    /**
     * Sends a request
     *
     * @param {string} name - The name of the request to send
     * @param {Object} obj - Options
     * @param {Object} obj.data - The data attached to this request [DEFAULT: {}]
     * @param {number} obj.timeout - The timeout of the request in ms [DEFAULT:5000]
     * @param {number} obj.dispatchMode - The dispatch mode [DEFAULT : DispatchMode.NATIVE]
     */
    sendRequest(
        name /*: string */ , {
            data = {},
            timeout = DEFAULT_REQUEST_TIMEOUT_IN_MS,
            dispatchMode = DispatchMode.NATIVE
        } = {} /*: Object */ ) /*: Promise<*> */ {
        let requestPromise;
        switch (dispatchMode) {
            case DispatchMode.NATIVE:
                requestPromise = NativeModules.ElectrodeBridge.dispatchRequest(name, uuid.v4(), data);
                break;
            case DispatchMode.JS:
                requestPromise = this._dispatchJsOriginatingRequest(name, uuid.v4(), data);
                break;
            default:
                throw new Error(`Unknown dispatchMode : ${dispatchMode}`);
        }

        const timeoutPromise = new Promise((resolve, reject) => {
            setTimeout(reject, timeout, ERROR_REQUEST_TIMEOUT);
        });

        return Promise.race([requestPromise, timeoutPromise]);
    }

    /**
     * Registers a request handler for a given request name
     *
     * @param {string} name - The name of the request associated to the handler
     * @param {Function} handler - The handler promise
     */
    registerRequestHandler(
        name /*: string */ ,
        handler /*: Promise<*> */ ) {
        if (this.requestHandlerByRequestName[name]) {
            throw new Error(`A handler is already registered for request name ${name}`);
        }

        this.requestHandlerByRequestName[name] = handler;
    }

    /**
     * Registers an event listener for a given event name
     *
     * @param {string} name - The name of the event
     * @param {Function} handler - A function to handle incoming events having this name
     */
    registerEventListener(
        name /*:string */ ,
        handler /*:Function*/ ) {
        this.addListener(name, handler);
    }

    //============================================================================
    // PRIVATE METHODS
    //============================================================================

    /**
     * Called whenever an event from the native side has been received
     *
     * @param {Object} event - The raw event received
     */
    _onEventFromNative(event /*: ElectrodeBridgeMessage */ ) {
        this.emit(event.name, event.data);
    }

    /**
     * Called whenever a request from the native side has been received
     *
     * @param {Object} request - The raw request received
     */
    _onRequestFromNative(request /*: ElectrodeBridgeMessage */ ) {
        this._dispatchNativeOriginatingRequest(request.name, request.id, request.data);
    }

    /**
     * Dispatch a request originating from the native side
     *
     * @param {string} name - The name of the request
     * @param {string} id - The request id
     * @param {Object} data - The data associated to the request
     */
    _dispatchNativeOriginatingRequest(
        name /*: string */ ,
        id /*: string */ ,
        data /*: Object */ ) {
        if (!this.requestHandlerByRequestName[name]) {
            this._sendErrorResponseToNative(id, ERROR_NO_REQUEST_HANDLER);
            return;
        }

        this.requestHandlerByRequestName[name](data)
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
     * @param {string} name - The name of the request
     * @param {string} id - The request id
     * @param {Object} data - The data associated to the request
     */
    _dispatchJsOriginatingRequest(
        name /* : string */ ,
        id /*: string */ ,
        data /*: Object */ ) {
        if (!this.requestHandlerByRequestName[name]) {
            throw ERROR_NO_REQUEST_HANDLER;
        }
        return this.requestHandlerByRequestName[name](data);
    }

    /**
     * Sends a success response to the native side for a given request
     *
     * @param {string} requestId - The id of the request associated to this response
     * @param {Object} data - The response data
     */
    _sendSuccessResponseToNative(
        requestId /*: string */ ,
        data /*: Object*/ ) {
        NativeModules.ElectrodeBridge.dispatchEvent(
            ELECTRODE_BRIDGE_RESPONSE_EVENT_NAME,
            uuid.v4(), { requestId, data });
    }

    /**
     * Sends an error response to the native for a given request
     *
     * @param {string} requestId - The id of the request associated to this response
     * @param {Object} error - The response error
     */
    _sendErrorResponseToNative(
        requestId /*: string */ ,
        error /*: ElectrodeBridgeError */ ) {
        NativeModules.ElectrodeBridge.dispatchEvent(
            ELECTRODE_BRIDGE_RESPONSE_EVENT_NAME,
            uuid.v4(), { requestId, error });
    }
}

type ElectrodeBridgeMessage = {
    id: string;
    name: string;
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
