package com.nicholasworkshop.proxyview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by nickwph on 8/6/15.
 */
@SuppressWarnings("unchecked")
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class ActualViewWrapperTest {

    private static final Rect BOUNDS = new Rect(1, 2, 3, 4);

    @Mock private Canvas mCanvas;
    @Mock private View mView;

    private ActualViewWrapper mActualViewWrapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mActualViewWrapper = spy(new ActualViewWrapper(RuntimeEnvironment.application));
    }

    @Test
    public void testSetView() throws Exception {
        mActualViewWrapper.setView(mView);
        verify(mActualViewWrapper).addView(mView);
    }

    @Test
    public void testSetView_ifSameView_thenDoNotUpdateView() throws Exception {
        mActualViewWrapper.setView(mView);
        mActualViewWrapper.setView(mView);
        verify(mActualViewWrapper, times(1)).addView(mView);
        verify(mActualViewWrapper, never()).removeView(any(View.class));
    }

    @Test
    public void testSetView_ifViewExists_theReplaceOldOne() throws Exception {
        View view = mock(View.class);
        mActualViewWrapper.setView(mView);
        mActualViewWrapper.setView(view);
        verify(mActualViewWrapper).addView(mView);
        verify(mActualViewWrapper).removeView(mView);
        verify(mActualViewWrapper).addView(view);
    }

    @Test
    public void testSetView_ifSettingNull_theReplaceOldWithNull() throws Exception {
        mActualViewWrapper.setView(mView);
        mActualViewWrapper.setView(null);
        verify(mActualViewWrapper).addView(mView);
        verify(mActualViewWrapper).removeView(mView);
    }

    @Test
    public void testGetView() throws Exception {
        mActualViewWrapper.setView(mView);
        assertEquals(mView, mActualViewWrapper.getView());
    }

    @Test
    public void testGetView_ifViewNotSet_thenReturnNull() throws Exception {
        assertEquals(null, mActualViewWrapper.getView());
    }

    @Test
    public void testHasView() throws Exception {
        mActualViewWrapper.setView(mView);
        assertEquals(true, mActualViewWrapper.hasView());
    }

    @Test
    public void testHasView_ifViewNotSet_thenReturnFalse() throws Exception {
        assertEquals(false, mActualViewWrapper.hasView());
    }

    @Test
    public void testDraw() throws Exception {
        mActualViewWrapper.draw(mCanvas);
        verify(mCanvas, never()).clipRect(BOUNDS);
        verify(mActualViewWrapper).draw(mCanvas);
    }

    @Test
    public void testDraw_ifApiBelow18_thenUseCustomClipBounds() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.setClipBounds(BOUNDS);
        mActualViewWrapper.draw(mCanvas);
        verify(mCanvas).clipRect(new Rect(BOUNDS));
        verify(mActualViewWrapper).draw(mCanvas);
    }

    @Test
    public void testDraw_ifApiBelow18AndNoCustomClipBounds_thenNothingSpecial() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.draw(mCanvas);
        verify(mCanvas, never()).clipRect(BOUNDS);
        verify(mActualViewWrapper).draw(mCanvas);
    }

    @Test
    public void testSetClipBounds() throws Exception {
        mActualViewWrapper.setClipBounds(BOUNDS);
        verify(mActualViewWrapper).setClipBounds(BOUNDS);
    }

    @Test
    public void testSetClipBounds_ifApiBelow18_thenCreateCustomClipBoundsAndInvalidate() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.setClipBounds(BOUNDS);
        verify(mActualViewWrapper).invalidate();
    }

    @Test
    public void testSetClipBounds_ifApiBelow18AndSettingNullBounds_thenInvalidateAndSetClipBoundsToNull() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.setClipBounds(BOUNDS);
        mActualViewWrapper.setClipBounds(null);
        verify(mActualViewWrapper, times(2)).invalidate();
        assertEquals(null, mActualViewWrapper.getClipBounds());
    }

    @Test
    public void testSetClipBounds_ifApiBelow18AndSameBounds_thenDoNothing() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.setClipBounds(BOUNDS);
        mActualViewWrapper.setClipBounds(BOUNDS);
    }

    @Test
    public void testSetClipBounds_ifApiBelow18AndHasBoundsAlready_thenInvalidateNewBounds() throws Exception {
        Rect newBounds = new Rect(5, 6, 7, 8);
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.setClipBounds(BOUNDS);
        mActualViewWrapper.setClipBounds(newBounds);
        verify(mActualViewWrapper).invalidate(
                Math.min(BOUNDS.left, newBounds.left),
                Math.min(BOUNDS.top, newBounds.top),
                Math.max(BOUNDS.right, newBounds.right),
                Math.max(BOUNDS.bottom, newBounds.bottom));
    }

    @Test
    public void testGetClipBounds() throws Exception {
        mActualViewWrapper.setClipBounds(BOUNDS);
        assertEquals(new Rect(BOUNDS), mActualViewWrapper.getClipBounds());
    }

    @Test
    public void testGetClipBounds_ifApiBelow18_thenReturnRectFromCustomClipBounds() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        mActualViewWrapper.setClipBounds(BOUNDS);
        assertEquals(new Rect(BOUNDS), mActualViewWrapper.getClipBounds());
    }

    @Test
    public void testGetClipBounds_ifApiBelow18AndClipBoundNotSet_thenReturnNull() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 17);
        assertEquals(null, mActualViewWrapper.getClipBounds());
    }
}