package com.pharos.walker.customview.popupdialog;

import android.view.View;

import androidx.appcompat.widget.ListPopupWindow;

public interface PopupSheetCallback {
    View setupItemView(int position);
    void itemClicked(ListPopupWindow popupWindow, int position);
}
