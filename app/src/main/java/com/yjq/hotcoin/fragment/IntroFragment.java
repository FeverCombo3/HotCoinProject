package com.yjq.hotcoin.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjq.hotcoin.R;
import com.yjq.hotcoin.view.WrapContentHeightViewPager;

/**
 * 简介
 */
public class IntroFragment extends Fragment {

    private WrapContentHeightViewPager vp;
    private int index;


    public IntroFragment() {
        // Required empty public constructor
    }

    public void setViewPage(WrapContentHeightViewPager vp, int index){
        this.vp=vp;
        this.index=index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        vp.setObjectForPosition(view,index);

        return view;
    }

}
