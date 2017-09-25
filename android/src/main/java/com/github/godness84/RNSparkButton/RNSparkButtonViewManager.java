package com.github.godness84.RNSparkButton;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * View manager for {@link RNSparkButton}.
 */
public class RNSparkButtonViewManager extends
        SimpleViewManager<RNSparkButton> {

    public static final String REACT_CLASS = "RNSparkButton";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected RNSparkButton createViewInstance(ThemedReactContext reactContext) {
        return new RNSparkButton(reactContext);
    }

    @ReactProp(name = "activeImageSrc")
    public void setActiveImageSource(RNSparkButton view, @Nullable ReadableMap source) {
        view.setActiveImageSource(source);
    }

    @ReactProp(name = "inactiveImageSrc")
    public void setInactiveImageSource(RNSparkButton view, @Nullable ReadableMap source) {
        view.setInactiveImageSource(source);
    }

    @ReactProp(name = "activeImageTint", defaultInt = Color.TRANSPARENT, customType = "Color")
    public void setActiveImageTint(RNSparkButton view, int tint) {
        view.setActiveImageTint(tint);
    }

    @ReactProp(name = "inactiveImageTint", defaultInt = Color.TRANSPARENT, customType = "Color")
    public void setInactiveImageTint(RNSparkButton view, int tint) {
        view.setInactiveImageTint(tint);
    }

    @ReactProp(name = "primaryColor", defaultInt = Color.BLACK, customType = "Color")
    public void setPrimaryColor(RNSparkButton view, int color) {
        view.setPrimaryColor(color);
    }

    @ReactProp(name = "secondaryColor", defaultInt = Color.BLACK, customType = "Color")
    public void setSecondayColor(RNSparkButton view, int color) {
        view.setSecondaryColor(color);
    }

    @ReactProp(name = "animationSpeed", defaultFloat = 1.0f)
    public void setAnimationSpeed(RNSparkButton view, float speed) {
        view.setAnimationSpeed(speed);
    }

    @Override
    protected void onAfterUpdateTransaction(RNSparkButton view) {
        super.onAfterUpdateTransaction(view);
        view.maybeUpdateView();
    }
}
