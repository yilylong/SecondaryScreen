package com.zhl.secondaryscreen.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhl.secondaryscreen.R;
import com.zhl.secondaryscreen.view.OnSecondFloorScrollistener;
import com.zhl.secondaryscreen.view.OnStateListener;
import com.zhl.secondaryscreen.view.SecondFloorView;

/**
 * 描述：
 * Created by zhaohl on 2020-4-3.
 */
public class ScrollViewRefreshFragment extends Fragment {
    SecondFloorView secondFloorView;
    TextView refreshView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_second_floor,null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        secondFloorView = view.findViewById(R.id.second_floor_view);
        refreshView = view.findViewById(R.id.tv_pull_header);
        secondFloorView.setOnSecondFloorScrollistener(new OnSecondFloorScrollistener() {
            @Override
            public void onScroll(int scrollY) {
//                Log.d("mytag","scrolly==="+scrollY);
            }

            @Override
            public void onSwipeToSecondFloor() {

            }

            @Override
            public void onSwipeToMain() {

            }

            @Override
            public void onRefresh() {
                Log.d("mytag","开始刷新");
                secondFloorView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        secondFloorView.finishRefresh();
                    }
                },2000);
            }
        });
        secondFloorView.setOnStateListener(new OnStateListener() {
            @Override
            public void onStateChanged(String state) {
//                Log.d("mytag","onStateChanged=="+state);
            }

            @Override
            public void onStateRefreshing() {
                Log.d("mytag","onStateRefreshing");
                refreshView.setText("正在刷新...");
            }

            @Override
            public void onStatePullToRefresh() {
                Log.d("mytag","onStatePullToRefresh");
                refreshView.setText("下拉赢好礼 ↓");
            }

            @Override
            public void onStateReleaseToRefresh() {
                Log.d("mytag","onStateReleaseToRefresh");
                refreshView.setText("松开开始刷新 ↑");
            }

            @Override
            public void onStateReleaseToSecond() {
                Log.d("mytag","onStateReleaseToSecond");
                refreshView.setText("松开进入二楼");
            }

            @Override
            public void onStateReleaseToMain() {
                Log.d("mytag","onStateReleaseToMain");
                refreshView.setText("松开回到主页");
            }

            @Override
            public void onStateMain() {
                Log.d("mytag","onStateMain");
                refreshView.setText("下拉赢好礼 ↓");
                refreshView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStateSecond() {
                Log.d("mytag","onStateSecond");
                refreshView.setVisibility(View.GONE);
            }
        });
    }
}
