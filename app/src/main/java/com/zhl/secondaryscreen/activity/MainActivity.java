package com.zhl.secondaryscreen.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.zhl.secondaryscreen.R;
import com.zhl.secondaryscreen.adapter.MyPaperAdapter;
import com.zhl.secondaryscreen.view.SecondaryScreenView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MagicIndicator magicIndicator;
    private ViewPager mViewPager;
    private ArrayList<String> mTitleDataList = new ArrayList<>();
    SecondaryScreenView secondaryScreenView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitleDataList.add("主页");
        mTitleDataList.add("发现");
        mTitleDataList.add("推荐");
        mTitleDataList.add("日报");
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        secondaryScreenView = findViewById(R.id.secondaryScreenView);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPaperAdapter(getSupportFragmentManager(),mTitleDataList));
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.GRAY);
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(mTitleDataList.get(index));
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
        ImageView btnSearch = findViewById(R.id.top_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondaryScreenView.translateToSecondaryView();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!secondaryScreenView.isStillMainView()){
            secondaryScreenView.translateToMainView();
        }else{
            super.onBackPressed();
        }
    }
}
