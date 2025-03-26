package com.example.safety_project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class HowToSwipe extends AppCompatActivity {

    private ViewPager2 viewPager;
    private HowToSwipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_swipe);

        viewPager = findViewById(R.id.viewPager);
        adapter = new HowToSwipeAdapter(this);
        viewPager.setAdapter(adapter);
    }
}
