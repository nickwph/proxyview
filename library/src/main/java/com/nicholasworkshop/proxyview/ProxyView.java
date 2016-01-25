package com.nicholasworkshop.proxyview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;
import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Created by nickwph on 4/14/15.
 * <p/>
 * Proxy view that reports position and size to the actual view.
 */
@SuppressLint("ViewConstructor")
public class ProxyView<ViewType extends View> extends View {

    private static final String TAG = ProxyView.class.getSimpleName();

    // flags
    private boolean mIsDebug;
    private boolean mIsAnimating;

    // views needed
    private final OverlayFrameLayout mOverlay;
    private final ActualViewWrapper<ViewType> mActualViewWrapper;

    // saved values before going to fullscreen
    private int mSavedVisibility;
    private int mSavedSystemUiVisibility;
    private ViewGroup.LayoutParams mSavedLayoutParams;

    public ProxyView(Context context, OverlayFrameLayout overlay) {
        this(context, overlay, new ActualViewWrapper<ViewType>(context));
    }

    @VisibleForTesting
    ProxyView(Context context, OverlayFrameLayout overlay, ActualViewWrapper<ViewType> wrapper) {
        super(context);
        mOverlay = overlay;
        mActualViewWrapper = wrapper;
        mActualViewWrapper.getViewTreeObserver().addOnPreDrawListener(new PreDrawListener());
        mActualViewWrapper.setOnTouchListener(new TouchListener());
    }

    public void setFullscreen(boolean fullscreen) {
        if (fullscreen) {
            enterFullscreen();
        } else {
            exitFullscreen();
        }
    }

    public ViewType getActualView() {
        return mActualViewWrapper.getView();
    }

    public void setActualView(ViewType view) {
        if (mActualViewWrapper.getView() != view) {
            mActualViewWrapper.setView(null);
            mOverlay.removeView(mActualViewWrapper);
        }
        if (view != null) {
            mActualViewWrapper.setView(view);
            mOverlay.addView(mActualViewWrapper, 0);
        }
    }

    public boolean isFullscreen() {
        return mSavedLayoutParams != null;
    }

    public void setIsDebug(boolean isDebug) {
        mIsDebug = isDebug;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mActualViewWrapper.setVisibility(visibility);
        mSavedVisibility = visibility;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mActualViewWrapper.setVisibility(visibility);
        mSavedVisibility = visibility;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (!isFullscreen()) {
            mActualViewWrapper.setLayoutParams(new FrameLayout.LayoutParams(params.width, params.height));
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        mActualViewWrapper.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isFullscreen() && mActualViewWrapper.getView() != null && !mIsAnimating) {
            invalidateFullScreenSize();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isFullscreen() && mActualViewWrapper.getView() != null && !mIsAnimating) {
            invalidateFullScreenSize();
        }
    }

    private void invalidateFullScreenSize() {
        // get current target size
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = ScreenDimensionUtils.getScreenSize(manager);
        float targetW = screenSize.x;
        float targetH = screenSize.y;
        if (mIsDebug) {
            Log.d(TAG, "targetW=" + targetW);
            Log.d(TAG, "targetH=" + targetH);
        }

        // calculate ratio from current size
        float currentW = mActualViewWrapper.getWidth();
        float currentH = mActualViewWrapper.getHeight();
        float ratioW = targetW / currentW;
        float ratioH = targetH / currentH;
        float ratio = min(ratioW, ratioH);
        if (mIsDebug) {
            Log.d(TAG, "ratioW=" + ratioW);
            Log.d(TAG, "ratioH=" + ratioH);
            Log.d(TAG, "ratio=" + ratio);
        }

        // create the finishing size
        AnimatorDataHolder end = new AnimatorDataHolder();
        end.w = (int) (currentW * ratio);
        end.h = (int) (currentH * ratio);
        end.x = (targetW - end.w) / 2;
        end.y = (targetH - end.h) / 2;

        // create animator and start
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.addUpdateListener(createAnimatorUpdateListener(mActualViewWrapper, end));
        animator.addListener(new FullscreenEnterAnimatorListener());
        animator.start();
    }

