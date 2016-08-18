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
  DispatchMode
} from 'react-native-electrode-bridge';
import RadioForm, {
  RadioButton,
  RadioButtonInput,
  RadioButtonLabel
} from 'react-native-simple-radio-button';

// Inbound event/request types
const REQUEST_EXAMPLE_TYPE = "request.example";
const EVENT_EXAMPLE_TYPE = "event.example";

class ElectrodeBridgeExample extends Component {

  constructor(props) {
    super(props);

    this.state = {
      pendingInboundRequest: null,
      logText: "[JS] >>>",
      eventDispatchMode: DispatchMode.NATIVE,
      requestDispatchType: DispatchMode.NATIVE
    };
  }

  componentDidMount() {
    electrodeBridge.addListener(EVENT_EXAMPLE_TYPE,
    this._logIncomingEvent.bind(this));

    electrodeBridge.registerRequestHandler(REQUEST_EXAMPLE_TYPE,
      this._receivedRequest.bind(this));
  }

  render() {
    let radio_props_request_dispatch_modes = [
      { label: 'Native  ', value: DispatchMode.NATIVE },
      { label: 'JS  ', value: DispatchMode.JS, }
    ];

    let radio_props_event_dispatch_modes = [
      ...radio_props_request_dispatch_modes,
      { label: 'Global  ', value: DispatchMode.GLOBAL }
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
              {this._renderButton('with data', 'royalblue',
                this._sendRequestWithData.bind(this))}
              {this._renderButton('w/o data', 'royalblue',
                this._sendRequestWithoutData.bind(this))}
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
              {this._renderButton('with data', 'royalblue',
                this._emitEventWithData.bind(this))}
              {this._renderButton('w/o data', 'royalblue',
                this._emitEventWithoutData.bind(this))}
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

  _receivedRequest(data) {
    this._setLoggerText(`Request received. Data : ${JSON.stringify(data)}`);
    return new Promise((resolve,reject) => {
        this.setState({
          pendingInboundRequest: { reject, resolve }
        });
    });
  }

  _sendRequestWithData() {
    electrodeBridge
      .sendRequest(REQUEST_EXAMPLE_TYPE, {
        data: { randFloat: Math.random() },
        dispatchMode: this.state.requestDispatchType
      })
      .then(resp => { this._logIncomingSuccessResponse(resp); })
      .catch(err => { this._logIncomingFailureResponse(err); });
  }

  _sendRequestWithoutData() {
    electrodeBridge
      .sendRequest(REQUEST_EXAMPLE_TYPE, {
         dispatchMode: this.state.requestDispatchType
       })
      .then(resp => { this._logIncomingSuccessResponse(resp); })
      .catch(err => { this._logIncomingFailureResponse(err); });
  }

  _emitEventWithData() {
    electrodeBridge
      .emitEvent(
        EVENT_EXAMPLE_TYPE, {
          data: { randFloat: Math.random() },
          dispatchMode: this.state.eventDispatchType
        });
  }

  _emitEventWithoutData() {
    electrodeBridge
      .emitEvent(
        EVENT_EXAMPLE_TYPE, {
          dispatchMode: this.state.eventDispatchType
        });
  }

  _logIncomingEvent(evt) {
    this._setLoggerText(`Event Received. Data : ${JSON.stringify(evt)}`);
  }

  _logIncomingSuccessResponse(resp) {
    this._setLoggerText(`Response success. Data : ${JSON.stringify(resp)}`)
  }

  _logIncomingFailureResponse(resp) {
    this._setLoggerText(`Response failure. Data : ${JSON.stringify(resp)}`)
  }

  _setLoggerText(text) {
    this.setState({logText: `[JS] >>> ${text}`});
  }

  _renderIncomingRequestButtonGroup() {
    let component;
    if (this.state.pendingInboundRequest !== null) {
      component =
      <View style={styles.buttonGroupIncomingRequest}>
        <View style={{flexDirection:'row'}}>
          {this._renderButtonGroupTitle('Resolve request', 'cornsilk')}
          {this._renderButton('with Data', 'green',
            this._resolveInboundRequestWithData.bind(this))}
          {this._renderButton('w/o Data', 'green',
            this._resolveInboundRequestWithoutData.bind(this))}
        </View>
        <View style={{flexDirection:'row'}}>
          {this._renderButtonGroupTitle('Reject request', 'cornsilk')}
          {this._renderButton('w/o Data', 'red',
            this._rejectInboundRequest.bind(this))}
        </View>
      </View>
    } else {
      component = <View/>
    }
    return component;
  }

  _resolveInboundRequestWithData() {
    this.state.pendingInboundRequest.resolve({ randFloat: Math.random() });
    this._cleanpendingInboundRequestState();
  }

  _resolveInboundRequestWithoutData() {
    this.state.pendingInboundRequest.resolve({});
    this._cleanpendingInboundRequestState();
  }

  _rejectInboundRequest() {
    this.state.pendingInboundRequest.reject({code: "ERRORCODE", message: "boum"});
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
    backgroundColor: 'darkslategrey'
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
    margin: 5
  }
});

AppRegistry.registerComponent('ElectrodeBridgeExample', () => ElectrodeBridgeExample);
