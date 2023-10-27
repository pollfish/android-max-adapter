package com.prodege.mediation.applovin.example;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.adapters.ProdegeMediationAdapter;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;

public class MainActivity extends AppCompatActivity implements MaxRewardedAdListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MaxRewardedAd rewardedAd;
    private Button showRewardedAdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtonListeners();

        AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);

        // Optional parameters region
        // Setting any of the following in code will override the ones you've already set in dashboard.
        AppLovinSdk.getInstance(this).setUserIdentifier("USER_ID");
        AppLovinSdk.getInstance(this).getSettings().setMuted(true);
        AppLovinSdk.getInstance(this).getSettings().setExtraParameter(ProdegeMediationAdapter.LOCAL_EXTRA_TEST_MODE, "true");
        // endregion

        AppLovinSdk.getInstance(this).initializeSdk(config -> {
            rewardedAd = MaxRewardedAd.getInstance(getString(R.string.ad_unit_id), this);

            // Optional parameters region
            // Setting any of the following in code will override the ones you've already set in dashboard.
            rewardedAd.setLocalExtraParameter(ProdegeMediationAdapter.LOCAL_EXTRA_REQUEST_UUID, "REQUEST_UUID");
            rewardedAd.setLocalExtraParameter(ProdegeMediationAdapter.LOCAL_EXTRA_MUTED, false);
            rewardedAd.setLocalExtraParameter(ProdegeMediationAdapter.LOCAL_EXTRA_PLACEMENT_ID, "PLACEMENT_ID");
            // endregion

            rewardedAd.setListener(this);

            loadRewardedAd();
        });
    }

    private void initializeButtonListeners() {
        showRewardedAdButton = findViewById(R.id.rewardedAdButton);
        showRewardedAdButton.setOnClickListener(v -> {
            if (rewardedAd.isReady()) {
                rewardedAd.showAd();
            }
        });
    }

    private void loadRewardedAd() {
        showRewardedAdButton.setVisibility(View.GONE);
        rewardedAd.loadAd();
    }

    public void onAdLoaded(MaxAd ad) {
        Log.d(TAG, "onAdLoaded");
        showRewardedAdButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {
        Log.d(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {
        Log.d(TAG, "onRewardedVideoCompleted");
    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        @SuppressLint("DefaultLocale")
        String message = String.format("onUserRewarded: %d %s", reward.getAmount(), reward.getLabel());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        Log.d(TAG, "onAdDisplayed");
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        Log.d(TAG, "onAdHidden");
        loadRewardedAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        Log.d(TAG, "onAdClicked");
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        String message = String.format("onAdLoadFailed: %s with error %s", adUnitId, error.getMessage());
        Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        String message = String.format("onAdDisplayFailed: %s with error %s", ad.getAdUnitId(), error.getMessage());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
        loadRewardedAd();
    }

}