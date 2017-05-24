Electrode React Native Bridge
-----------------------------

*WIP/EXPERIMENTAL*

Link to the [demo app](/example)

This project is essentially a react native library, consisting of a JavaScript module and an associated Android Native Module and iOS Native module.

It is built on top of the react native built-in bridging constructs (to communicate between the react native JS side and Native side) and offers a clean bi-directional communication API, exposing methods to send events and/or requests from/to any side of the bridge (JS/Native). It offers more options and flexibility to communicate between the JS/Native side that is offered out of the box by react native. Ultimately it can help with integrating react native applications into existing native code bases. It might be used as one of the basic building block of react native mini apps and native modules.

Here is a non-exhaustive list of a few reasons to use this library as the low level communication bridge instead of the built-in react native constructs :

- Isolates the host application from react native library types and specifics (vanilla Java/Android implementation)
- Messages can be sent either to the other side of the bridge or on the same side (or both)
- Request timeout supported
- Offer a way to send requests from Native to JS side
- More than a bridge, it can be used as a message hub allow react native apps / native modules intercommunication

The bridge API is built around two messaging idioms, `events` and `requests` :

- An event is a fire & forget message type. You should emit an event whenever you are not expecting any kind of response back from whoever is listening for this specific event. Because of this, there can be multiple listeners for a given event name.
- On the other hand, a request expects some kind of response. You should send a request whenever you are asking for something (be it some data or just an acknowledgment that the request was successfully handled or not). A request can only have one "handler" registered for it. Also due to the fact that sending a request expects a response back; you can specify a timeout when sending the request.

Both sides of the bridge (JS/Native) expose a similar API (mirrored) to respectively send requests and emit events, and also listen for specific events or requests.

While this bridge can be used a standalone react native plugin to integrate a single react native app into an native host application, this is not the optimal use of it. Indeed multiple native modules using the bridge for communication will be able to exchange messages between themselves or with the react native apps.

```
For iOS:
NOTE: Please do yarn add react-native@0.42.0, that installs react-native. This step is necessary to avoid compiling issues in iOS. 
For eg: b0m00ca@m-C02RW0LJG8WM ~/Documents/Projects/react-native-electrode-bridge (master) $ yarn add react-native@0.42.0
```

```
TODO :

ANDROID :
- Offer a way to unregister event/request handlers in order to avoid keeping references around
- Figure if annotations might offer a nicer client API and implement annotation support (@EventHandler @RequestHandler)
```

### JavaScript API Reference

```javascript
import { electrodeBridge } from '@walmart/react-native-electrode-bridge';
```

Once you import the module, you can interact with the ```electrodeBridge``` instance through a few API methods:

#### electrodeBridge.sendRequest

```javascript
electrodeBridge.sendRequest(
    name: String, {
    data: Object = {},
    timeout: Number = DEFAULT_REQUEST_TIMEOUT_IN_MS /* 5000 */,
    dispatchMode = DispatchMode.NATIVE
  }): Promise
```

Sends a request with a specific `name` through the bridge.

*Mandatory*

- `name` : The name of the request to emit

Optional :

- `data` : An object to include as the data payload of the request *(Default : {})*

- `timeout` : A timeout in milliseconds, after which, if no response was received, the returned promise will be rejected with error code `EREQUESTIMEOUT`. *(Default : 5000)*

- `dispatchMode` : The dispatch mode to use for the request :
    - `DispatchMode.NATIVE` will send the request to the native side
    - `DispatchMode.JS` will send the request to the JS side

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
    data: Object = {},
    dispatchMode: DispatchMode = DispatchMode.NATIVE
  }): void
```

Emits an event with a specific `name` through the bridge.

*Mandatory*

- `name` : The name of the event to emit

*Optional*

- `data` : An object to include as the data payload of the event *(Default : {})*

- `dispatchMode` : The dispatch mode to use for the event :
    - `DispatchMode.NATIVE` will send the event to the native side
    - `DispatchMode.JS` will send the event to the JS side
    - `DispatchMode.GLOBAL` will send the event to both native & JS side

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

### Android API Reference

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

`ElectrodeBridge` can deal with any `PrimitiveWrapper` and `Bridgeable` as the request and response types.

#### ElectrodeBridgeHolder.sendRequest

```java
void sendRequest(
  @NonNull ElectrodeBridgeRequest request,
  @NonNull ElectrodeBridgeResponseListener responseListener);
```

Sends a request through the bridge.

*Mandatory*

- `request` : A request instance created using `ElectrodeBridgeRequest.Builder`

- `completionListener` : An instance of `RequestCompletionListener` to be notified of the response.

To make is easier to construct a request and send it via bridge the `RequestProcessor` class can be used

Example usage :

```java

new RequestProcessor<>("my.request.name", <input data>, <ExpectedResponse>.class, new ElectrodeBridgeResponseListener<ExpectedResponse>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
              //Handle failure
            }

            @Override
            public void onSuccess(@Nullable ExpectedResponse responseData) {
              // Do whatever you need to do with the response
            }
        }).execute();
```
The `RequestProcessor` takes care of generating a `ElectrodeBridgeRequest` and sending it over to the `ElectrodeBridge`

In case of a request not expecting any `ExpectedResponse` use `None` to indicate the same.

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
new RequestHandlerProcessor<>("my.request.name", <ExpectedRequest>.class, <ExpectedResponse>.class, new ElectrodeBridgeRequestHandler<ExpectedRequest, ExpectedResponse>() {
           @Override
           public void onRequest(@Nullable ExpectedRequest payload, @NonNull ElectrodeBridgeResponseListener<ExpectedResponse> responseListener) {
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
