// @flow

import {
  DeviceEventEmitter,
  NativeEventEmitter,
  NativeModules,
  Platform
} from 'react-native'
import uuid from 'uuid'
var EventEmitter = require('events')

//=====================================================
// Sample message format
//
// {
//   "type": "req",
//   "id": "2689ddb2-a33b-4b44-9409-533c100a5746",
//   "name": "com.walmartlabs.sample.request",
//   "data": "a string"
// }
//
// "type", "id"" and "name are" mandatory. They'll be
// present in any message
//
// "data" is optional. It is the payload associated
// with the message. It should not be present if there
// is no payload for the message.
//
// "error" is optional. It is only used in response
// messages if the response contains an error.
// If that is the case, then "data" should not be
// specified. "error" and "data" are mutually exclusive
//=====================================================


// All messages (requests/responses/events) coming from the native side
// will be transmitted as events with the following event name
const ELECTRODE_BRIDGE_MESSAGE_EVENT_NAME = 'electrode.bridge.message'

// The different message types
const ELECTRODE_BRIDGE_REQUEST_TYPE = 'req'
const ELECTRODE_BRIDGE_RESPONSE_TYPE = 'rsp'
const ELECTRODE_BRIDGE_EVENT_TYPE = 'event'

const DEFAULT_REQUEST_TIMEOUT_IN_MS = 5000

const ERROR_REQUEST_TIMEOUT = {
  code: 'EREQUESTIMEOUT',
  message: 'Request timeout'
}

const ERROR_NO_REQUEST_HANDLER = {
  code: 'ENOHANDLER',
  message: 'No registered request handler'
}

