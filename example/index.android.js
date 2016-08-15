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
  Slider
} from 'react-native';
import { electrodeBridge } from 'react-native-electrode-bridge';

class ModuleTesting extends Component {

  constructor(props) {
    super(props);
    this.state = {
      bgColor: 'rgb(0,0,0)',
      pendingRequest: false,
      pendingRequestPromiseResolve: null,
      pendingRequestPromiseReject: null
    };
  }

  componentDidMount() {
    electrodeBridge.addListener("native.seekbar.value.update", this._updateColor.bind(this));
    electrodeBridge.registerRequestHandler("coremodule.request", this._receivedRequest.bind(this));
  }

  _receivedRequest(data) {
    return new Promise((resolve,reject) => {
        this.setState({
          pendingRequest: true,
          pendingRequestPromiseReject: reject,
          pendingRequestPromiseResolve: resolve
        });
    });
  }

  _updateColor(data) {
    this.setState({bgColor: `rgb(0, 0, ${data.value})`});
  }

  render() {
    return (
      <View style={styles.container} backgroundColor={this.state.bgColor}>
        <TouchableOpacity onPress={this._sendRequest}>
          <Text style={styles.button}>
            Send Request
          </Text>
        </TouchableOpacity>
        <Slider style={styles.slider} maximumValue={255} step={1} onValueChange={this._sliderValueChanged}/>
        {this._renderRequestCompletionButtons()}
      </View>
    );
  }

  _renderRequestCompletionButtons() {
    let component;
    if (this.state.pendingRequest === true) {
      component =
      <View>
        <TouchableOpacity onPress={this._resolvePendingRequest.bind(this)}>
          <Text style={styles.button}>
            Resolve Request
          </Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={this._rejectPendingRequest.bind(this)}>
          <Text style={styles.button}>
            Reject Request
          </Text>
        </TouchableOpacity>
      </View>
    } else {
      component = <View/>
    }

    return (component);
  }

  _resolvePendingRequest() {
    this.state.pendingRequestPromiseResolve({hello: "world"});
    this._cleanPendingRequestState();
  }

  _rejectPendingRequest() {
    this.state.pendingRequestPromiseReject(new Error("boum"));
    this._cleanPendingRequestState();
  }

  _cleanPendingRequestState() {
    this.setState({
      pendingRequest: false,
      pendingRequestPromiseReject: null,
      pendingRequestPromiseResolve: null
    });
  }

  _sendRequest() {
    electrodeBridge.sendRequestToNative("coremodule.request", {"hello": "world"})
    .then(
      resp => {
        console.log("Response received");
      }
    )
    .catch(error => {
      console.log("Error received");
    });
  }

  _sliderValueChanged(value) {
    electrodeBridge.emitEventToNative("reactnative.seekbar.value.update", {value:value});
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  slider: {
    width: 400
  }
});

AppRegistry.registerComponent('ModuleTesting', () => ModuleTesting);
