package com.gamelibrary2d.objects;

public interface NavigationAware {

    void onNavigatedTo(NavigationPanel navigationPanel);

    void onNavigatedFrom(NavigationPanel navigationPanel);

}