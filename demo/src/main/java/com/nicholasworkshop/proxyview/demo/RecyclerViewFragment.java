package com.nicholasworkshop.proxyview.demo;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nicholasworkshop.proxyview.OverlayFrameLayout;
import com.nicholasworkshop.proxyview.ProxyView;

import static com.nicholasworkshop.proxyview.MetricUtils.getPxFromDp;

/**
 * Created by nickwph on 2/13/16.
 */
public class RecyclerViewFragment extends Fragment {

    private static final String TITLE = "Recycler View Demo";
    private static final String IMAGE_1 = "http://goo.gl/BhK1vP";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

            return new RecyclerView.ViewHolder(proxyView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // get back the actual view
            ProxyView<ImageView> proxyView = (ProxyView<ImageView>) holder.itemView;

            // modify the actual view according the the data you have
            Glide.with(proxyView.getContext())
                    .load(IMAGE_1)
                    .into(proxyView.getActualView());
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_IMAGE;
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}