class ElectrodeBridge extends EventEmitter {
  constructor() {
    super()

    let eventEmitter = (Platform.OS === 'ios')
      ? new NativeEventEmitter(NativeModules.ElectrodeBridge)
      : DeviceEventEmitter

    eventEmitter.addListener(
      ELECTRODE_BRIDGE_MESSAGE_EVENT_NAME,
      this._onMessageFromNative.bind(this))

    this.requestHandlerByRequestName = new Map()
    this.pendingResponseCallbackById = new Map()
    this.eventListenerUUIDRef = new Map()
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
   */
  emitEvent(
    name /*: string */ , {
      data
    } = {} /*: Object */ ) {
      const eventMessage = this._buildMessage(ELECTRODE_BRIDGE_EVENT_TYPE, name, { data })
      NativeModules.ElectrodeBridge.sendMessage(eventMessage)
      this.emit(name, data)
  }

  /**
   * Sends a request
   *
   * @param {string} name - The name of the request to send
   * @param {Object} obj - Options
   * @param {Object} obj.data - The data attached to this request [DEFAULT: {}]
   * @param {number} obj.timeout - The timeout of the request in ms [DEFAULT:5000]
   */
  sendRequest(
    name /*: string */ , {
      data,
      timeout = DEFAULT_REQUEST_TIMEOUT_IN_MS
    } = {} /*: Object */ ) /*: Promise<*> */ {
    let requestPromise

    if (this.requestHandlerByRequestName.has(name)) {
      requestPromise = this.requestHandlerByRequestName.get(name)(data)
    } else {
      let requestMessage = this._buildMessage(ELECTRODE_BRIDGE_REQUEST_TYPE, name, { data })
      requestPromise = this._waitForResponse(requestMessage.id)
      NativeModules.ElectrodeBridge.sendMessage(requestMessage)
    }

    const timeoutPromise = new Promise((resolve, reject) => {
      setTimeout(reject, timeout, ERROR_REQUEST_TIMEOUT)
    })

    return Promise.race([requestPromise, timeoutPromise])
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
    if (this.requestHandlerByRequestName.has(name)) {
      throw new Error(`A handler is already registered for request name ${name}`)
    }

    this.requestHandlerByRequestName.set(name, handler)
  }

  /**
   * Registers an event listener for a given event name
   *
   * @param {string} name - The name of the event
   * @param {Function} handler - A function to handle incoming events having this name
   */
  registerEventListener(
    name /*:string */ ,
    handler /*:Function*/ ) /*:string */ {
    this.addListener(name, handler)
    let event = { name , handler}
    let eventUUID = uuid.v4()
    this.eventListenerUUIDRef.set(eventUUID, event)
    return eventUUID
  }

  /**
   * Removes event listener for a given event UUID returned while registering EventListener
   *
   * @param {string} name - UUID of the event
   */
  removeEventListener(
    uuid /*:string */ ) /*:EventEmitter*/ {
    const event = this.eventListenerUUIDRef.get(uuid)
    if(event) {
      this.eventListenerUUIDRef.delete(uuid)
      return this.removeListener(event.name, event.handler)
    }
  }

  //============================================================================
  // INTERNAL METHODS
  //============================================================================

  /**
   * Builds a bridge message
   *
   * @param {string} type - The type of the message ('req', 'rsp' or 'event')
   * @param {string} name - The name of the message
   * @param {Object} obj - Options
   * @param {Object|string|number} obj.data - The data attached to this message
   * @param {Object} obj.error - The error attached to this message
   * @param {string} obj.id - The id of this message
   */
  _buildMessage(type, name, {
      data,
      error,
      id = uuid.v4()
    } = {}) {
    let message = { type, name, id }
    //Check only if data is null or undefined. 0, false are valid values
    if ((data !== undefined) && (data !== null)) {
      message.data = data
    } else if (error) {
      message.error = error
    }

    return message
  }

  /**
   * Create a promise that completed whenever we get a response
   * for a given request
   *
   * @param {string} requestId - The id of the request
   */
  _waitForResponse(requestId) {
    return new Promise((resolve, reject) => {
      this.pendingResponseCallbackById.set(requestId, (data, error) => {
        if (error) {
        reject(error)
        } else {
        resolve(data)
        }
      })
    })
  }

  /**
   * Called whenever a bridge message is received from the native side
   */
  _onMessageFromNative(message) {
    switch (message.type) {
      case ELECTRODE_BRIDGE_REQUEST_TYPE:
        if (!this.requestHandlerByRequestName.has(message.name)) {
          const errorMessage = this._buildMessage(
            ELECTRODE_BRIDGE_RESPONSE_TYPE,
            message.name,
            { id: message.id, error: ERROR_NO_REQUEST_HANDLER })
          return NativeModules.ElectrodeBridge.sendMessage(errorMessage)
        }

        this.requestHandlerByRequestName.get(message.name)(message.data)
        .then((data) => {
          const responseMessage = this._buildMessage(
            ELECTRODE_BRIDGE_RESPONSE_TYPE,
            message.name,
            { id: message.id, data })
          return NativeModules.ElectrodeBridge.sendMessage(responseMessage)
        })
        .catch((error) => {
          const errorMessage = this._buildMessage(
            ELECTRODE_BRIDGE_RESPONSE_TYPE,
            message.name,
            { id: message.id, error });
          return NativeModules.ElectrodeBridge.sendMessage(errorMessage)
        })
        break

      case ELECTRODE_BRIDGE_RESPONSE_TYPE:
        if (this.pendingResponseCallbackById.has(message.id)) {
          this.pendingResponseCallbackById.get(message.id)(message.data, message.error)
          this.pendingResponseCallbackById.delete(message.id)
        }
        break

      case ELECTRODE_BRIDGE_EVENT_TYPE:
        this.emitEvent(message.name, message.data ? { data: message.data } : {})
        break
    }
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
    if (!this.requestHandlerByRequestName.has(name)) {
      throw ERROR_NO_REQUEST_HANDLER
    }
    return this.requestHandlerByRequestName.get(name)(data)
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
      uuid.v4(), {
        requestId,
        data
      })
  }

  /**
   * Sends an error response to the native for a given request
   *
   * @param {string} requestId - The id of the request associated to this response
   * @param {Object} error - The response error
   */
  _sendErrorResponseToNative(
    requestId /*: string */ ,
    error) {
    NativeModules.ElectrodeBridge.dispatchEvent(
      ELECTRODE_BRIDGE_RESPONSE_EVENT_NAME,
      uuid.v4(), {
        requestId,
        error
      })
  }
}

export const DispatchMode = {
  NATIVE: 0,
  JS: 1,
  GLOBAL: 2
};

export const electrodeBridge = new ElectrodeBridge()