    private void enterFullscreen() {
        if (!isFullscreen() && mActualViewWrapper.getView() != null && !mIsAnimating) {
            Log.i(TAG, "Going fullscreen...");
            hideSystemUi(true);
            mActualViewWrapper.setClickable(true);
            mSavedLayoutParams = new FrameLayout.LayoutParams(mActualViewWrapper.getLayoutParams());
            invalidateFullScreenSize();
        }
    }

    private void exitFullscreen() {
        if (isFullscreen() && mActualViewWrapper.getView() != null && !mIsAnimating) {
            Log.i(TAG, "Exiting fullscreen...");
            restoreSystemUi();
            mActualViewWrapper.setClickable(false);

            // restore status and navigation bar
            setSystemUiVisibility(mSavedSystemUiVisibility);

            // calculate the final size and position
            int[] location = new int[2];
            getLocationInWindow(location);
            AnimatorDataHolder end = new AnimatorDataHolder();
            end.x = location[0];
            end.y = location[1];
            end.w = mSavedLayoutParams.width;
            end.h = mSavedLayoutParams.height;

            // create animator and start
            ValueAnimator animator = ValueAnimator.ofInt(0, 100);
            animator.addUpdateListener(createAnimatorUpdateListener(mActualViewWrapper, end));
            animator.addListener(new FullscreenExitAnimatorListener());
            animator.start();
        }
    }

    private void restoreSystemUi() {
        // restore status and navigation bar
        setSystemUiVisibility(mSavedSystemUiVisibility);
    }

