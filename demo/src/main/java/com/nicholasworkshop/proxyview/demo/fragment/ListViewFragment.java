package com.nicholasworkshop.proxyview.demo.fragment;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nicholasworkshop.proxyview.OverlayFrameLayout;
import com.nicholasworkshop.proxyview.ProxyView;

import static com.nicholasworkshop.proxyview.MetricUtils.getPxFromDp;

/**
 * Created by nickwph on 2/13/16.
 */
public class ListViewFragment extends ListFragment {

    private static final String TITLE = "List View Demo";
    private static final String IMAGE_1 = "http://www.sample-videos.com/audio/mp3/wave.mp3";
    private static final String VIDEO_1 = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4";

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(TITLE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setListAdapter(new ListViewAdapter());
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public String getItem(int position) {
            return IMAGE_1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_IMAGE;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                // get the overlay view
                OverlayFrameLayout overlayView = OverlayFrameLayout.getInstance(getActivity());

                // create your actual view
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setBackgroundColor(Color.BLACK);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(getPxFromDp(320), getPxFromDp(180)));

                // put the actual view into the proxy view
                ProxyView<ImageView> proxyView = new ProxyView<>(getActivity(), overlayView);
                proxyView.setLayoutParams(new ViewGroup.LayoutParams(getPxFromDp(320), getPxFromDp(180)));
                proxyView.setActualView(imageView);
                proxyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProxyView view = (ProxyView) v;
                        view.setFullscreen(!view.isFullscreen());
                    }
                });

                // set it back to convert view
                convertView = proxyView;
            }

            // get back the actual view
            ProxyView<ImageView> proxyView = (ProxyView<ImageView>) convertView;

            // modify the actual view according the the data you have
            Glide.with(parent.getContext())
                    .load(getItem(position))
                    .into(proxyView.getActualView());

            return proxyView;
        }
    }
}
