![travis ci](https://travis-ci.org/electrode-io/react-native-electrode-bridge.svg?branch=master)


Electrode React Native Bridge
-----------------------------

This project is essentially a react native library, consisting of a JavaScript module and an associated Android and iOS Native modules.

It is built on top of the react native built-in bridging constructs (to communicate between the react native JS side and Native side) and offers a clean bi-directional communication API, exposing methods to send events and/or requests from/to any side of the bridge (JS/Native).
It offers more options and flexibility to communicate between the JS/Native side that is not offered out of the box by react native. Ultimately it can help with integrating react native applications into existing native code bases.
It might be used as one of the basic building block of react native apps and native modules.

Here is a non-exhaustive list of a few reasons to use this library as the low level communication bridge instead of the built-in react native constructs :

- Isolates the host application from react native library types and specifics (vanilla Java/Android/Swift implementation)
- Messages can be sent either to the other side of the bridge or on the same side (or both)
- Request timeout supported
- Offer an easy way to send requests from Native to JS side.
- Offers type safety on native side while communicating to JS side.
- More than a bridge, it can be used as a message hub that allows react native apps / native modules intercommunication

Read more about bridge [HERE](https://electrode.gitbooks.io/electrode-native/content/platform-parts/bridge.html)

### React Native Version Compatibility

| React Native Version | Bridge Version |
| ------------- | -----|
| v0.42->v0.47 | v1.5.0+ |
| v0.48+ | v1.5.9+ |

### Getting Started

Follow the steps below to start contributing to the bridge code.

```bash
$ cd <workspace>
$ git clone https://github.com/electrode-io/react-native-electrode-bridge.git
$ cd react-native-electrode-bridge
```

Followed by

```bash
$ yarn add react-native
```

OR

```bash
$ npm install react-native
```

Now depending on which part of the bridge(Android|iOS|JS) you are working on open the code in respective IDEs.

# Bridge message types

Communication through the Electrode Native bridge is based on message exchanges between JavaScript and the Native application. The Electrode Native bridge processes three message types: `Request`, `Response`, and `Event`.

- `Request`   
A Request message is used to request data from a receiver or to request an action to be performed by a receiver. A Request message always results in an associated response message that can contain either the requested data or indicate the result of an action. A Request message can optionally contain a payload. For any given Request message type, there can be only one associated receiver. The receiver handles the request and issues a response message. From a developer perspective, a Request message can be thought as being a method call.

- `Response`    
A Response message is the result of a single Request message. A Response message can optionally contain a payload. From a developer perspective, a Response message can be thought as the return value of a method. The value can be of a specific type or not (void).

- `Event`  
An Event message is a "fire and forget" message. The sender of the Event message does not expect a response --so the receiver is known as a listener. Unlike a Request message, an Event message can be received by multiple listeners. All registered listeners (on the JavaScript side and native side) for a specific event message type will receive the Event message.

# How to use Electrode Native Bridge

## JavaScript

```javascript
import { electrodeBridge } from 'react-native-electrode-bridge';
```

Once you import the module, you can interact with the ```electrodeBridge``` instance through a few API methods:

#### electrodeBridge.sendRequest

```javascript
electrodeBridge.sendRequest(
    name: String, {
    data: Object = {},
    timeout: Number = DEFAULT_REQUEST_TIMEOUT_IN_MS /* 5000 */
  }): Promise
```

Sends a request with a specific `name` through the bridge.

*Mandatory*

- `name` : The name of the request to emit

Optional :

- `data` : An object to include as the data payload of the request *(Default : {})*

- `timeout` : A timeout in milliseconds, after which, if no response was received, the returned promise will be rejected with error code `EREQUESTIMEOUT`. *(Default : 5000)*

Example usage :

```javascript
electrodeBridge.sendRequest(
    "myapp.get.current.weather", {
    data: { latlng: `37.381435,-122.036909` },
    timeout: 6000
  }).then(resp) {
    // Do whatever you need to do with the response
  }
```

#### electrodeBridge.emitEvent

```javascript
electrodeBridge.emitEvent(
    name: String, {
    data: Object = {}
  }): void
```

Emits an event with a specific `name` through the bridge.

*Mandatory*

- `name` : The name of the event to emit

*Optional*

- `data` : An object to include as the data payload of the event *(Default : {})*

Example usage :

```javascript
electrodeBridge.emitEvent("myapp.some.event");
```

#### electrodeBridge.registerRequestHandler

```javascript
electrodeBridge.registerRequestHandler(
  name: String,
  handler: Promise): void
```

Registers a handler that can handle a specific request `name`.
Please note that if an handler already exists for the specific request name (on the side you are making the call) the method will throw an error. Current implementation only allows one request handler to be associated to a given request name.

*Mandatory*

- `name` : The name of the request this handler can handle

- `handler` : The handler function, taking a single parameter being the data of the request and returning a Promise. Implementer of the handler should either resolve the promise with an object being the response data (if any) or reject the promise with an Error.

Example usage :

```javascript
electrodeBridge.registerRequestHandler(
  "myapp.awesomerequest",
  (requestData) => {
    return Promise.resolve({ hello: "World"});
  }
)
```

#### electrodeBridge.registerEventListener

```javascript
electrodeBridge.registerEventListener(
  name: String,
  handler: Function): void
}
```

Registers an event listener that will be invoked whenever an event of the specific `type` is received by the bridge.

*Mandatory*

- `name` : The name of the event that this listener is interested in

- `handler` : A function to handle an incoming event. The function takes a single parameter being the data payload of the event (if any).

Example usage :

```javascript
electrodeBridge.registerEventListener(
  "myapp.coolevent",
  (eventData) => {
    // Do whatever you need to do
  }
)
```

## Android

First step is to add the `ElectrodeBridgePackage` containing the `ElectrodeBridge` Native Module to the list of packages included in your app :

```java
@Override
protected List<ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
      new MainReactPackage(),
      new ElectrodeBridgePackage() // ADD THIS LINE !
  );
}
```

Then, access to API methods is provided through static methods of the `ElectrodeBridgeHolder` class.

`ElectrodeBridge` can deal with any `PrimitiveWrapper` or `Bridgeable` as the request and response types.

#### ElectrodeBridgeHolder.sendRequest

```java
void sendRequest(
  @NonNull ElectrodeBridgeRequest request,
  @NonNull ElectrodeBridgeResponseListener<ElectrodeBridgeResponse> responseListener);
```

Sends a request through the bridge.

*Mandatory*

- `request` : A request instance created using `ElectrodeBridgeRequest.Builder`

- `responseListener` : An instance of `ElectrodeBridgeResponseListener` to be notified of the response.

To make it easier to construct a request and send it via bridge the `RequestProcessor` class can be used

Example usage :

```java

new RequestProcessor<>("my.request.name", <input data>, <ExpectedResponse>.class, new ElectrodeBridgeResponseListener<ExpectedObjectType>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
              //Handle failure
            }

            @Override
            public void onSuccess(@Nullable ExpectedObjectType responseData) {
              // Do whatever you need to do with the response
            }
        }).execute();
```
The `RequestProcessor` takes care of generating a `ElectrodeBridgeRequest` and sending it over to the `ElectrodeBridge`

In case of a request not expecting any `ElectrodeBridgeRequest` use `None` as the type.

`<input data>`:  can be null.

#### ElectrodeBridge.emitEvent

```java
void emitEvent(@NonNull ElectrodeBridgeEvent event)
```

Emits an event through the bridge.

*Mandatory*

- `event` : An event instance created using `ElectrodeBridgeEvent.Builder`

To make is easier to construct an event and emit it via bridge the `EventProcessor` class can be used


Example usage :

```java

new EventProcessor<>("my.event.name", <data>).execute();

```

`<data>` can be null

#### ElectrodeBridge.registerRequestHandler

```java
UUID registerRequestHandler(
  @NonNull String name,
  @NonNull ElectrodeBridgeRequestHandler requestHandler);
```

Registers a handler that can handle a specific request `name`.
When a request is fired, for example from JS side, `ElectrodeBridge` first looks for a registered request handler on JS side, if not found bridge will forward the request to Native side.

*Mandatory*

- `name` : The name of the request this handler can handle

- `requestHandler` an instance of `ElectrodeBridgeRequestHandler` that should take care of handling the request and completing it.

To make is easier to construct a request handler and register it to the bridge a `RequestHandlerProcessor` class can be used

Example usage :

```java
new RequestHandlerProcessor<>("my.request.name", <ExpectedRequest>.class, <ExpectedResponse>.class, new ElectrodeBridgeRequestHandler<ExpectedRequestType, ExpectedResponseType>() {
           @Override
           public void onRequest(@Nullable ExpectedRequestType payload, @NonNull ElectrodeBridgeResponseListener<ExpectedResponse> responseListener) {
             // Handle the request (sync or async) and call one of the completion methods once done
             requestCompletioner.onSuccess(expectedResponse); OR
             requestCompletion.onFailure(failureMessage);  // With error
           }
       }).execute();
```

#### ElectrodeBridge.registerEventListener

```java
void addEventListener(
  @NonNull String eventName,
  @NonNull ElectrodeBridgeEventListener eventListener);
)
```

*Mandatory*

- `name` : The name of the event that this listener is interested in

- `eventListener` an instance of `ElectrodeBridgeEventListener` that is interested in knowing when an event is emitted.

Example usage :

```java
new EventListenerProcessor<>("my.event.name", <ExpectedEvent>.class, new ElectrodeBridgeEventListener<ExpectedEvent>() {
            @Override
            public void onEvent(@Nullable ExpectedEvent eventPayload) {
                //Do what you need to do now.
            }
        }).execute();
```

Note: Multiple event listeners can be registered for the same event.

### Android remarks

This bridge implementation does not make use any third party library, so that we don't lock in any client into a specific framework. It makes use of vanilla Java/Android types to expose communication methods.

The client native app might want to build a specific adapter around the bridge, so that the native app can make use of whatever framework fits them best (Rx/Bolts/Otto ... for communication ... Jackson/Gson for serialization ...).

It would be nice at some point to see adapters for a specific frameworks being redistributed as libraries to be used by others.


## iOS

First step is to install `ElectrodeReactNativeBridge` as dependency through your preferred way.
Next step is to import `ElectrodeReactNativeBridge.h` into your app

```objectivec
#import <ElectrodeReactNativeBridge/ElectrodeReactNativeBridge.h>

```

Then, access to API methods is provided through static methods of the `ElectrodeBridgeHolder` class.

`ElectrodeBridgeHolder` can deal with any `primitives` or `Bridgeable` as the request and response types.

#### sendRequest:completionHandler:
```objectivec
+ (void)sendRequest:(ElectrodeBridgeRequest *)request
    completionHandler:(ElectrodeBridgeResponseCompletionHandler)completion;
```

Sends a request through the bridge.

*Nonnull*

- `request` : A request instance created by `ElectrodeBridgeRequest` class

- `completionHandler` : An block that takes an `id _Nullable data` and an `id<ElectrodeFailureMessage> _Nullable message` to notify its listener on completion of a request. When the request failed, a failure message must be send back; Otherwise, the request is assumed to be successful. In the case of success, `data` associated with the request could be pass back to the listener(optional).


To make it easier to construct a request and send it via bridge the `ElectrodeRequestProcessor` class can be used

Example usage :

```swift
let requestProcessor = ElectrodeRequestProcessor<TReq, TResp, TItem>(
    requestName: "my.request.name",
    requestPayload: <request data>,
    respClass: <ExpectedResponseObjectType>.self,
    responseItemType: <ItemObjectType>.self, //only needed if response is an array. e.g. for [Person], it will be Person.self
    responseCompletionHandler: responseCompletionHandler: { any, failureMessage in
        if let failure = failureMessage {
            // handle failure
        } else {
            // handle success
        }
     })
requestProcessor.execute()
```
The `ElectrodeRequestProcessor` takes care of generating a `ElectrodeBridgeRequest` and sending it over to the `ElectrodeBridge`

In case of a request not expecting any `ElectrodeBridgeRequest` use `None` as the type.

`requestPayload` and `responseItemType` are `Optional`.

#### sendEvent:

```objectivec
+ (void)sendEvent:(ElectrodeBridgeEvent *)event;
```

Emits an event through the bridge.

*Nonnull*

- `event` : An event instance of `ElectrodeBridgeEvent`

To make is easier to construct an event and emit it via bridge the `EventProcessor` class can be used


Example usage :

```objectivec
let eventProcessor = EventProcessor(eventName: "<event name>", eventPayload: <event payload>)
eventProcessor.execute()
```

`eventPayload` is `Optional`

#### registerRequestHanlderWithName:requestCompletionHandler

```objectivec
+ (void)registerRequestHanlderWithName:(NSString *)name
              requestCompletionHandler:(ElectrodeBridgeRequestCompletionHandler)completion;
```

Registers a handler that can handle a specific request `name`.
When a request is fired, for example from JS side, `ElectrodeBridge` first looks for a registered request handler on JS side, if not found bridge will forward the request to Native side.

*Nonnull*

- `name` : The name of the request this handler can handle

- `requestHandler` a `ElectrodeBridgeRequestCompletionHandler` block that should take care of handling the request and completing it.

To make is easier to construct a request handler and register it to the bridge a `ElectrodeRequestHandlerProcessor` class can be used

Example usage :

```swift
let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(
    requestName: "<your request name>",
    reqClass: <YourRequestParamClass>.self,
    respClass: <ExpectedResponseClass>.self,
    requestCompletionHandler: { data, responseCompletionHandler in
        // data is of type <ExpectedResponseClass>
        // responseCompletionHandler is a block of ElectrodeBridgeRequestCompletionHandler
    })
requestHandlerProcessor.execute()

```

#### addEventListnerWithName:eventListner:

```objectivec
+ (void)addEventListnerWithName:(NSString *)name
                   eventListner:(ElectrodeBridgeEventListener)eventListner;
```

*Nonnull*

- `name` : The name of the event that this listener is interested in

- `eventListener` a `ElectrodeBridgeEventListener` ElectrodeBridgeEventListener that is interested in knowing when an event is emitted.

Example usage :
```swift
let listenerProcessor = EventListenerProcessor(
    eventName: "<YourEventName>",
    eventPayloadClass: <PayloadClass>.self,
    eventListener: { payload in
        // the payload emitted with the event
    })
listenerProcessor.execute()

```

Note: Multiple event listeners can be registered for the same event.
