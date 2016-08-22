package com.nicholasworkshop.proxyview.demo.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.nicholasworkshop.proxyview.OverlayFrameLayout;
import com.nicholasworkshop.proxyview.ProxyView;
import com.nicholasworkshop.proxyview.demo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nickwph on 2/13/16.
 */
public class VideoViewFragment extends Fragment {

    @Bind(R.id.container) FrameLayout container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        OverlayFrameLayout overlayView = OverlayFrameLayout.getInstance(getActivity());
        VideoView videoView = new VideoView(view.getContext());

        final ProxyView<VideoView> proxyView = new ProxyView<>(getActivity(), overlayView);
        proxyView.setActualView(videoView);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                proxyView.setFullscreen(!proxyView.isFullscreen());
                return true;
            }
        });

        container.addView(proxyView);
        videoView.setVideoURI(Uri.parse("http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4"));
        videoView.setMediaController(new MediaController(view.getContext()));
        videoView.start();
    }
}
