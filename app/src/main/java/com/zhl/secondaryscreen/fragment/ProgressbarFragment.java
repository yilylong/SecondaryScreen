package com.zhl.secondaryscreen.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhl.secondaryscreen.R;
import com.zhl.secondaryscreen.view.ImageProgressBar;

/**
 * 描述：
 * Created by zhaohl on 2019-6-26.
 */
public class ProgressbarFragment extends Fragment {
    private static final int UPDATE_PROGRESS=0;
    boolean stop;
    ImageProgressBar progressBar,progressBar2;
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    progressBar.setProgress(msg.arg1);
                    progressBar2.setProgress(msg.arg1);
                    break;
                default:
                    break;
            }
        };
    };

    public static ProgressbarFragment newInstance(){
        ProgressbarFragment fg = new ProgressbarFragment();
        return fg;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_custom_progress_bar,null);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar2 = view.findViewById(R.id.progressBar2);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while(!stop){
                    if(progress>=100){
                        break;
                    }
                    Message msg = handler.obtainMessage();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress+=1;
                    msg.what= UPDATE_PROGRESS;
                    msg.arg1 = progress;
                    msg.sendToTarget();
                }
                progress = 0;
            }
        }).start();
    }
}
