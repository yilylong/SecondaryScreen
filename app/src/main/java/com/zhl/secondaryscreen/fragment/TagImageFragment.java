package com.zhl.secondaryscreen.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhl.secondaryscreen.R;
import com.zhl.secondaryscreen.view.TagImageView;

/**
 * 描述：
 * Created by zhaohl on 2019-6-26.
 */
public class TagImageFragment extends Fragment {
    TagImageView imageView;

    public static TagImageFragment newInstance(){
        return new TagImageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_custom_view_test, null);
        imageView = view.findViewById(R.id.tag_img);
//        imageView.setTagSize(50);
//        imageView.setTagLocation(TagImageView.Location.ON_TOP_lEFT);
//        imageView.showTag(false);
//        imageView.setTagBackgroud(Color.parseColor("#FF3396FF"));
//        imageView.setTagTextColor(Color.parseColor("#FFFF336D"));
        return view;
    }


}
