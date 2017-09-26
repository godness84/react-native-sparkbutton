/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

 import React, { Component } from 'react';
 import { View, Text, Button, StyleSheet, AppRegistry } from 'react-native';
 import SparkButton from 'react-native-sparkbutton';


 export default class Example extends React.Component {
   state = {
     size: 90,
     checked: false
   }

   render() {
     const { size, checked } = this.state;
     return (
       <View style={styles.container}>
         <View style={styles.wrapper}>
           <SparkButton
             ref={(comp) => this._spark = comp}
             style={{ width: size, height: size }}
             activeImageSrc={require('./assets/thumbs-up.png')}
             inactiveImageTint={'rgba(255,255,255,0.8)'}
             primaryColor={"yellow"}
             secondaryColor={"red"}
             animationSpeed={1}
             checked={checked}
             onCheckedChange={(checked) => this.setState({ checked })} />
         </View>
         <View style={styles.buttons}>
           <View style={styles.buttonWrapper}>
             <Button
               title="Enlarge"
               onPress={this.handleEnlarge} />
           </View>
           <View style={styles.buttonWrapper}>
             <Button
               title="Shrink"
               onPress={this.handleShrink} />
           </View>
           <View style={styles.buttonWrapper}>
             <Button
               title="Spark!"
               onPress={this.handleSpark} />
           </View>
         </View>
       </View>
     )
   }

   handleEnlarge = () => {
     this.setState({
       size: this.state.size + 15
     });
   }

   handleShrink = () => {
     this.setState({
       size: this.state.size - 15
     });
   }

   handleSpark = () => {
     this._spark.playAnimation();
   }
 }

 const styles = StyleSheet.create({
   container: {
     flex: 1,
   },
   wrapper: {
     flex: 1,
     alignItems: 'center',
     justifyContent: 'center',
   },
   buttons: {
     flexDirection: 'row',
     paddingBottom: 5
   },
   buttonWrapper: {
     flex: 1,
     margin: 5,
   }
 })

AppRegistry.registerComponent('Example', () => Example);
