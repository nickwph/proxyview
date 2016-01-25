package com.nicholasworkshop.proxyview;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import robolectric.ShadowValueAnimator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.buildActivity;

/**
 * Created by nickwph on 8/5/15.
 */
@SuppressWarnings("unchecked")
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml", shadows = {ShadowValueAnimator.class})
public class ProxyViewTest {

    @Spy private Activity mActivity = buildActivity(Activity.class).create().get();
    @Spy private View mActualView = new View(mActivity);
    @Spy private OverlayFrameLayout mOverlay = OverlayFrameLayout.getInstance(mActivity);
    @Spy private ActualViewWrapper mActualViewWrapper = new ActualViewWrapper(mActivity);

    private ProxyView mProxyView;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mProxyView = new ProxyView(mActivity, mOverlay, mActualViewWrapper);
        when(mActualViewWrapper.getLayoutParams()).thenReturn(mock(FrameLayout.LayoutParams.class));
    }

    @Test
    public void testProxyView() throws Exception {
        mProxyView = new ProxyView(mActivity, mOverlay);
    }

    @Test
    public void testSetFullscreen_toTrue() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        verify(mOverlay).setBlackCurtainVisible(true);
        verify(mActualViewWrapper).setClickable(true);
        verify(mActualViewWrapper).bringToFront();
        verify(mActualViewWrapper).setClipBounds(null);
        assertEquals(true, mProxyView.isFullscreen());
    }

    @Test
    public void testSetFullscreen_toTrue_whenActualViewNotSet_thenDoNothing() throws Exception {
        mProxyView.setFullscreen(true);
        verify(mOverlay, never()).setBlackCurtainVisible(anyBoolean());
        verify(mActualViewWrapper, never()).setClickable(anyBoolean());
        verify(mActualViewWrapper, never()).bringToFront();
        verify(mActualViewWrapper, never()).setClipBounds(null);
        assertEquals(false, mProxyView.isFullscreen());
    }

    @Test
    public void testSetFullscreen_toTrue_whenFullScreenAlready_thenDoNothing() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        mProxyView.setFullscreen(true);
        verify(mOverlay, times(1)).setBlackCurtainVisible(anyBoolean());
        verify(mActualViewWrapper, times(1)).setClickable(anyBoolean());
        verify(mActualViewWrapper, times(1)).bringToFront();
        verify(mActualViewWrapper, times(1)).setClipBounds(null);
        assertEquals(true, mProxyView.isFullscreen());
    }

    @Test
    public void testSetFullscreen_toFalse() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        mProxyView.setFullscreen(false);
        verify(mActualViewWrapper).setClickable(false);
        verify(mOverlay).setBlackCurtainVisible(false);
        assertEquals(false, mProxyView.isFullscreen());
    }

    @Test
    public void testSetFullscreen_toFalse_whenActualViewNotSet_thenDoNothing() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        mProxyView.setActualView(null);
        mProxyView.setFullscreen(false);
        verify(mActualViewWrapper, times(1)).setClickable(anyBoolean());
        verify(mOverlay, times(1)).setBlackCurtainVisible(anyBoolean());
        assertEquals(true, mProxyView.isFullscreen());
    }

    @Test
    public void testSetFullscreen_toFalse_whenItIsNotFullScreen_thenDoNothing() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(false);
        verify(mActualViewWrapper, never()).setClickable(anyBoolean());
        verify(mOverlay, never()).setBlackCurtainVisible(anyBoolean());
        assertEquals(false, mProxyView.isFullscreen());
    }

    @Test
    public void testGetActualView() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.getActualView();
        assertEquals(mActualView, mProxyView.getActualView());
    }

    @Test
    public void testSetActualView() throws Exception {
        mProxyView.setActualView(mActualView);
        verify(mActualViewWrapper).setView(mActualView);
        verify(mOverlay).addView(mActualViewWrapper, 0);
    }

    @Test
    public void testIsFullscreen() throws Exception {
        mProxyView.isFullscreen();
    }

    @Test
    public void testSetIsDebug() throws Exception {
        mProxyView.setIsDebug(true);
    }

    @Test
    public void testOnVisibilityChanged() throws Exception {
        mProxyView.onVisibilityChanged(mock(View.class), View.VISIBLE);
        verify(mActualViewWrapper).setVisibility(View.VISIBLE);
    }

    @Test
    public void testSetLayoutParams() throws Exception {
        ViewGroup.LayoutParams params = mock(ViewGroup.LayoutParams.class);
        mProxyView.setLayoutParams(params);
        verify(mActualViewWrapper).setLayoutParams(any(FrameLayout.LayoutParams.class));
    }

    @Test
    public void testKeyListener() throws Exception {
        mProxyView = spy(mProxyView);
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        boolean result = captureKeyListener().onKey(mock(View.class), KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        verify(mProxyView).setFullscreen(false);
        assertEquals(true, result);
    }

    @Test
    public void testKeyListener_whenNotActionUp_thenDoNotConsume() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        boolean result = captureKeyListener().onKey(mock(View.class), KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        assertEquals(false, result);
    }

    @Test
    public void testKeyListener_whenNotBackKeyCode_thenDoNotConsume() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        boolean result = captureKeyListener().onKey(mock(View.class), KeyEvent.KEYCODE_0, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_0));
        assertEquals(false, result);
    }

    @Test
    public void testKeyListener_whenNotFullScreen_thenDoNotConsume() throws Exception {
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        mProxyView.setFullscreen(false);
        boolean result = captureKeyListener().onKey(mock(View.class), KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        assertEquals(false, result);
    }

    @Test
    public void testTouchListener_withActionMove() throws Exception {
        View view = spy(new View(mActivity));
        MotionEvent event = MotionEvent.obtain(1, 2, MotionEvent.ACTION_MOVE, 3, 4, 5);
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        assertEquals(true, mProxyView.isFullscreen());
        boolean result = captureTouchListener().onTouch(view, event);
        event.recycle();
        verify(view).setTranslationX(anyInt());
        verify(view).setTranslationY(anyInt());
        assertEquals(true, result);
    }

    @Test
    public void testTouchListener_withActionUp() throws Exception {
        View view = new View(mActivity);
        MotionEvent event = MotionEvent.obtain(1, 2, KeyEvent.ACTION_UP, 3, 4, 5);
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        boolean result = captureTouchListener().onTouch(view, event);
        event.recycle();
        assertEquals(true, result);
    }

    @Test
    public void testTouchListener_withActionDown() throws Exception {
        View view = new View(mActivity);
        MotionEvent event = MotionEvent.obtain(1, 2, KeyEvent.ACTION_DOWN, 3, 4, 5);
        mProxyView.setActualView(mActualView);
        mProxyView.setFullscreen(true);
        boolean result = captureTouchListener().onTouch(view, event);
        event.recycle();
        assertEquals(true, result);
    }

    @Test
    public void testTouchListener_ifNotFullScreen_thenNotConsume() throws Exception {
        View view = mock(View.class);
        MotionEvent event = MotionEvent.obtain(1, 2, KeyEvent.ACTION_DOWN, 3, 4, 5);
        boolean result = captureTouchListener().onTouch(view, event);
        event.recycle();
        assertEquals(false, result);
    }


    private View.OnKeyListener captureKeyListener() {
        ArgumentCaptor<View.OnKeyListener> captor = ArgumentCaptor.forClass(View.OnKeyListener.class);
        verify(mOverlay).setOnKeyListener(captor.capture());
        return captor.getValue();
    }

    private View.OnTouchListener captureTouchListener() {
        ArgumentCaptor<View.OnTouchListener> captor = ArgumentCaptor.forClass(View.OnTouchListener.class);
        verify(mActualViewWrapper).setOnTouchListener(captor.capture());
        return captor.getValue();
    }
}