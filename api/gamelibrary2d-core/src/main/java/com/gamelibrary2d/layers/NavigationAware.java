package com.gamelibrary2d.layers;

public interface NavigationAware {

    void onNavigatedTo(NavigationPanel navigationPanel);

    void onNavigatedFrom(NavigationPanel navigationPanel);

}