    private void hideSystemUi(boolean saveCurrentSetting) {
        if (saveCurrentSetting) {
            mSavedSystemUiVisibility = getSystemUiVisibility();
        }
        // hide status and navigation bar
        setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE | SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private class AnimatorDataHolder {
        public int w, h;
        public float x, y;

        @Override
        public String toString() {
            return String.format("w=%d h=%d x=%d y=%d", w, h, (int) x, (int) y);
        }
    }

    private AnimatorDataHolder createCurrentAnimatorDataHolder(View view) {
        AnimatorDataHolder holder = new AnimatorDataHolder();
        holder.x = view.getTranslationX();
        holder.y = view.getTranslationY();
        holder.w = view.getWidth();
        holder.h = view.getHeight();
        return holder;
    }

    private ValueAnimator.AnimatorUpdateListener createAnimatorUpdateListener(View view, AnimatorDataHolder end) {
        return createAnimatorUpdateListener(view, createCurrentAnimatorDataHolder(view), end);
    }

    private ValueAnimator.AnimatorUpdateListener createAnimatorUpdateListener(final View view, final AnimatorDataHolder start, final AnimatorDataHolder end) {
        if (mIsDebug) {
            Log.d(TAG, "animator-from: " + start);
            Log.d(TAG, "animator-to:   " + end);
        }
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float antiFraction = 1 - fraction;
                view.setTranslationX(end.x * fraction + start.x * antiFraction);
                view.setTranslationY(end.y * fraction + start.y * antiFraction);
                view.getLayoutParams().width = (int) (end.w * fraction + start.w * antiFraction);
                view.getLayoutParams().height = (int) (end.h * fraction + start.h * antiFraction);
                view.requestLayout();
            }
        };
    }

    @SuppressLint("NewApi")
    private void updatePosition() {
        if (!isFullscreen() && !mIsAnimating) {
            int[] location = new int[2];
            Rect bounds = new Rect();
            getLocationInWindow(location);
            boolean shown = isShown();
            boolean visible = getLocalVisibleRect(bounds);

            // update actual view position
            if (shown) {
                mActualViewWrapper.setTranslationY(location[1]);
                mActualViewWrapper.setTranslationX(location[0]);
                mActualViewWrapper.getLayoutParams().height = getHeight();
                mActualViewWrapper.getLayoutParams().width = getWidth();
            }
            // clip actual view
            if (shown && visible) {
                mActualViewWrapper.setVisibility(mSavedVisibility == VISIBLE ? VISIBLE : mSavedVisibility);
                mActualViewWrapper.setClipBounds(bounds);
            } else {
                mActualViewWrapper.setVisibility(GONE);
            }
            // show debug message if in debug mode
            if (mIsDebug) {
                Log.e(TAG, "DEBUG:" +
                        " mLocationInWindow=(" + location[0] + "," + location[1] + ")" +
                        " mClipBounds=" + bounds +
                        " isShown()=" + shown +
                        " getLocalVisibleRect()=" + visible);
            }
        }
    }

    private class PreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            updatePosition();
            return true;
        }
    }

    private class KeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (isFullscreen() && event.getAction() == ACTION_UP && keyCode == KEYCODE_BACK) {
                Log.e(TAG, "Taking over back event... " + event);
                setFullscreen(false);
                return true;
            }
            return false;
        }
    }

    private class FullscreenEnterAnimatorListener implements Animator.AnimatorListener {

        @Override
        @SuppressLint("NewApi")
        public void onAnimationStart(Animator animation) {
            mIsAnimating = true;
            mOverlay.setBlackCurtainVisible(true);
            mOverlay.setGoneToAllViewsExceptView(true, mActualViewWrapper);
            mActualViewWrapper.bringToFront();
            mActualViewWrapper.setClipBounds(null);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mIsAnimating = false;
            mOverlay.setOnKeyListener(new KeyListener());
            mOverlay.setFocusableInTouchMode(true);
            boolean granted = mOverlay.requestFocusFromTouch();
            if (!granted) {
                Log.e(TAG, "Unable to request focus on the overlay view!");
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

    private class FullscreenExitAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            mIsAnimating = true;
            mOverlay.setBlackCurtainVisible(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mOverlay.setGoneToAllViewsExceptView(false, mActualViewWrapper);
            mSavedLayoutParams = null;
            mIsAnimating = false;
            updatePosition();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

    private class TouchListener implements OnTouchListener {

        private float mDeltaX, mDeltaY;
        private float mOriginalX, mOriginalY;
        private boolean mIsPositionDirty;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (!mIsAnimating && isFullscreen()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + mDeltaX;
                        float newY = event.getRawY() + mDeltaY;
                        view.setTranslationX(newX);
                        view.setTranslationY(newY);
                        updateOverlayAlpha(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (calculateDistanceFraction(view) <= 0.4f) {
                            exitFullscreen();
                        } else {
                            hideSystemUi(false);
                            ViewCompat.animate(view)
                                    .translationX(mOriginalX)
                                    .translationY(mOriginalY)
                                    .setUpdateListener(mUpdateListener)
                                    .setListener(mListener)
                                    .start();
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        restoreSystemUi();
                        mDeltaX = view.getTranslationX() - event.getRawX();
                        mDeltaY = view.getTranslationY() - event.getRawY();
                        if (!mIsPositionDirty) {
                            mOriginalX = view.getTranslationX();
                            mOriginalY = view.getTranslationY();
                        }
                        break;
                }
                return true;
            } else {
                return false;
            }
        }

        private float calculateDistanceFraction(View view) {
            float fractionX = 1 - abs(view.getTranslationX() - mOriginalX) / view.getWidth();
            float fractionY = 1 - abs(view.getTranslationY() - mOriginalY) / view.getHeight();
            return fractionX * fractionY;
        }

        private void updateOverlayAlpha(View view) {
            float alpha = calculateDistanceFraction(view);
            mOverlay.setBlackCurtainAlpha(alpha);
        }

        private ViewPropertyAnimatorUpdateListener mUpdateListener = new ViewPropertyAnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(View view) {
                updateOverlayAlpha(view);
            }
        };

        private ViewPropertyAnimatorListener mListener = new ViewPropertyAnimatorListener() {

            @Override
            public void onAnimationStart(View view) {
                mIsPositionDirty = true;
            }

            @Override
            public void onAnimationEnd(View view) {
                mIsPositionDirty = false;
            }

            @Override
            public void onAnimationCancel(View view) {
            }
        };
    }
}