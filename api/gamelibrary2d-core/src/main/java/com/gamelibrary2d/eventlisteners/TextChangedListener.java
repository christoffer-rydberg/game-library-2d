package com.gamelibrary2d.eventlisteners;

import com.gamelibrary2d.renderable.Label;

public interface TextChangedListener {
    void onTextChanged(Label label, String before, String after);
}
