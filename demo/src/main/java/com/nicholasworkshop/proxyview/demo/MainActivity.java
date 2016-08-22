package com.nicholasworkshop.proxyview.demo;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nicholasworkshop.proxyview.demo.fragment.ImageViewFragment;
import com.nicholasworkshop.proxyview.demo.fragment.ListViewFragment;
import com.nicholasworkshop.proxyview.demo.fragment.RecyclerViewFragment;
import com.nicholasworkshop.proxyview.demo.fragment.VideoViewFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getFragmentManager()
//                .beginTransaction()
//                .replace(R.id.content, new ImageViewFragment())
//                .replace(R.id.content, new VideoViewFragment())
//                .replace(R.id.content, new ListViewFragment())
//                .replace(R.id.content, new RecyclerViewFragment())
//                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.single_image_view)
    void onSingleImageViewButtonClicked() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new ImageViewFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.single_video_view)
    void onSingleVideoViewButtonClicked() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new VideoViewFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.recycler_view)
    void onRecyclerViewButtonClicked() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new RecyclerViewFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.list_view)
    void onListViewButtonClicked() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new ListViewFragment())
                .addToBackStack(null)
                .commit();
    }
}
