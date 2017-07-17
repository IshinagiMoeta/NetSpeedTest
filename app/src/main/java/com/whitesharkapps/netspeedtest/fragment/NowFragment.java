package com.whitesharkapps.netspeedtest.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.info.SwitchEnum;
import com.whitesharkapps.netspeedtest.service.Traffic_Service;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowFragment extends Fragment {
    private ToggleButton floatButton;
    private ToggleButton notifyButton;


    public NowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now, container, false);
        floatButton = view.findViewById(R.id.float_switch_button);
        notifyButton = view.findViewById(R.id.notify_switch_button);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (floatButton.isChecked()) {
                    Intent intent = new Intent();
                    intent.setAction(SwitchEnum.floatSwitch_off);
                    getContext().sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(SwitchEnum.floatSwitch_on);
                    getContext().sendBroadcast(intent);
                }
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notifyButton.isChecked()) {
                    Intent intent = new Intent();
                    intent.setAction(SwitchEnum.notifySwitch_off);
                    getContext().sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(SwitchEnum.notifySwitch_on);
                    getContext().sendBroadcast(intent);
                }
            }
        });
        return view;
    }

}
