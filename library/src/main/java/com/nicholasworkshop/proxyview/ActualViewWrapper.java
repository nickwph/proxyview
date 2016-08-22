package com.nicholasworkshop.proxyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by Nicholas Wong on 4/22/15.
 * <p/>
 * Wrapper for the actual view.
 */
public class ActualViewWrapper<ViewType extends View> extends FrameLayout {

    private ViewType mActualView;
    private Rect mClipBounds;
    private int mSavedVisibility = -1;

    public ActualViewWrapper(Context context) {
        super(context);
        setWillNotDraw(false); // so draw() and onDraw() will be triggered
    }

    public void setView(ViewType view) {
        if (mActualView != view) {
            if (mActualView != null) {
                removeView(mActualView);
            }
            if (view != null) {
                addView(view);
            }
            mActualView = view; // view can be null
        }
    }

    public ViewType getView() {
        return mActualView;
    }

    public boolean hasView() {
        return mActualView != null;
    }

    @Override
    public void setVisibility(int visibility) {
        if (mSavedVisibility == -1) {
            super.setVisibility(visibility);
        } else {
            mSavedVisibility = visibility;
        }
    }

    public void setGoneTemporarily(boolean gone) {
        if (gone) {
            if (mSavedVisibility == -1) {
                mSavedVisibility = getVisibility();
                super.setVisibility(GONE);
            }
        } else {
            if (mSavedVisibility != -1) {
                //noinspection ResourceType
                setVisibility(mSavedVisibility);
                mSavedVisibility = -1;
            }
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (SDK_INT < 18 && mClipBounds != null) {
            canvas.clipRect(mClipBounds);
        }
        super.draw(canvas);
    }

    @Override
    @SuppressLint("NewApi")
    public void setClipBounds(Rect clipBounds) {
        if (SDK_INT >= 18) {
            super.setClipBounds(clipBounds);
            return;
        }

        if (clipBounds != null) {
            if (clipBounds.equals(mClipBounds)) {
                return;
            }
            if (mClipBounds == null) {
                invalidate();
                mClipBounds = new Rect(clipBounds);
            } else {
                invalidate(
                        Math.min(mClipBounds.left, clipBounds.left),
                        Math.min(mClipBounds.top, clipBounds.top),
                        Math.max(mClipBounds.right, clipBounds.right),
                        Math.max(mClipBounds.bottom, clipBounds.bottom));
                mClipBounds.set(clipBounds);
            }
        } else {
            if (mClipBounds != null) {
                invalidate();
                mClipBounds = null;
            }
        }
    }

    @Override
    public Rect getClipBounds() {
        if (SDK_INT >= 18) {
            return super.getClipBounds();
        } else {
            return (mClipBounds != null) ? new Rect(mClipBounds) : null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
