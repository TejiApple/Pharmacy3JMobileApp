package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.project.pharmacy3jmobileapp.R;

public class HomepageGridViewAdapter extends BaseAdapter {
    Context context;

    public HomepageGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_products, null);
        }
        ImageView imageView = convertView.findViewById(R.id.selectedPhoto);
        imageView.setImageResource(R.drawable.icon_beauty_care);
        return convertView;
    }
}
