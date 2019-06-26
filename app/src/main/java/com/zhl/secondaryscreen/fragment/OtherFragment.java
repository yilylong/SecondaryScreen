package com.zhl.secondaryscreen.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 描述：
 * Created by zhaohl on 2019-1-21.
 */
public class OtherFragment extends Fragment {
    private String title;
    private int imgResID;
    public static OtherFragment newInstance(String title, int ImgResid){
        OtherFragment fg = new OtherFragment();
        fg.title = title;
        fg.imgResID = ImgResid;
        return fg;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        TextView textView = new TextView(getContext());
//        textView.setText(title);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        textView.setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(imgResID);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }
}
