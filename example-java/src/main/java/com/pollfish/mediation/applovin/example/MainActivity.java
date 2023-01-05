package com.pollfish.mediation.applovin.example;

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

        showRewardedAdButton = findViewById(R.id.rewardedAdButton);
        showRewardedAdButton.setOnClickListener(v -> {
            if (rewardedAd.isReady()) {
                rewardedAd.showAd();
            }
        });

        AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
        AppLovinSdk.getInstance(this).initializeSdk(config -> {
            rewardedAd = MaxRewardedAd.getInstance(getString(R.string.ad_unit_id), this);

            // Optional parameters, if have already been set in the AppLovin dashboard
            // In case you've already set them in the Dashboard, params in code will override the ones you've already set
            rewardedAd.setLocalExtraParameter("api_key", "YOUR_API_KEY");
            rewardedAd.setLocalExtraParameter("request_uuid", "REQUEST_UUID");
            rewardedAd.setLocalExtraParameter("release_mode", false);
            rewardedAd.setLocalExtraParameter("user_id", "USER_ID");

            rewardedAd.setListener(this);

            loadRewardedAd();
        });
    }

    private void loadRewardedAd() {
        showRewardedAdButton.setVisibility(View.GONE);
        rewardedAd.loadAd();
    }

    public void onAdLoaded(MaxAd ad) {
        showRewardedAdButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {}

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {}

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        Log.d(TAG, "Reward received: " + reward.getAmount() + " " + reward.getLabel());
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {}

    @Override
    public void onAdHidden(MaxAd ad) {
        loadRewardedAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {}

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Toast.makeText(this, "Ad load failed. AdUnitId: " + adUnitId + ", Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        Toast.makeText(this, "Ad show failed. " + ad.toString() + ", Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        loadRewardedAd();
    }

}