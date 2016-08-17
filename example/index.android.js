/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

 //@flow

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Slide
} from 'react-native';
import {
  electrodeBridge,
  RequestDispatchMode,
  EventDispatchMode
} from 'react-native-electrode-bridge';
import RadioForm, {
  RadioButton,
  RadioButtonInput,
  RadioButtonLabel
} from 'react-native-simple-radio-button';

// Inbound event/request types
const NATIVE_REQUEST_EXAMPLE_TYPE = "native.request.example";
const EVENT_EXAMPLE_TYPE = "event.example";

// Outbound event/request types
const REACTNATIVE_REQUEST_EXAMPLE_TYPE = "reactnative.request.example";

class ElectrodeBridgeExample extends Component {

  constructor(props) {
    super(props);

    this.state = {
      pendingInboundRequest: null,
      logText: ">>>",
      eventDispatchMode: EventDispatchMode.NATIVE_WITH_JS_FALLBACK,
      requestDispatchType: RequestDispatchMode.NATIVE_WITH_JS_FALLBACK
    };
  }

  componentDidMount() {
    electrodeBridge.addListener(EVENT_EXAMPLE_TYPE,
    this._logIncomingEvent.bind(this));

    electrodeBridge.registerRequestHandler(NATIVE_REQUEST_EXAMPLE_TYPE,
      this._receivedRequest.bind(this));

    electrodeBridge.registerRequestHandler(REACTNATIVE_REQUEST_EXAMPLE_TYPE,
        this._receivedRequest.bind(this));
  }

  _receivedRequest(data) {
    this._setLoggerText(`Request received. Payload : ${JSON.stringify(data)}`);
    return new Promise((resolve,reject) => {
        this.setState({
          pendingInboundRequest: { reject, resolve }
        });
    });
  }

  render() {
    let radio_props_event_dispatch_modes = [
      { label: 'Native=>JS  ', value: EventDispatchMode.NATIVE_WITH_JS_FALLBACK },
      { label: 'JS=>Native  ', value: EventDispatchMode.JS_WITH_NATIVE_FALLBACK, },
      { label: 'Global  ', value: EventDispatchMode.GLOBAL }
    ];

    let radio_props_request_dispatch_modes = [
      { label: 'Native=>JS  ', value: EventDispatchMode.NATIVE_WITH_JS_FALLBACK },
      { label: 'JS=>Native  ', value: EventDispatchMode.JS_WITH_NATIVE_FALLBACK }
    ];

    return (
      <View style={styles.container}>
        <View style={{flexDirection:'column', justifyContent: 'space-between'}}>
        <Text style={styles.logger}>
          {this.state.logText}
        </Text>
        <View style={styles.buttonGroup}>
          <View style={{flexDirection:'column'}}>
            <View style={{flexDirection:'row'}}>
              {this._renderButtonGroupTitle('Send request', 'gold')}
              {this._renderButton('with payload', 'royalblue',
                this._sendRequestWithPayload.bind(this))}
              {this._renderButton('w/o payload', 'royalblue',
                this._sendRequestWithoutPayload.bind(this))}
            </View>
            <RadioForm
                radio_props={radio_props_request_dispatch_modes}
                initial={this.state.requestDispatchType}
                formHorizontal={true}
                onPress={(val,idx) => { this.setState({requestDispatchType:idx}) }}
                buttonSize={5}
                labelColor={'white'}
                style={styles.radioForm}/>
          </View>
        </View>
        <View style={styles.buttonGroup}>
          <View style={{flexDirection:'column'}}>
            <View style={{flexDirection:'row'}}>
              {this._renderButtonGroupTitle('Emit event', 'gold')}
              {this._renderButton('with payload', 'royalblue',
                this._emitEventWithPayload.bind(this))}
              {this._renderButton('w/o payload', 'royalblue',
                this._emitEventWithoutPayload.bind(this))}
            </View>
            <RadioForm
                radio_props={radio_props_event_dispatch_modes}
                initial={this.state.eventDispatchType}
                formHorizontal={true}
                onPress={(val,idx) => { this.setState({eventDispatchType:idx}) }}
                buttonSize={5}
                labelColor={'white'}
                style={styles.radioForm}/>
          </View>
        </View>
        {this._renderIncomingRequestButtonGroup()}
        </View>
      </View>
    );
  }

  _sendRequestWithPayload() {
    electrodeBridge
      .sendRequest(REACTNATIVE_REQUEST_EXAMPLE_TYPE, { hello: "world" }, this.state.requestDispatchType)
      .then(resp => { this._logIncomingSuccessResponse(resp); })
      .catch(err => { this._logIncomingFailureResponse(err); });
  }

