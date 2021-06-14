package com.gamelibrary2d.components.denotations;

import com.gamelibrary2d.components.containers.NavigationPanel;

public interface NavigationAware {

    void navigatedTo(NavigationPanel navigationPanel);

    void navigatedFrom(NavigationPanel navigationPanel);

}