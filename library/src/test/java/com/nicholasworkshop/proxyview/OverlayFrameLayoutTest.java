package com.nicholasworkshop.proxyview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.buildActivity;

/**
 * Created by nickwph on 8/6/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class OverlayFrameLayoutTest {

    @Rule public TestName mTestName = new TestName();

    @Spy private Activity mActivity = buildActivity(Activity.class).create().get();
    @Spy private ViewGroup mRootView = new FrameLayout(mActivity);
    @Spy private View mCurtainView = new View(mActivity);

    @Mock private Button mDebugButton;

    private OverlayFrameLayout mOverlayFrameLayout;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        Window window = mock(Window.class);
        when(window.getDecorView()).thenReturn(mRootView);
        when(mActivity.getWindow()).thenReturn(window);
        if (mTestName.getMethodName().startsWith("testGetInstance")) {
            mOverlayFrameLayout = OverlayFrameLayout.getInstance(mActivity);
        } else {
            mOverlayFrameLayout = new OverlayFrameLayout(mActivity, mCurtainView, mDebugButton);
        }
    }

    @Test
    public void testGetInstance() throws Exception {
        verify(mRootView).addView(mOverlayFrameLayout);
    }

    @Test
    public void testGetInstance_ifExistsAlready_thenDoNotCreateNewOne() throws Exception {
        when(mActivity.findViewById(R.id.yahoo_videosdk_proxy_overlay)).thenReturn(mOverlayFrameLayout);
        OverlayFrameLayout overlay = OverlayFrameLayout.getInstance(mActivity);
        verify(mRootView, times(1)).addView(mOverlayFrameLayout);
        assertSame(mOverlayFrameLayout, overlay);
    }

    @Test
    public void testGetDebugButton() throws Exception {
        assertNotNull(mOverlayFrameLayout.getDebugButton());
    }

    @Test
    public void testSetDebugButtonVisible() throws Exception {
        reset(mDebugButton);
        mOverlayFrameLayout.setDebugButtonVisible(true);
        verify(mDebugButton).setVisibility(View.VISIBLE);
        mOverlayFrameLayout.setDebugButtonVisible(false);
        verify(mDebugButton).setVisibility(View.GONE);
    }

    @Test
    public void testGetBlackCurtainView() throws Exception {
        assertNotNull(mOverlayFrameLayout.getBlackCurtainView());
    }

    @Test
    public void testSetBlackCurtainVisible_toTrue() throws Exception {
        mOverlayFrameLayout.setBlackCurtainVisible(true);
        verify(mCurtainView).setClickable(true);
        verify(mCurtainView).animate();
        verify(mCurtainView).bringToFront();
    }

    @Test
    public void testSetBlackCurtainVisible_toFalse() throws Exception {
        mOverlayFrameLayout.setBlackCurtainVisible(false);
        verify(mCurtainView).setClickable(false);
        verify(mCurtainView).animate();
        verify(mCurtainView, never()).bringToFront();
    }

    @Test
    public void testSetBlackCurtainAlpha() throws Exception {
        mOverlayFrameLayout.setBlackCurtainAlpha(0.5f);
        assertEquals(0.5f, mOverlayFrameLayout.getBlackCurtainView().getAlpha(), 0.001);
    }

    @Test
    public void test_debugButtonClickListener() throws Exception {
        ArgumentCaptor<View.OnClickListener> captor = ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(mDebugButton).setOnClickListener(captor.capture());
        captor.getValue().onClick(mDebugButton);
    }
}
