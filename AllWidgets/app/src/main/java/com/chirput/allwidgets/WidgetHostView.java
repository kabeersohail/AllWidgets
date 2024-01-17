package com.chirput.allwidgets;

import android.view.View;

public interface WidgetHostView {

    View getView();

    int resizeMode();

    boolean isReconfigurable();
}
