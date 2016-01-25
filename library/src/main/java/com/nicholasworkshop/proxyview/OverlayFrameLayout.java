package com.nicholasworkshop.proxyview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by nickwph on 4/15/15.
 * <p/>
 * Overlay view always on the root level of activity.
 */
@SuppressLint("ViewConstructor")
public class OverlayFrameLayout extends FrameLayout {

    private static final String TAG = OverlayFrameLayout.class.getSimpleName();

    private Button mDebugButton;
    private View mBlackCurtain;

    public static OverlayFrameLayout getInstance(Activity activity) {
        OverlayFrameLayout overlay = (OverlayFrameLayout) activity.findViewById(R.id.yahoo_videosdk_proxy_overlay);
        if (overlay == null) {
            overlay = new OverlayFrameLayout(activity, new View(activity), new Button(activity));
        }
        return overlay;
    }

    @VisibleForTesting
    OverlayFrameLayout(Activity activity, View blackCurtainView, Button debugButton) {
        super(activity);
        setBackgroundColor(Color.TRANSPARENT);
        setId(R.id.yahoo_videosdk_proxy_overlay);
        attachDebugButton(debugButton);
        attachBlackCurtain(blackCurtainView);
        ViewGroup root = (ViewGroup) activity.getWindow().getDecorView();
        root.addView(this);
    }

    public View getBlackCurtainView() {
        return mBlackCurtain;
    }

    public void setBlackCurtainVisible(boolean visible) {
        mBlackCurtain.animate().alpha(visible ? 1f : 0f).start();
        mBlackCurtain.setClickable(visible);
        if (visible) mBlackCurtain.bringToFront();
    }

    public void setBlackCurtainAlpha(float alpha) {
        mBlackCurtain.setAlpha(alpha);
    }

    public Button getDebugButton() {
        return mDebugButton;
    }

    public void setDebugButtonVisible(boolean visible) {
        mDebugButton.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setGoneToAllViewsExceptView(boolean gone, View view) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != mBlackCurtain && child != mDebugButton && child != view && child instanceof ActualViewWrapper) {
                ActualViewWrapper wrapper = (ActualViewWrapper) child;
                wrapper.setGoneTemporarily(gone);
//                View videoView = wrapper.findViewById(R.id.yahoo_streamingsdk_video_surface);
//                if (videoView != null) {
//                    videoView.setVisibility(gone ? GONE : VISIBLE);
//                }
            }
        }
    }

    private void attachBlackCurtain(View blackCurtainView) {
        mBlackCurtain = blackCurtainView;
        mBlackCurtain.setBackgroundColor(Color.BLACK);
        mBlackCurtain.setAlpha(0f);
        addView(mBlackCurtain);
    }

    private void attachDebugButton(Button debugButton) {
        mDebugButton = debugButton;
        mDebugButton.setText("Dump Hierarchy in Logcat");
        mDebugButton.setOnClickListener(new DebugButtonClickListener());
        mDebugButton.setVisibility(GONE);
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.RIGHT);
        params.setMargins(0, MetricUtils.getPxFromDp(100), MetricUtils.getPxFromDp(10), 0);
        addView(mDebugButton, params);
    }

    private class DebugButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "getChildCount()=" + getChildCount());
            for (int i = 0; i < getChildCount(); i++) {
                Log.e(TAG, "getChildAt(" + i + ")=" + getChildAt(i));
            }
        }
    }
}
