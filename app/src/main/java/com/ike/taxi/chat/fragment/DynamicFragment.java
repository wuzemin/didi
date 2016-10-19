package com.ike.taxi.chat.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ike.taxi.R;

public class DynamicFragment extends Fragment {

    private static Activity act;

    public DynamicFragment() {
    }


    public static DynamicFragment newInstance(Activity activity) {
        DynamicFragment fragment = new DynamicFragment();
        act=activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dynamic, container, false);
    }
}
