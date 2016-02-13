package com.nicholasworkshop.proxyview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getFragmentManager()
                .beginTransaction()
//                .replace(R.id.content, new ImageViewFragment())
//                .replace(R.id.content, new VideoViewFragment())
                .replace(R.id.content, new ListViewFragment())
                .commit();
    }
}
