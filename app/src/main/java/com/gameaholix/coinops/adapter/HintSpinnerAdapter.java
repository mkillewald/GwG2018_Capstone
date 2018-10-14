package com.gameaholix.coinops.adapter;

import android.content.Context;
import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gameaholix.coinops.R;

public class HintSpinnerAdapter extends ArrayAdapter<String> {

    public HintSpinnerAdapter(@NonNull Context context, String[] list) {
        super(context, R.layout.support_simple_spinner_dropdown_item, list);
    }

    @NonNull
    @Override
    public boolean isEnabled(int position){
        if (position == 0) {
            // Disable first item which is used for hint.
            return false;
        } else {
            return true;
        }
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);

        TextView textView = (TextView) view;
        if (position == 0) {
            // Set hint text color to gray
            textView.setTextColor(Color.GRAY);
        }
        else {
            // Set all other items text color to black
            textView.setTextColor(Color.BLACK);
        }

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView textView = (TextView) view;
        if (position == 0) {
            // Set hint text color to gray
            textView.setTextColor(Color.GRAY);
        }
        else {
            // Set all other items text color to black
            textView.setTextColor(Color.BLACK);
        }

        return view;
    }
}
