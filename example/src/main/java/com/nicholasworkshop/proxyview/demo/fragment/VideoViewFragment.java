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
        videoView.setVideoURI(Uri.parse("http://r8---sn-p5qlsu7z.googlevideo.com/videoplayback?sparams=dur,expire,id,initcwndbps,ip,ipbits,itag,lmt,mime,mm,mn,ms,mv,nh,pl,ratebypass,source,upn&key=cms1&mime=video%2Fmp4&fexp=9406994%2C9407117%2C9416126%2C9417703%2C9420452%2C9422596%2C9423661%2C9423662%2C9424005%2C9427317%2C9427892%2C9427977%2C9428247%2C9428309%2C9428424%2C9428464%2C9428732&expire=1455398156&dur=596.497&lmt=1429515542551690&signature=21AB688922F91AC2D31D09EA08804C06415F5647.52636A1372460CB242BC5ABB37EAC0D1F0D3ECAE&ip=72.69.70.14&id=5d218157378151b9&ipbits=0&upn=fUbncO4w6h8&pl=24&itag=22&source=youtube&ratebypass=yes&sver=3&title=Big+Buck+Bunny+animation+(1080p+HD)&req_id=14d6b1a4b123a3ee&redirect_counter=2&cms_redirect=yes&mm=26&mn=sn-p5qlsu7z&ms=tsu&mt=1455387020&mv=u"));
        videoView.setMediaController(new MediaController(view.getContext()));
        videoView.start();
    }
}
