package com.example.androiddemo;

import com.example.framework.android.AbstractGameActivity;

public class MainActivity extends AbstractGameActivity {

    public MainActivity() {
        super(activity -> new DemoGame(activity.getAssets()));
    }
}
