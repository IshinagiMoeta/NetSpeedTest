package com.whitesharkapps.netspeedtest.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.activity.RootActivity;
import com.whitesharkapps.netspeedtest.info.SwitchEnum;
import com.whitesharkapps.netspeedtest.service.Traffic_Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowFragment extends Fragment {
    private ToggleButton floatButton;
    private ToggleButton notifyButton;
    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout adView;

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
        nativeAdContainer = view.findViewById(R.id.native_ad_container);

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
        showNativeAd();

        return view;
    }

    private void showNativeAd() {
        nativeAd = new NativeAd(getContext(),getResources().getString(R.string.fbId_Native_250));
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                if (nativeAd.isAdLoaded()) {
                    nativeAd.unregisterView();
                }

                // Add the Ad view into the ad container.

                LayoutInflater inflater = LayoutInflater.from(getContext());
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                adView = (LinearLayout) inflater.inflate(R.layout.facebook_native_ad, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) getActivity().findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(getContext() , nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(nativeAdContainer,clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        // Request an ad
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }
}
