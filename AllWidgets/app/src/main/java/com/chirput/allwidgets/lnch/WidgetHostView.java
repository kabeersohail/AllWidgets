package com.chirput.allwidgets.lnch;

import android.view.View;

public interface WidgetHostView {

    View getView();

    int resizeMode();

    boolean isReconfigurable();
}
