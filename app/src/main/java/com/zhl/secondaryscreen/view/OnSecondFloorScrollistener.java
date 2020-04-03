package com.zhl.secondaryscreen.view;

/**
 * 描述：
 * Created by zhaohl on 2020-3-31.
 */
public interface OnSecondFloorScrollistener {
    /**
     * 滚动回调
     * @param scrollY
     */
    public void onScroll(int scrollY);

    /**
     * 切换到第二页
     */
    public void onSwipeToSecondFloor();

    /**
     * 切换到主页
     */
    public void onSwipeToMain();

    /**
     * 刷新
     */
    public void onRefresh();

}
