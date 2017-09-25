import React, { Component, PropTypes } from 'react';
import { View, requireNativeComponent } from 'react-native';

class SparkButton extends Component {
  static propTypes = {
    ...View.propTypes,
    activeImageSrc: PropTypes.object,
    inactiveImageSrc: PropTypes.object,
    activeImageTint: PropTypes.string,
    inactiveImageTint: PropTypes.string,
    primaryColor: PropTypes.string,
    secondaryColor: PropTypes.string,
    animationSpeed: PropTypes.number
  }

  render() {
    const {children, ...props} = this.props;

    return (
      <RNSparkButton
        {...props}>
        {children}
      </RNSparkButton>
    );
  }
}
const RNSparkButton = requireNativeComponent('RNSparkButton', null);

export default SparkButton;
