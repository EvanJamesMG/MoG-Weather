package com.example.evanjames.mogweather.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evanjames.mogweather.R;

/**
 * Created by EvanJames on 2015/10/5.
 */
public class MenuLeftFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.menu_left_fragment, container, false);
    }
}