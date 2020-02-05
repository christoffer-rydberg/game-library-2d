package com.gamelibrary2d.markers;

import com.gamelibrary2d.layers.NavigationPanel;

public interface NavigationAware {

    void onNavigatedTo(NavigationPanel navigationPanel);

    void onNavigatedFrom(NavigationPanel navigationPanel);

}