  _sendRequestWithoutPayload() {
    electrodeBridge
      .sendRequest(REACTNATIVE_REQUEST_EXAMPLE_TYPE, {}, this.state.requestDispatchType)
      .then(resp => { this._logIncomingSuccessResponse(resp); })
      .catch(err => { this._logIncomingFailureResponse(err); });
  }

  _emitEventWithPayload() {
    electrodeBridge
      .emitEvent(
        EVENT_EXAMPLE_TYPE, {
          payload: { randFloat: Math.random() },
          dispatchMode: this.state.eventDispatchType
        });
  }

  _emitEventWithoutPayload() {
    electrodeBridge
      .emitEvent(
        EVENT_EXAMPLE_TYPE, {
          dispatchMode: this.state.eventDispatchType
        });
  }

  _logIncomingEvent(evt) {
    this._setLoggerText(`Event Received. Payload : ${JSON.stringify(evt)}`);
  }

  _logIncomingSuccessResponse(resp) {
    this._setLoggerText(`Response success. Payload : ${JSON.stringify(resp)}`)
  }

  _logIncomingFailureResponse(resp) {
    this._setLoggerText(`Response failure. Payload : ${JSON.stringify(resp)}`)
  }

  _setLoggerText(text) {
    this.setState({logText: `>>> ${text}`});
  }

  _renderIncomingRequestButtonGroup() {
    let component;
    if (this.state.pendingInboundRequest !== null) {
      component =
      <View style={styles.buttonGroupIncomingRequest}>
        <View style={{flexDirection:'row'}}>
          {this._renderButtonGroupTitle('Resolve request', 'cornsilk')}
          {this._renderButton('with payload', 'green',
            this._resolveInboundRequestWithPayload.bind(this))}
          {this._renderButton('w/o payload', 'green',
            this._resolveInboundRequestWithoutPayload.bind(this))}
        </View>
        <View style={{flexDirection:'row'}}>
          {this._renderButtonGroupTitle('Reject request', 'cornsilk')}
          {this._renderButton('w/o payload', 'red',
            this._rejectInboundRequest.bind(this))}
        </View>
      </View>
    } else {
      component = <View/>
    }
    return component;
  }

  _resolveInboundRequestWithPayload() {
    this.state.pendingInboundRequest.resolve({ hello: "world" });
    this._cleanpendingInboundRequestState();
  }

  _resolveInboundRequestWithoutPayload() {
    this.state.pendingInboundRequest.resolve({});
    this._cleanpendingInboundRequestState();
  }

  _rejectInboundRequest() {
    this.state.pendingInboundRequest.reject(new Error("boum"));
    this._cleanpendingInboundRequestState();
  }

  _renderButtonGroupTitle(title, color) {
    return (
      <Text style={[styles.buttonGroupTitle, { color: color }]}>{title}</Text>
    )
  }

  _renderButton(name, color, onClickCallback) {
    return (
      <TouchableOpacity style={styles.button} onPress={onClickCallback}>
        <Text style={[styles.buttonText, { backgroundColor: color }]} onPress={onClickCallback}>
          {name}
        </Text>
      </TouchableOpacity>
    );
  }

  _cleanpendingInboundRequestState() {
    this.setState({
      pendingInboundRequest: null
    });
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between',
    backgroundColor: 'black'
  },
  buttonGroup: {
    backgroundColor: 'dimgrey',
    marginTop: 10,
    borderColor: 'cadetblue',
    borderBottomWidth: 1,
    borderTopWidth: 1
  },
  buttonGroupIncomingRequest: {
    backgroundColor: 'slategrey',
    marginTop: 10,
    borderColor: 'gold',
    borderBottomWidth: 1,
    borderTopWidth: 1
  },
  buttonGroupTitle: {
    flex:1,
    fontSize: 15,
    padding: 1,
    margin:10,
    textAlign: 'left'
  },
  button: {
    flex:1,
    margin: 10,
    borderWidth: 1,
    borderRadius: 2,
    borderColor: 'black'
  },
  buttonText: {
    textAlign: 'center',
    fontSize: 15,
    color: 'seashell'
  },
  logger: {
    margin: 10,
    fontSize: 12
  },
  radioForm: {
    justifyContent: 'space-between',
    margin: 5
  }
});

AppRegistry.registerComponent('ElectrodeBridgeExample', () => ElectrodeBridgeExample);
