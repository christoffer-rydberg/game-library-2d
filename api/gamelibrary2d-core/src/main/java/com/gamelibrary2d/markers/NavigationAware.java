package com.gamelibrary2d.markers;

import com.gamelibrary2d.layers.NavigationPanel;

public interface NavigationAware {

    void navigatedTo(NavigationPanel navigationPanel);

    void navigatedFrom(NavigationPanel navigationPanel);

}