package com.whitesharkapps.netspeedtest.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.model.NetSpeedCount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class NormalFragment extends Fragment{

    TextView[] views = new TextView[4];
    Button speed_button;
    EditText selfWeb;
    TextView selfText;
    int anInt = 0;

    public NormalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal, container, false);
        // Inflate the layout for this fragment

        views[0] = view.findViewById(R.id.speed_textview_1);
        views[1] = view.findViewById(R.id.speed_textview_2);
        views[2] = view.findViewById(R.id.speed_textview_3);
        views[3] = view.findViewById(R.id.speed_textview_4);
        speed_button = view.findViewById(R.id.speed_button);
        speed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selfWeb.getText().length()!=0) {
                    if (isMobileNO(selfWeb.getText().toString())){
                        NetSpeedCount count = NetSpeedCount.getInstance();
                        count.selfStartSpeed(new selfSpeedHandler(),selfWeb.getText().toString());
                        selfText.setText(R.string.on_speed);
                    }else{
                        Toast.makeText(getContext(),R.string.err_text_hint,Toast.LENGTH_LONG).show();
                        anInt++;
                    }
                }else{
                    Toast.makeText(getContext(),R.string.no_text_hint,Toast.LENGTH_SHORT).show();
                    anInt++;
                }

                NetSpeedCount count = NetSpeedCount.getInstance();
                count.startSpeed(new SpeedHandler());
                for (int i=0;i<views.length;i++){
                    views[i].setText(getString(R.string.on_speed));
                }
                speed_button.setText(getString(R.string.on_speed));
                speed_button.setEnabled(false);
            }
        });
        selfWeb = view.findViewById(R.id.self_web_editText);
        selfText = view.findViewById(R.id.self_speed_textView);

        selfWeb.setHint(R.string.speed_web_hint);
        selfWeb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                selfWeb.setText("http://");
            }
        });
        return view;
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+$");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    public class SpeedHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            views[msg.what].setText(msg.getData().getString("speed"));
            if (msg.what==3){
                anInt++;
                if (anInt==2){
                speed_button.setEnabled(true);
                speed_button.setText(getString(R.string.start_speed));
                anInt = 0;
                }
            }
        }
    }

    public class selfSpeedHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                selfText.setText(msg.getData().getString("speed"));
                anInt++;
                if (anInt == 2) {
                    speed_button.setEnabled(true);
                    speed_button.setText(getString(R.string.start_speed));
                    anInt = 0;
                }
            }
        }
    }



}
