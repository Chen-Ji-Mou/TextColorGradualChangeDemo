package com.chenjimou.textcolorgradualchangedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private final List<TestColorChangeView> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);

        list.add(findViewById(R.id.first));
        list.add(findViewById(R.id.second));
        list.add(findViewById(R.id.third));
        list.add(findViewById(R.id.fourth));

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return new Fragment(R.layout.fragment);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset > 0){
                    TestColorChangeView current = list.get(position);
                    TestColorChangeView next = list.get(position + 1);

                    current.setPoint(TestColorChangeView.POINT_OUT);
                    next.setPoint(TestColorChangeView.POINT_ON);

                    current.setProgress(1 - positionOffset);
                    next.setProgress(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}