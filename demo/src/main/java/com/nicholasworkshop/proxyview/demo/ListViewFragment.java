package com.nicholasworkshop.proxyview.demo;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nicholasworkshop.proxyview.OverlayFrameLayout;
import com.nicholasworkshop.proxyview.ProxyView;

import timber.log.Timber;

import static com.nicholasworkshop.proxyview.MetricUtils.getPxFromDp;

/**
 * Created by nickwph on 2/13/16.
 */
public class ListViewFragment extends ListFragment {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public String getItem(int position) {
                return "http://www.hdwallpapersnew.net/wp-content/uploads/2015/03/beautiful-nature-wallpaper-snow-hd-desktop-background-free-pictures.jpg";
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
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    OverlayFrameLayout overlayView = OverlayFrameLayout.getInstance(getActivity());
                    ImageView imageView = new ImageView(parent.getContext());
                    imageView.setBackgroundColor(Color.BLACK);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(getPxFromDp(320), getPxFromDp(180)));

                    final ProxyView<ImageView> proxyView = new ProxyView<>(getActivity(), overlayView);
                    proxyView.setLayoutParams(new ViewGroup.LayoutParams(getPxFromDp(320), getPxFromDp(180)));
                    proxyView.setActualView(imageView);
                    convertView = proxyView;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            proxyView.setFullscreen(!proxyView.isFullscreen());
                        }
                    });
                }

                ProxyView<ImageView> proxyView = (ProxyView<ImageView>) convertView;
                proxyView.getActualView();
                Timber.e(getItem(position));
                Glide.with(parent.getContext())
                        .load(getItem(position))
                        .into(proxyView.getActualView());

                return proxyView;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
