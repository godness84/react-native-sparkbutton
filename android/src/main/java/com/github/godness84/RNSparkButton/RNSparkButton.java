package com.github.godness84.RNSparkButton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.views.imagehelper.ImageSource;
import com.varunest.sparkbutton.SparkEventListener;
import com.varunest.sparkbutton.helpers.CircleView;
import com.varunest.sparkbutton.helpers.DotsView;


public class RNSparkButton extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "SparkButton";
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final float DOTVIEW_SIZE_FACTOR = 3;
    private static final float DOTS_SIZE_FACTOR = .08f;
    private static final float CIRCLEVIEW_SIZE_FACTOR = 1.4f;

    int imageSize;
    int dotsSize;
    int circleSize;
    int secondaryColor;
    int primaryColor;
    int activeImageTint;
    int inActiveImageTint;
    DotsView dotsView;
    CircleView circleView;
    ImageView imageView;

    boolean pressOnTouch = true;
    float animationSpeed = 1;
    boolean isChecked = false;

    ImageSource activeImageSource;
    Bitmap activeImageBitmap;
    ImageSource inactiveImageSource;
    Bitmap inactiveImageBitmap;

    private AnimatorSet animatorSet;
    private SparkEventListener listener;
    private boolean surrogateLayoutPassScheduled = false;
    private boolean isDirty = true;

    RNSparkButton(Context context) {
        super(context);
        init();
        maybeUpdateView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup)parent).setClipChildren(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int wMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int wSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int hMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int hSize = View.MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.EXACTLY && hMode == MeasureSpec.EXACTLY) {
            imageSize = Math.min(wSize, hSize);
            isDirty = true;
            maybeUpdateView();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (!surrogateLayoutPassScheduled) {
            Log.d(TAG, "requestLayout() called. Going to schedule a surrogate layout pass");
            surrogateLayoutPassScheduled = true;
            this.post(new Runnable() {
                @Override
                public void run() {
                    RNSparkButton.this.measure(
                            View.MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY)
                    );
                    RNSparkButton.this.layout(getLeft(), getTop(), getRight(), getBottom());
                    surrogateLayoutPassScheduled = false;
                    Log.d(TAG, "surrogate layout pass executed");
                }
            });
        }
    }

    public void setActiveImageSource(@Nullable ReadableMap source) {
        this.activeImageSource = null;

        if (source != null) {
            String uri = source.getString("uri");
            this.activeImageSource = new ImageSource(getContext(), uri);
        }

        this.isDirty = true;
    }

    public void setInactiveImageSource(@Nullable ReadableMap source) {
        this.inactiveImageSource = null;

        if (source != null) {
            String uri = source.getString("uri");
            this.inactiveImageSource = new ImageSource(getContext(), uri);
        }

        this.isDirty = true;
    }

    public void setActiveImageTint(int tint) {
        this.activeImageTint = tint;
        this.isDirty = true;
    }

    public void setInactiveImageTint(int tint) {
        this.inActiveImageTint = tint;
        this.isDirty = true;
    }

    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        this.isDirty = true;
    }

    public void setSecondaryColor(int color) {
        this.secondaryColor = color;
        this.isDirty = true;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
        // not necessary to flag it dirty
    }

    /**
     * Call this function to start spark animation
     */
    public void playAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        imageView.animate().cancel();
        imageView.setScaleX(0);
        imageView.setScaleY(0);
        circleView.setInnerCircleRadiusProgress(0);
        circleView.setOuterCircleRadiusProgress(0);
        dotsView.setCurrentProgress(0);

        animatorSet = new AnimatorSet();

        ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(circleView, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        outerCircleAnimator.setDuration((long) (250 / animationSpeed));
        outerCircleAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(circleView, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        innerCircleAnimator.setDuration((long) (200 / animationSpeed));
        innerCircleAnimator.setStartDelay((long) (200 / animationSpeed));
        innerCircleAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(imageView, ImageView.SCALE_Y, 0.2f, 1f);
        starScaleYAnimator.setDuration((long) (350 / animationSpeed));
        starScaleYAnimator.setStartDelay((long) (250 / animationSpeed));
        starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(imageView, ImageView.SCALE_X, 0.2f, 1f);
        starScaleXAnimator.setDuration((long) (350 / animationSpeed));
        starScaleXAnimator.setStartDelay((long) (250 / animationSpeed));
        starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(dotsView, DotsView.DOTS_PROGRESS, 0, 1f);
        dotsAnimator.setDuration((long) (900 / animationSpeed));
        dotsAnimator.setStartDelay((long) (50 / animationSpeed));
        dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

        animatorSet.playTogether(
                outerCircleAnimator,
                innerCircleAnimator,
                starScaleYAnimator,
                starScaleXAnimator,
                dotsAnimator
        );

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                circleView.setInnerCircleRadiusProgress(0);
                circleView.setOuterCircleRadiusProgress(0);
                dotsView.setCurrentProgress(0);
                imageView.setScaleX(1);
                imageView.setScaleY(1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onEventAnimationEnd(imageView,isChecked);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onEventAnimationStart(imageView,isChecked);
                }
            }
        });

        animatorSet.start();
    }

    /**
     * Returns whether the button is checked (Active) or not.
     *
     * @return
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Change Button State (Works only if both active and disabled image resource is defined)
     *
     * @param flag desired checked state of the button
     */
    public void setChecked(boolean flag) {
        isChecked = flag;
        isDirty = true;
    }

    public void setEventListener(SparkEventListener listener) {
        this.listener = listener;
    }

    public void pressOnTouch(boolean pressOnTouch) {
        this.pressOnTouch = pressOnTouch;
        this.isDirty = true;
    }

    @Override
    public void onClick(View v) {
        isChecked = !isChecked;
        isDirty = true;
        maybeUpdateView();

        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (isChecked) {
            circleView.setVisibility(View.VISIBLE);
            dotsView.setVisibility(VISIBLE);
            playAnimation();
        } else {
            dotsView.setVisibility(INVISIBLE);
            circleView.setVisibility(View.GONE);
        }
        if (listener != null) {
            listener.onEvent(imageView, isChecked);
        }
    }

    private void setOnTouchListener() {
        if (pressOnTouch) {
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            imageView.animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).setInterpolator(DECELERATE_INTERPOLATOR);
                            setPressed(true);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            break;

                        case MotionEvent.ACTION_UP:
                            imageView.animate().scaleX(1).scaleY(1).setInterpolator(DECELERATE_INTERPOLATOR);
                            if (isPressed()) {
                                performClick();
                                setPressed(false);
                            }
                            break;

                        case MotionEvent.ACTION_CANCEL:
                            imageView.animate().scaleX(1).scaleY(1).setInterpolator(DECELERATE_INTERPOLATOR);
                            break;
                    }
                    return true;
                }
            });
        } else {
            setOnTouchListener(null);
        }
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(com.varunest.sparkbutton.R.layout.layout_spark_button, this, true);

        circleView = (CircleView) findViewById(com.varunest.sparkbutton.R.id.vCircle);
        dotsView = (DotsView) findViewById(com.varunest.sparkbutton.R.id.vDotsView);
        imageView = (ImageView) findViewById(com.varunest.sparkbutton.R.id.ivImage);

        setOnClickListener(this);
        setClipChildren(false);
    }

    public void maybeUpdateView() {
        if (isDirty) {
            isDirty = false;
            circleSize = (int) (imageSize * CIRCLEVIEW_SIZE_FACTOR);
            dotsSize = (int) (imageSize * DOTVIEW_SIZE_FACTOR);

            circleView.setColors(secondaryColor, primaryColor);
            circleView.getLayoutParams().height = circleSize;
            circleView.getLayoutParams().width = circleSize;

            dotsView.getLayoutParams().width = dotsSize;
            dotsView.getLayoutParams().height = dotsSize;
            dotsView.setColors(secondaryColor, primaryColor);
            dotsView.setMaxDotSize((int) (imageSize * DOTS_SIZE_FACTOR));

            imageView.getLayoutParams().height = imageSize;
            imageView.getLayoutParams().width = imageSize;

            if (isChecked) {
                if (activeImageBitmap != null) {
                    imageView.setImageBitmap(activeImageBitmap);
                } else if (inactiveImageBitmap != null) {
                    imageView.setImageBitmap(inactiveImageBitmap);
                }
                imageView.setColorFilter(activeImageTint, PorterDuff.Mode.SRC_ATOP);
            } else {
                if (inactiveImageBitmap != null) {
                    imageView.setImageBitmap(inactiveImageBitmap);
                } else if (activeImageBitmap != null) {
                    imageView.setImageBitmap(activeImageBitmap);
                }
                imageView.setColorFilter(inActiveImageTint, PorterDuff.Mode.SRC_ATOP);
            }

            if (activeImageSource != null) {
                final ImagePipeline imagePipeline = Fresco.getImagePipeline();
                final Uri imageUri = activeImageSource.getUri();
                activeImageSource = null;

                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(imageUri)
                        .setRequestPriority(Priority.HIGH)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();

                DataSource<CloseableReference<CloseableImage>> dataSource =
                        imagePipeline.fetchDecodedImage(imageRequest, getContext());

                dataSource.subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    public void onNewResultImpl(@Nullable Bitmap bitmap) {
                        activeImageBitmap = bitmap.copy(bitmap.getConfig(), true);
                        isDirty = true;
                        maybeUpdateView();
                    }

                    @Override
                    public void onFailureImpl(DataSource dataSource) {
                        Log.e(TAG, "Failed to load image: " + imageUri, dataSource.getFailureCause());
                    }
                }, UiThreadImmediateExecutorService.getInstance());
            }

            if (inactiveImageSource != null) {
                final ImagePipeline imagePipeline = Fresco.getImagePipeline();
                final Uri imageUri = inactiveImageSource.getUri();
                inactiveImageSource = null;

                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(imageUri)
                        .setRequestPriority(Priority.HIGH)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();

                DataSource<CloseableReference<CloseableImage>> dataSource =
                        imagePipeline.fetchDecodedImage(imageRequest, getContext());

                dataSource.subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    public void onNewResultImpl(@Nullable Bitmap bitmap) {
                        inactiveImageBitmap = bitmap.copy(bitmap.getConfig(), true);
                        isDirty = true;
                        maybeUpdateView();
                    }

                    @Override
                    public void onFailureImpl(DataSource dataSource) {
                        Log.e(TAG, "Failed to load image: " + imageUri, dataSource.getFailureCause());
                    }
                }, UiThreadImmediateExecutorService.getInstance());
            }

            setOnTouchListener();
        }
    }

    /*
    private void getStuffFromXML(AttributeSet attr) {
        TypedArray a = getContext().obtainStyledAttributes(attr, com.varunest.sparkbutton.R.styleable.sparkbutton);
        imageSize = a.getDimensionPixelOffset(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_iconSize, Utils.dpToPx(getContext(), 50));
        imageResourceIdActive = a.getResourceId(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_activeImage, INVALID_RESOURCE_ID);
        imageResourceIdInactive = a.getResourceId(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_inActiveImage, INVALID_RESOURCE_ID);
        primaryColor = ContextCompat.getColor(getContext(), a.getResourceId(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_primaryColor, com.varunest.sparkbutton.R.color.spark_primary_color));
        secondaryColor = ContextCompat.getColor(getContext(), a.getResourceId(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_secondaryColor, com.varunest.sparkbutton.R.color.spark_secondary_color));
        activeImageTint = ContextCompat.getColor(getContext(), a.getResourceId(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_activeImageTint, com.varunest.sparkbutton.R.color.spark_image_tint));
        inActiveImageTint = ContextCompat.getColor(getContext(), a.getResourceId(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_inActiveImageTint, com.varunest.sparkbutton.R.color.spark_image_tint));
        pressOnTouch = a.getBoolean(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_pressOnTouch, true);
        animationSpeed = a.getFloat(com.varunest.sparkbutton.R.styleable.sparkbutton_sparkbutton_animationSpeed, 1);
        // recycle typedArray
        a.recycle();
    }*/
}
