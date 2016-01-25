package robolectric;

import android.animation.Animator;
import android.animation.ValueAnimator;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * Custom shadow for ValueAnimator to just run all the listener methods.
 * <p/>
 * Created by nickwph on 8/6/15.
 */
@Implements(ValueAnimator.class)
public class ShadowValueAnimator extends org.robolectric.shadows.ShadowValueAnimator {

    @RealObject
    private ValueAnimator mRealObject;

    @Implementation
    public void start() {
        for (Animator.AnimatorListener listener : mRealObject.getListeners()) {
            listener.onAnimationStart(mRealObject);
            listener.onAnimationEnd(mRealObject);
            listener.onAnimationCancel(mRealObject);
            listener.onAnimationRepeat(mRealObject);
        }
    }
}
