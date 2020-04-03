package com.zhl.secondaryscreen.view;

/**
 * 描述：
 * Created by zhaohl on 2020-4-1.
 */
public interface OnStateListener {
    public void onStateChanged(String state);
    public void onStateRefreshing();
    public void onStatePullToRefresh();
    public void onStateReleaseToRefresh();
    public void onStateReleaseToSecond();
    public void onStateReleaseToMain();
    public void onStateMain();
    public void onStateSecond();
}
