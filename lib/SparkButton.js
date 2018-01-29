import React, { Component } from 'react';
import ReactNative, { View, TouchableWithoutFeedback, StyleSheet, requireNativeComponent, UIManager } from 'react-native';
const resolveAssetSource = require("react-native/Libraries/Image/resolveAssetSource");
import PropTypes from 'prop-types';

const SrcPropType = PropTypes.oneOfType([
  PropTypes.shape({
    uri: PropTypes.string,
  }),
  // Opaque type returned by require('./image.jpg')
  PropTypes.number,
]);

class SparkButton extends Component {
  static propTypes = {
    ...View.propTypes,
    pressed: PropTypes.bool,
    checked: PropTypes.bool,
    activeImageSrc: SrcPropType,
    inactiveImageSrc: SrcPropType,
    activeImageTint: PropTypes.string,
    inactiveImageTint: PropTypes.string,
    primaryColor: PropTypes.string,
    secondaryColor: PropTypes.string,
    animationSpeed: PropTypes.number,
    touchableProps: PropTypes.object,
    onCheckedChange: PropTypes.func,
    onPress: PropTypes.func,
  }

  state = {
    pressed: false
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.checked !== this.props.checked && nextProps.checked) {
      this.playAnimation();
    }
  }

  playAnimation() {
    if (this._ref) {
      UIManager.dispatchViewManagerCommand(
        ReactNative.findNodeHandle(this._ref),
        UIManager.RNSparkButton.Commands.playAnimation,
        []
      );
    }
  }

  render() {
    const {
      children,
      style,
      touchableProps,
      onCheckedChange,
      onPress,
      ...props } = this.props;

    const { pressed } = this.state;

    const activeImageSrc = resolveAssetSource(this.props.activeImageSrc);
    const inactiveImageSrc = resolveAssetSource(this.props.inactiveImageSrc);

    return (
      <TouchableWithoutFeedback
        onPress={this._handlePress}
        onPressIn={this._handlePressIn}
        onPressOut={this._handlePressOut}
        delayPressIn={0}
        {...touchableProps}>
        <View style={style} pointerEvents="box-only">
          <RNSparkButton
            ref={this._setRef}
            style={StyleSheet.absoluteFill}
            pressed={pressed}
            {...props}
            activeImageSrc={activeImageSrc}
            inactiveImageSrc={inactiveImageSrc}>
            {children}
          </RNSparkButton>
        </View>
      </TouchableWithoutFeedback>
    );
  }

  _setRef = (component) => {
    this._ref = component;
  }

  _handlePress = () => {
    const { onCheckedChange, onPress, checked } = this.props;
    var newValue = !checked;
    if (onPress) {
      onPress();
    }
    if (onCheckedChange) {
      onCheckedChange(newValue);
    }
  }

  _handlePressIn = () => {
    this.setState({ pressed: true });
  }

  _handlePressOut = () => {
    this.setState({ pressed: false });
  }
}
const RNSparkButton = requireNativeComponent('RNSparkButton', null);

export default SparkButton;
