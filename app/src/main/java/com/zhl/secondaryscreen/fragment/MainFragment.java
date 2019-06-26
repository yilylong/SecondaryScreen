package com.zhl.secondaryscreen.fragment;

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
import com.zhy.adapter.recyclerview.CommonAdapter;
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
        mRecyclerView.setAdapter(new CommonAdapter(getContext(), R.layout.item_cardview, datas) {
            @Override
            protected void convert(ViewHolder holder, Object o, int position) {
                holder.setText(R.id.item_tx, datas.get(position));
            }
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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
        return view;
    }
}
