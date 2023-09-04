package com.pharos.walker.customview.popupdialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.ListPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class PopupSheet {

    private Activity activity;
    private View rootView;
    private PopupSheetCallback callback;
    private int gravity;
    private List<?> items;
    private int sheetWidth;
    private int popupBgResource;

    private ListPopupWindow popupWindow;

    public PopupSheet(Activity activity, View rootView, List<?> items, PopupSheetCallback callback) {

        this(activity, rootView, items, callback, 0, 0);
    }

    public PopupSheet(Activity activity, View rootView, List<?> items, PopupSheetCallback callback, int sheetWidth) {

        this(activity, rootView, items, callback, sheetWidth, 0);
    }

    public PopupSheet(Activity activity, View rootView, List<?> items, PopupSheetCallback callback, int sheetWidth, int popupBgResource) {
        this.activity = activity;
        this.rootView = rootView;
        this.callback = callback;
        this.sheetWidth = sheetWidth;
        this.popupBgResource = popupBgResource;
        if (items == null){
            items = new ArrayList<>();
        }
        this.items = items;
        gravity = Gravity.END;
    }

    public int getSheetWidth() {
        return sheetWidth;
    }

    public void setSheetWidth(int sheetWidth) {
        this.sheetWidth = sheetWidth;
    }

    public void show(){

        PopupSheetAdapter sheetAdapter = new PopupSheetAdapter(activity, items, callback);

        popupWindow = new ListPopupWindow(activity);
        if (popupBgResource != 0){
            popupWindow.setBackgroundDrawable(activity.getDrawable(popupBgResource));
        }
        popupWindow.setAnchorView(rootView);
        popupWindow.setDropDownGravity(gravity);
        popupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        popupWindow.setWidth(sheetWidth == 0 ? rootView.getWidth()+100 : sheetWidth);
        popupWindow.setAdapter(sheetAdapter);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback.itemClicked(popupWindow, i);

            }
        });
        popupWindow.show();

    }

    public ListPopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void dismiss(){
        if (popupWindow != null){
            popupWindow.dismiss();
        }
    }
}
