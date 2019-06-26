package com.zhl.secondaryscreen.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.zhl.secondaryscreen.R;

/**
 * 描述：
 * Created by zhaohl on 2019-1-16.
 */
public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {
    private LottieAnimationView lottieAnimationView;
    private ImageView arrow;
    private TextView tipView;

    public CustomRefreshHeader(Context context) {
        this(context, null);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_refresh_header, null);
        lottieAnimationView = view.findViewById(R.id.lottie_anim);
        arrow = view.findViewById(R.id.arrow);
        tipView = view.findViewById(R.id.tip_view);
        addView(view);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        Log.d("mytag","----onPulling...");
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        Log.d("mytag","----onReleasing...");
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
        Log.d("mytag","----onReleased...");
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        lottieAnimationView.playAnimation();
        Log.d("mytag","----onStartAnimator...");
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        lottieAnimationView.pauseAnimation();
        if (success) {
            tipView.setText("刷新成功");
        } else {
            tipView.setText("刷新失败");
        }
        return 400;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                tipView.setText("下拉开始刷新");
                arrow.setVisibility(VISIBLE);
                arrow.animate().rotation(0);
                break;
            case ReleaseToRefresh:
                tipView.setText("释放刷新");
                arrow.animate().rotation(180);
                break;
            case Refreshing:
                tipView.setText("正在刷新...");
                arrow.setVisibility(GONE);
                Log.d("mytag","----正在刷新...");
                break;

        }
    }
}
