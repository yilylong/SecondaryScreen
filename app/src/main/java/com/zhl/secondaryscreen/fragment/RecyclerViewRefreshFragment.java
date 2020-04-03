package com.zhl.secondaryscreen.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhl.secondaryscreen.R;
import com.zhl.secondaryscreen.view.OnSecondFloorScrollistener;
import com.zhl.secondaryscreen.view.OnStateListener;
import com.zhl.secondaryscreen.view.SecondFloorView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 * Created by zhaohl on 2020-4-3.
 */
public class RecyclerViewRefreshFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> datas = new ArrayList<>();
    private SmartRefreshLayout refreshView ;
    private CommonAdapter<String> mAdapter;
    SecondFloorView secondFloorView;
    TextView tvHeader;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_custom_refresh_header,null);
        refreshView =  view.findViewById(R.id.refresh_layout);
        refreshView.setEnableRefresh(false);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        for (int i = 0; i < 30; i++) {
            datas.add("recyclerview in secondfloorview" + i);
        }
        mRecyclerView.setAdapter(mAdapter = new CommonAdapter<String>(getContext(), R.layout.item_cardview, datas) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.item_tx, s);
            }
        });
        refreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.finishRefresh();
                    }
                },2000);
            }
        });
        secondFloorView = view.findViewById(R.id.second_floor_view);
        tvHeader = view.findViewById(R.id.tv_pull_header);
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
                tvHeader.setText("正在刷新...");
            }

            @Override
            public void onStatePullToRefresh() {
                Log.d("mytag","onStatePullToRefresh");
                tvHeader.setText("下拉赢好礼 ↓");
            }

            @Override
            public void onStateReleaseToRefresh() {
                Log.d("mytag","onStateReleaseToRefresh");
                tvHeader.setText("松开开始刷新 ↑");
            }

            @Override
            public void onStateReleaseToSecond() {
                Log.d("mytag","onStateReleaseToSecond");
                tvHeader.setText("松开进入二楼");
            }

            @Override
            public void onStateReleaseToMain() {
                Log.d("mytag","onStateReleaseToMain");
                tvHeader.setText("松开回到主页");
            }

            @Override
            public void onStateMain() {
                Log.d("mytag","onStateMain");
                tvHeader.setText("下拉赢好礼 ↓");
                tvHeader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStateSecond() {
                Log.d("mytag","onStateSecond");
                tvHeader.setVisibility(View.GONE);
            }
        });
        return view;
    }
}
