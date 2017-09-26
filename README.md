
# react-native-sparkbutton

A button with Twitter's heart explosion like animation (for Android).

<img src="./art/showcase.gif" width="280" />

## Getting started

`$ npm install react-native-sparkbutton --save`

### Mostly automatic installation

`$ react-native link react-native-sparkbutton`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.github.godness84.RNSparkButton.RNSparkButtonPackage;` to the imports at the top of the file
  - Add `new RNSparkButtonPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-sparkbutton'
  	project(':react-native-sparkbutton').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-sparkbutton/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-sparkbutton')
  	```


## Usage

```javascript
import { Component } from 'react';
import SparkButton from 'react-native-sparkbutton';

class MyExample extends Component {
  state = {
    checked: false
  }

  render() {
    const { checked } = this.state;
    return (
      <SparkButton        
        style={{ width: 100, height: 100 }}
        activeImageSrc={require('./assets/thumbs-up.png')}
        inactiveImageTint={'rgba(255,255,255,0.8)'}
        primaryColor={"yellow"}
        secondaryColor={"red"}
        animationSpeed={1}
        checked={checked}
        onCheckedChange={(checked) => this.setState({ checked })} />
    );
  }
}
```

# Props

Prop name             | Description   | Type      | Default value
----------------------|---------------|-----------|--------------
`style`               | Style for the container of the button | object | {}
`checked`             | Checked or not. In case it's checked after mounting it will play the animation | bool | false
`activeImageSrc`      | Source of the active image | {uri: '...'} or require('image.png') | none
`inactiveImageSrc`    | Source of the inactive image | {uri: '...'} or require('image.png') | none
`activeImageTint`     | Tint color to apply to active image | color | none
`inactiveImageTint`   | Tint color to apply to inactive image | color | none
`primaryColor`        | Primary color for the explosion | color | none
`secondaryColor`      | Secondary color for the explosion | color | none
`animationSpeed`      | Speed of the explosion | number | 1.0
`touchableProps`      | Props for the Touchable that wraps the button internally | object | none
`onCheckedChange`     | Called when the checked state changes | function | none
`onPress`             | Called when the button is pressed | function | none

# Methods

Method name           | Params                          | Description
----------------------|---------------------------------|------------
`playAnimation`       | none | Play the explosion animation
