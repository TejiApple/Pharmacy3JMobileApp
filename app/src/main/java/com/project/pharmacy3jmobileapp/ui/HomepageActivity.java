package com.project.pharmacy3jmobileapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.ui.adapter.HomepageGridViewAdapter;

public class HomepageActivity extends AppCompatActivity {

    BaseAdapter baseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        GridView gridView = findViewById(R.id.gvProducts);
        baseAdapter = new HomepageGridViewAdapter(this);
        gridView.setAdapter(baseAdapter);
    }
}