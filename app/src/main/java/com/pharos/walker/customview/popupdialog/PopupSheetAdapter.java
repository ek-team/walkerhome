package com.pharos.walker.customview.popupdialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PopupSheetAdapter extends ArrayAdapter {

    private PopupSheetCallback callback;

    public PopupSheetAdapter(@NonNull Context context, @NonNull List<?> items, PopupSheetCallback callback) {
        super(context, 0, 0, items);
        this.callback = callback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return callback.setupItemView(position);
    }
}
