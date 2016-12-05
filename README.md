Electrode React Native Bridge
-----------------------------

*WIP/EXPERIMENTAL*

Link to the [demo app](/example)

This project is essentially a react native library, consisting of a JavaScript module and an associated Android Native Module (iOS implementation TBD).

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
TODO :

ANDROID :
- Incoming events/requests/responses should be on the main thread (or at least give option to choose main thread or not)
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

Then, access to API methods is provided through an instance of the `ElectrodeBridge` class.

React Native engine initialization, including the initialization of all native modules is done on it's own thread. Because of this, the bridge Native Module might not be immediately ready and therefore the `ElectrodeBridge` instance can not be obtained directly.

To get a hold of this instance (which will be a singleton instance), you'll need to pass a callback to the method `ElectrodeBridgeHolder.setOnBridgeReadyListener` which will then be invoked with the bridge instance whenever it is ready to roll.

```java
ElectrodeBridgeHolder.setOnBridgeReadyListener(
  new ElectrodeBridgeHolder.OnBridgeReadyListener() {
     @Override
     public void onBridgeReady(ElectrodeBridge electrodeBridge) {
         // Here is your electrodeBridge instance !
     }
  });
```

Once you have the singleton instance of `ElectrodeBridge` at your disposal, you can start accessing its API.

#### electrodeBridge.sendRequest

```java
void sendRequest(
  @NonNull ElectrodeBridgeRequest request,
  @NonNull RequestCompletionListener completionListener);
```

Sends a request through the bridge.

*Mandatory*

- `request` : A request instance created using `ElectrodeBridgeRequest.Builder`

- `completionListener` : An instance of `RequestCompletionListener` to be notified of the response.

Example usage :

```java
Bundle data = new Bundle();
data.putInt("someInt", mRand.nextInt());

ElectrodeBridgeRequest request =
  new ElectrodeBridgeRequest.Builder("my.request.name")
    // Optional bundle containing the payload data (Default: no data)
    .withData(data)
    // Optional timeout in ms (Default: 5000)
    .withTimeout(8000)
    // Optional dispatch mode (Default: RequestDispatchMode.JS)
    .withDispatchMode(RequestDispatchMode.NATIVE)  
    .build();

electrodeBridge.sendRequest(request, new RequestCompletionListener() {
  @Override
  public void onSuccess(@NonNull Bundle payload) {
      // Do whatever you need to do with the response
      // Bundle will be empty if not payload data in the response
  }

  @Override
  public void onError(@NonNull String code, @NonNull String message) {
      // Error !
  }
});
```

#### electrodeBridge.emitEvent

```java
void emitEvent(@NonNull ElectrodeBridgeEvent event)
```

Emits an event through the bridge.

*Mandatory*

- `event` : An event instance created using `ElectrodeBridgeEvent.Builder`

Example usage :

```java
Bundle data = new Bundle();
data.putInt("someInt", mRand.nextInt());

ElectrodeBridgeEvent event =
  new ElectrodeBridgeEvent.Builder("my.event.name")
    // Optional bundle containing the payload data (Default: no data)
    .withData(data)
    // Optional dispatch mode (Default: RequestDispatchMode.JS)
    .withDispatchMode(RequestDispatchMode.NATIVE)  
    .build();

electrodeBridge.emitEvent(event);
```

#### electrodeBridge.requestRegistrar().registerRequestHandler

```java
UUID registerRequestHandler(
  @NonNull String name,
  @NonNull RequestHandler requestHandler);
```

Registers a handler that can handle a specific request `name`.  
As for the JS API, please note that if an handler already exists for the specific request name (on the side you are making the call) the method will throw an error. Current implementation only allows one request handler to be associated to a given request name.

*Mandatory*

- `name` : The name of the request this handler can handle

- `requestHandler` an instance of `RequestHandler` that should take care of handling the request and completing it.

Example usage :

```java
electrodeBridge.requestRegistrar()
  .registerRequestHandler("awesomerequest.name",
    new RequestHandler() {
      @Override
      public void onRequest(Bundle data,
                            RequestCompletioner requestCompletioner) {
        // Handle the request (sync or async) and call completion methods once done
        requestCompletioner.success();              // Without response data
        // requestCompletioner.success(bundle);     // With response data
        // requestCompletion.error(code, message);  // With error
      }
  });
```

#### electrodeBridge.eventRegistrar().registerEventListener

```java
UUID registerEventHandler(
  @NonNull String type,
  @NonNull EventListener eventListener);
)
```

*Mandatory*

- `name` : The name of the event that this listener is interested in

- `eventListener` an instance of `EventListener` that should take care of handling the event.

Example usage :

```java
electrodeBridge.eventRegistrar()
  .registerEventHandler("awesomeevent.name",
  new EventListener() {
    @Override
    public void onEvent(Bundle data) {
      // Do what you need to do
    }
  })
```

### Android remarks

This bridge implementation does not make use any third party library, so that we don't lock in any client into a specific framework. It makes use of vanilla Java/Android types to expose communication methods.

The client native app might want to build a specific adapter around the bridge, so that the native app can make use of whatever framework fits them best (Rx/Bolts/Otto ... for communication ... Jackson/Gson for serialization ...).

It would be nice at some point to see adapters for a specific frameworks being redistributed as libraries to be used by others.
