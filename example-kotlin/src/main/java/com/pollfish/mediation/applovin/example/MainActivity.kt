package com.pollfish.mediation.applovin.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.pollfish.mediation.applovin.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MaxRewardedAdListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rewardedAd: MaxRewardedAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rewardedAdButton.setOnClickListener {
            if (rewardedAd.isReady) {
                rewardedAd.showAd()
            }
        }
        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX

        AppLovinSdk.getInstance(this).initializeSdk {
            rewardedAd = MaxRewardedAd.getInstance(getString(R.string.ad_unit_id), this)

            // Optional parameters, if have already been set in the AppLovin dashboard
            // In case you've already set them in the Dashboard, params in code will override the ones you've already set
            rewardedAd.setLocalExtraParameter("api_key", "YOUR_API_KEY")
            rewardedAd.setLocalExtraParameter("request_uuid", "REQUEST_UUID")
            rewardedAd.setLocalExtraParameter("release_mode", false)
            rewardedAd.setLocalExtraParameter("user_id", "USER_ID")

            rewardedAd.setListener(this)

            loadRewardedAd()
        }
    }

    private fun loadRewardedAd() {
        binding.rewardedAdButton.visibility = View.GONE
        rewardedAd.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd?) {
        binding.rewardedAdButton.visibility = View.VISIBLE
    }

    override fun onAdDisplayed(ad: MaxAd?) {}

    override fun onAdHidden(ad: MaxAd?) {
        loadRewardedAd()
    }

    override fun onAdClicked(ad: MaxAd?) {}

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        Toast.makeText(
            this,
            "Ad load failed. AdUnitId: " + adUnitId + ", Error: " + error?.message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        Toast.makeText(
            this,
            "Ad show failed. " + ad.toString() + ", Error: " + error?.message,
            Toast.LENGTH_SHORT
        ).show()
        loadRewardedAd()
    }

    @Deprecated("Deprecated in Java")
    override fun onRewardedVideoStarted(ad: MaxAd?) {}

    @Deprecated("Deprecated in Java")
    override fun onRewardedVideoCompleted(ad: MaxAd?) {}

    override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
        reward?.let {
            Log.d(TAG, "Reward received: ${reward.amount} ${reward.label}")
        }
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}