package com.zhl.secondaryscreen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhl.secondaryscreen.R;
import com.zhl.secondaryscreen.activity.SecondFloorActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 * Created by zhaohl on 2019-1-21.
 */
public class MainFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> datas = new ArrayList<>();
    private SmartRefreshLayout refreshView ;
    private CommonAdapter<String> mAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_custom_refresh_header,null);
        refreshView =  view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        for (int i = 0; i < 30; i++) {
            datas.add("custom_refresh_header" + i);
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
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(getContext(), SecondFloorActivity.class);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return view;
    }
}
