package com.github.godness84.RNSparkButton;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

/**
 * View manager for {@link RNSparkButton}.
 */
public class RNSparkButtonViewManager extends
        ViewGroupManager<RNSparkButton> {

    public static final String REACT_CLASS = "RNSparkButton";
    public static final int COMMAND_PLAY_ANIMATION = 1;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected RNSparkButton createViewInstance(ThemedReactContext reactContext) {
        return new RNSparkButton(reactContext);
    }

    @ReactProp(name = "pressed", defaultBoolean = false)
    public void setPressed(RNSparkButton view, boolean pressed) {
        view.setPressed(pressed);
    }

    @ReactProp(name = "checked", defaultBoolean = false)
    public void setChecked(RNSparkButton view, boolean checked) {
        view.setChecked(checked);
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

    @javax.annotation.Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "playAnimation", COMMAND_PLAY_ANIMATION
        );
    }

    @Override
    public void receiveCommand(
            final RNSparkButton view,
            int commandType,
            @Nullable ReadableArray args) {
        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case COMMAND_PLAY_ANIMATION: {
                view.playAnimation();
                return;
            }

            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }
    }

    @Override
    protected void onAfterUpdateTransaction(RNSparkButton view) {
        super.onAfterUpdateTransaction(view);
        view.maybeUpdateView();
    }
}
