/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

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

import { RadioButtons, SegmentedControls } from 'react-native-radio-buttons';

// Inbound event/request names
const REQUEST_EXAMPLE_NAME = "request.example";
const EVENT_EXAMPLE_NAME = "event.example";

class ElectrodeBridgeExample extends Component {

  constructor(props) {
    super(props);

    this.state = {
      pendingInboundRequest: null,
      logText: "[JS] >>>",
      eventDispatchMode: DispatchMode.NATIVE,
      requestDispatchType: DispatchMode.NATIVE,
      selectedRequestOption: 'Native',
      selectedEventOption: 'Native'
    };
  }

  componentDidMount() {
    electrodeBridge.addListener(EVENT_EXAMPLE_NAME,
    this._logIncomingEvent.bind(this));

    electrodeBridge.registerRequestHandler(REQUEST_EXAMPLE_NAME,
      this._receivedRequest.bind(this));
  }



  render() {

    const requestOptions = [
		  'Native', 'JS'
  	];

  	const eventOptions = [
  		'Native', 'JS', 'Global'
  	];

    function setSelectedRequestOption(selectedOption) {
   		this.setState({ requestDispatchType: selectedOption == 'JS' ? DispatchMode.JS : DispatchMode.NATIVE });
   		this.setState({ selectedRequestOption: selectedOption });
  	}

    function setSelectedEventOption(selectedOption) {
   		var eventState = DispatchMode.JS;
   		if (selectedOption == 'Native') {
   			eventState = DispatchMode.NATIVE;
   		} else if (selectedOption == 'Global') {
   			eventState = DispatchMode.GLOBAL;
   		}
   		this.setState({ eventDispatchMode: eventState });
   		this.setState({ selectedEventOption: selectedOption });
  	}

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
            <View style={{margin: 10}}>
      				<SegmentedControls
      				  options={ requestOptions }
      				  onSelection={ setSelectedRequestOption.bind(this) }
      				  selectedOption={ this.state.selectedRequestOption }
      				  selectedTint={'white'}
      				  tint={'royalblue'}
      				/>
			      </View>
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
            <View style={{margin: 10}}>
      				<SegmentedControls
      				  options={ eventOptions }
      				  onSelection={ setSelectedEventOption.bind(this) }
      				  selectedOption={ this.state.selectedEventOption }
      				  selectedTint={'white'}
      				  tint={'royalblue'}
      				/>
			      </View>
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
      .sendRequest(REQUEST_EXAMPLE_NAME, {
        data: { randFloat: Math.random() },
        dispatchMode: this.state.requestDispatchType
      })
      .then(resp => { this._logIncomingSuccessResponse(resp); })
      .catch(err => { this._logIncomingFailureResponse(err); });
  }

  _sendRequestWithoutData() {
    electrodeBridge
      .sendRequest(REQUEST_EXAMPLE_NAME, {
         dispatchMode: this.state.requestDispatchType
       })
      .then(resp => { this._logIncomingSuccessResponse(resp); })
      .catch(err => { this._logIncomingFailureResponse(err); });
  }

  _emitEventWithData() {
    electrodeBridge
      .emitEvent(
        EVENT_EXAMPLE_NAME, {
          data: { randFloat: Math.random() },
          dispatchMode: this.state.eventDispatchMode
        });
  }

  _emitEventWithoutData() {
    electrodeBridge
      .emitEvent(
        EVENT_EXAMPLE_NAME, {
          dispatchMode: this.state.eventDispatchMode
        });
  }

  _logIncomingEvent(evt) {
    this._setLoggerText(`Event Received. Data : ${JSON.stringify(evt)}`);
  }

  _logIncomingSuccessResponse(resp) {
    this._setLoggerText(`Response success. Data : ${JSON.stringify(resp)}`)
  }

  _logIncomingFailureResponse(resp) {
    this._setLoggerText(`Response failure. code : ${resp.code} . message: ${resp.message}`)
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
