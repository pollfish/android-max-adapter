package com.prodege.mediation.applovin.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.adapters.ProdegeMediationAdapter
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.prodege.mediation.applovin.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MaxRewardedAdListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rewardedAd: MaxRewardedAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initializeButtonListeners()

        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX

        // Optional parameters region
        // Setting any of the following in code will override the ones you've already set in dashboard.
        AppLovinSdk.getInstance(this).userIdentifier = "USER_ID"
        AppLovinSdk.getInstance(this).settings.isMuted = true
        AppLovinSdk.getInstance(this).settings.setExtraParameter(
            ProdegeMediationAdapter.LOCAL_EXTRA_TEST_MODE,
            "true"
        )
        // endregion

        AppLovinSdk.getInstance(this).initializeSdk {
            rewardedAd = MaxRewardedAd.getInstance(getString(R.string.ad_unit_id), this)

            // Optional parameters region
            // Setting any of the following in code will override the ones you've already set in dashboard.
            rewardedAd.setLocalExtraParameter(
                ProdegeMediationAdapter.LOCAL_EXTRA_REQUEST_UUID,
                "REQUEST_UUID"
            )
            rewardedAd.setLocalExtraParameter(ProdegeMediationAdapter.LOCAL_EXTRA_MUTED, false)
            rewardedAd.setLocalExtraParameter(
                ProdegeMediationAdapter.LOCAL_EXTRA_PLACEMENT_ID,
                "PLACEMENT_ID"
            )
            // endregion

            rewardedAd.setListener(this)

            loadRewardedAd()
        }
    }

    private fun initializeButtonListeners() {
        binding.rewardedAdButton.setOnClickListener {
            if (rewardedAd.isReady) {
                rewardedAd.showAd()
            }
        }
        binding.showDebuggerButton.setOnClickListener {
            AppLovinSdk.getInstance(this).showMediationDebugger()
        }
    }

    private fun loadRewardedAd() {
        binding.rewardedAdButton.visibility = View.GONE
        rewardedAd.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd?) {
        Log.d(TAG, "onAdLoaded")
        binding.rewardedAdButton.visibility = View.VISIBLE
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        Log.d(TAG, "onAdDisplayed")
    }

    override fun onAdHidden(ad: MaxAd?) {
        Log.d(TAG, "onAdHidden")
        loadRewardedAd()
    }

    override fun onAdClicked(ad: MaxAd?) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        val message = "onAdLoadFailed: $adUnitId with error ${error?.message}"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, message)
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        val message = "onAdDisplayFailed: ${ad?.adUnitId} with error ${error?.message}"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, message)
        loadRewardedAd()
    }

    @Deprecated("Deprecated in Java")
    override fun onRewardedVideoStarted(ad: MaxAd?) {
        Log.d(TAG, "onRewardedVideoStarted")
    }

    @Deprecated("Deprecated in Java")
    override fun onRewardedVideoCompleted(ad: MaxAd?) {
        Log.d(TAG, "onRewardedVideoCompleted")
    }

    override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
        reward?.let {
            val message = "onUserRewarded: ${reward.amount} ${reward.label}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, message)
        }
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}