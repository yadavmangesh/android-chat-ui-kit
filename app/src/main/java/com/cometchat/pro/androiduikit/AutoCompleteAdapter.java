package com.cometchat.pro.androiduikit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.cometchat.pro.uikit.Avatar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter {

    private int resource;
    private List<Option> optionList;
    private LayoutInflater layoutInflater;

    public AutoCompleteAdapter(Context context, int resource, List<Option> optionList) {
        super(context, resource, optionList);

        this.optionList = optionList;
        this.resource = resource;

        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public Object getItem(int position) {
        return optionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return optionList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = layoutInflater.inflate(resource, parent, false);

        Option option = optionList.get(position);
        MaterialButton userBlock = rootView.findViewById(R.id.userBlock);
        userBlock.setIcon(option.getDrawable());
        userBlock.setIconTint(null);
        userBlock.setIconSize(100);
        userBlock.setText(option.getName());
        userBlock.setTag(option.getId());

        return rootView;
    }


}
