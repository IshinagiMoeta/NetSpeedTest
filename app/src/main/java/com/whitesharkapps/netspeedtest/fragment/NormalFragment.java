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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.activity.RootActivity;
import com.whitesharkapps.netspeedtest.model.NetSpeedCount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class NormalFragment extends Fragment{

    private TextView[] views = new TextView[4];
    private Button speed_button;
    private EditText selfWeb;
    private TextView selfText;
    private int anInt = 0;
    private AdView adView;
    private LinearLayout adContainer;
    private InterstitialAd interstitialAd;

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
        selfWeb = view.findViewById(R.id.self_web_editText);
        selfText = view.findViewById(R.id.self_speed_textView);
        adContainer = view.findViewById(R.id.banner_normal);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        speed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitialAd();
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

        selfWeb.setHint(R.string.speed_web_hint);
        selfWeb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                selfWeb.setText("http://");
            }
        });

        faceBookAd();
    }

    private void faceBookAd(){
        adView = new AdView(getContext(),getResources().getString(R.string.fbId_Banner), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();
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

    public void showInterstitialAd(){
        interstitialAd = new InterstitialAd(getContext(), getContext().getResources().getString(R.string.fbId_Interstitial));
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(getContext(), "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }
            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}
