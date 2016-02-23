package com.nicholasworkshop.proxyview.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nicholasworkshop.proxyview.OverlayFrameLayout;
import com.nicholasworkshop.proxyview.ProxyView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nickwph on 2/13/16.
 */
public class ImageViewFragment extends Fragment {

    @Bind(R.id.container) FrameLayout container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        // get the overlay view
        OverlayFrameLayout overlayView = OverlayFrameLayout.getInstance(getActivity());

        // create the actual view
        ImageView imageView = new ImageView(view.getContext());

        // put the actual view into the proxy view
        ProxyView<ImageView> proxyView = new ProxyView<>(getActivity(), overlayView);
        proxyView.setActualView(imageView);
        proxyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProxyView view = (ProxyView) v;
                view.setFullscreen(!view.isFullscreen());
            }
        });

        container.addView(proxyView);
        Glide.with(view.getContext())
                .load("http://www.hdwallpapersnew.net/wp-content/uploads/2015/03/beautiful-nature-wallpaper-snow-hd-desktop-background-free-pictures.jpg")
                .centerCrop()
                .into(imageView);
    }
}
