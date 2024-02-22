package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.pharmacy3jmobileapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomepageFilterAdapter extends BaseAdapter {

    Context context;

    ArrayList<String> filterList;
    SharedPreferences sp;

    public HomepageFilterAdapter(Context context, ArrayList<String> filterList, SharedPreferences sp) {
        this.context = context;
        this.filterList = filterList;
        this.sp = sp;
    }

    @Override
    public int getCount() {
        return filterList.size();
    }

    @Override
    public Object getItem(int position) {
        return filterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_filter, null, true);
        }
        sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        CheckBox filters = convertView.findViewById(R.id.tvFilterItem);

        ArrayList<Integer> selectedFilterPosition1 = new ArrayList<>();
        String selectedFilter1 = sp.getString("selectedFilter", "");

        if (!selectedFilter1.isEmpty()){
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(selectedFilter1);

            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group());
                selectedFilterPosition1.add(number);
            }
        }

        for (int i = 0; i < selectedFilterPosition1.size(); i++){
            if (position == selectedFilterPosition1.get(i)){
                filters.setChecked(true);
                filters.setBackgroundColor(Color.parseColor("#faa63e"));
            }
        }

        filters.setText(filterList.get(position));
        filters.setOnClickListener(v -> {
            if (filters.isChecked()){
                filters.setBackgroundColor(Color.parseColor("#faa63e"));
                ArrayList<Integer> selectedFilterPosition = new ArrayList<>();
                String selectedFilter = sp.getString("selectedFilter", "");

                if (!selectedFilter.isEmpty()){
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(selectedFilter);

                    while (matcher.find()) {
                        int number = Integer.parseInt(matcher.group());
                        selectedFilterPosition.add(number);
                    }
                }

                for (int i = 0; i < selectedFilterPosition.size(); i++){
                    if (position == selectedFilterPosition.get(i)){
                        selectedFilterPosition.remove(i);
                    }
                }

                selectedFilterPosition.add(position);
                editor.putString("selectedFilter", selectedFilterPosition.toString());
                editor.apply();
            } else {
                filters.setBackgroundColor(Color.parseColor("#ffffff"));
                String selectedFilter = sp.getString("selectedFilter", "");

                ArrayList<Integer> selectedFilterPosition = new ArrayList<>();
                if (!selectedFilter.isEmpty()){
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(selectedFilter);

                    while (matcher.find()) {
                        int number = Integer.parseInt(matcher.group());
                        selectedFilterPosition.add(number);
                    }
                }

                for (int i = 0; i < selectedFilterPosition.size(); i++){
                    int filterPos = selectedFilterPosition.get(i);
                    if (filterPos == position){
                        selectedFilterPosition.remove(i);
                        editor.putString("selectedFilter", selectedFilterPosition.toString());
                        editor.apply();
                    }
                }
            }

        });
        return convertView;
    }
}
