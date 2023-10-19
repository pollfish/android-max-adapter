package com.applovin.mediation.adapters

import android.app.Activity
import android.util.Log
import com.applovin.impl.mediation.MaxRewardImpl
import com.applovin.mediation.MaxReward
import com.applovin.mediation.adapter.MaxAdapter
import com.applovin.mediation.adapter.MaxAdapterError
import com.applovin.mediation.adapter.MaxRewardedAdapter
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters
import com.applovin.mediation.adapters.internal.config.ProdegeMaxAdapterInitializationParameters
import com.applovin.mediation.adapters.internal.config.ProdegeMaxAdapterResponseParameters
import com.applovin.mediation.adapters.internal.config.ProdegeMaxAdapterShowParameters
import com.applovin.mediation.adapters.internal.utils.asBundle
import com.applovin.mediation.adapters.internal.utils.asMaxAdapterError
import com.applovin.mediation.adapters.prodege.BuildConfig
import com.applovin.sdk.AppLovinSdk
import com.prodege.Prodege
import com.prodege.builder.AdOptions
import com.prodege.builder.InitOptions
import com.prodege.builder.Platform
import com.prodege.listener.ProdegeException
import com.prodege.listener.ProdegeInitListener
import com.prodege.listener.ProdegeRewardedInfo
import com.prodege.listener.ProdegeRewardedLoadListener
import com.prodege.builder.AdRequest
import com.prodege.listener.ProdegeEventListener
import com.prodege.listener.ProdegeReward
import com.prodege.listener.ProdegeRewardListener
import com.prodege.listener.ProdegeShowListener

public class ProdegeMediationAdapter(sdk: AppLovinSdk) : MediationAdapterBase(sdk),
    MaxRewardedAdapter,
    ProdegeEventListener,
    ProdegeRewardListener {

    private var adapterListener: MaxRewardedAdapterListener? = null

    override fun initialize(
        parameters: MaxAdapterInitializationParameters?,
        activity: Activity?,
        onCompletionListener: MaxAdapter.OnCompletionListener?
    ) {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d(TAG, ProdegeException.MinimumSdkVersion.message)
            onCompletionListener?.onCompletion(
                MaxAdapter.InitializationStatus.NOT_INITIALIZED,
                ProdegeException.MinimumSdkVersion.message
            )
            return
        }

        if (Prodege.isInitialized()) {
            Log.d(TAG, "Prodege SDK already initialized")
            onCompletionListener?.onCompletion(
                MaxAdapter.InitializationStatus.INITIALIZED_SUCCESS,
                null
            )
            return
        }

        if (activity == null) {
            onCompletionListener?.onCompletion(
                MaxAdapter.InitializationStatus.INITIALIZED_FAILURE,
                "Null activity"
            )
            return
        }

        if (parameters == null) {
            onCompletionListener?.onCompletion(
                MaxAdapter.InitializationStatus.INITIALIZED_FAILURE,
                "Null parameters"
            )
            return
        }

        val adapterConfiguration = ProdegeMaxAdapterInitializationParameters.fromParams(
            wrappingSdk.userIdentifier,
            wrappingSdk.settings,
            parameters
        )

        if (adapterConfiguration == null) {
            onCompletionListener?.onCompletion(
                MaxAdapter.InitializationStatus.INITIALIZED_FAILURE,
                "Error while parsing the adapter configuration."
            )
            return
        }

        Log.d(
            TAG,
            "Initializing Prodege SDK with $adapterConfiguration."
        )

        val initOptions = InitOptions.Builder()
            .platform(Platform.MAX)
            .testMode(adapterConfiguration.testMode)

        adapterConfiguration.userId?.let {
            initOptions.userId(it)
        }

        onCompletionListener?.onCompletion(MaxAdapter.InitializationStatus.INITIALIZING, null)

        Prodege.initialize(activity, adapterConfiguration.apiKey, object : ProdegeInitListener {
            override fun onError(exception: ProdegeException) {
                onCompletionListener?.onCompletion(
                    MaxAdapter.InitializationStatus.INITIALIZED_FAILURE,
                    exception.message
                )
            }

            override fun onSuccess() {
                onCompletionListener?.onCompletion(
                    MaxAdapter.InitializationStatus.INITIALIZED_SUCCESS,
                    null
                )
            }
        }, initOptions.build())
    }

    override fun getSdkVersion(): String {
        return BuildConfig.PRODEGE_SDK_VERSION
    }

    override fun getAdapterVersion(): String {
        return BuildConfig.ADAPTER_VERSION
    }

    override fun onDestroy() {
        Prodege.hideAll()
        this.adapterListener = null
    }

    override fun loadRewardedAd(
        parameters: MaxAdapterResponseParameters?,
        activity: Activity?,
        listener: MaxRewardedAdapterListener?
    ) {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d(TAG, ProdegeException.MinimumSdkVersion.message)
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        if (parameters == null) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        if (parameters.isAgeRestrictedUser == true) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.NO_FILL)
            return
        }

        val adRequestConfiguration = ProdegeMaxAdapterResponseParameters.fromParams(parameters)

        if (adRequestConfiguration == null) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION)
            return
        }

        if (Prodege.isPlacementVisible(adRequestConfiguration.placementId)) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        val adRequest = AdRequest.Builder()

        adRequestConfiguration.requestUuid?.let {
            adRequest.requestUuid(it)
        }

        adRequestConfiguration.surveyFormat?.let {
            adRequest.surveyFormat(it)
        }

        Log.d(TAG, "Loading Prodege Ads with $adRequestConfiguration.")

        Prodege.loadRewardedAd(
            adRequestConfiguration.placementId,
            object : ProdegeRewardedLoadListener {
                override fun onRewardedLoadFailed(placementId: String, exception: ProdegeException) {
                    listener?.onRewardedAdLoadFailed(exception.asMaxAdapterError())
                }

                override fun onRewardedLoaded(placementId: String, info: ProdegeRewardedInfo) {
                    adapterListener = listener
                    Prodege.setRewardListener(this@ProdegeMediationAdapter)
                    Prodege.setEventListener(this@ProdegeMediationAdapter)
                    listener?.onRewardedAdLoaded(info.asBundle())
                }
            }, adRequest.build()
        )
    }

    override fun showRewardedAd(
        parameters: MaxAdapterResponseParameters?,
        activity: Activity?,
        listener: MaxRewardedAdapterListener?
    ) {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d(TAG, ProdegeException.MinimumSdkVersion.message)
            listener?.onRewardedAdDisplayFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        if (parameters == null) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        val showConfiguration =
            ProdegeMaxAdapterShowParameters.fromParams(wrappingSdk.settings, parameters)

        if (showConfiguration == null) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION)
            return
        }

        val adOptions = AdOptions.Builder()
            .muted(showConfiguration.muted)

        Log.d(TAG, "Presenting Prodege Ads with $showConfiguration.")

        Prodege.showPlacement(showConfiguration.placementId, object : ProdegeShowListener {
            override fun onClosed(placementId: String) {
                listener?.onRewardedAdHidden()
            }

            override fun onOpened(placementId: String) {
                listener?.onRewardedAdDisplayed()
            }

            override fun onShowFailed(placementId: String, exception: ProdegeException) {
            }
        }, adOptions.build())
    }

    override fun onComplete(placementId: String) {
        adapterListener?.onRewardedAdVideoCompleted()
    }

    override fun onClick(placementId: String) {
        adapterListener?.onRewardedAdClicked()
    }

    override fun onStart(placementId: String) {
        adapterListener?.onRewardedAdVideoStarted()
    }

    override fun onRewardReceived(reward: ProdegeReward) {
        val maxReward: MaxReward = MaxRewardImpl.create(reward.points, reward.currency)
        adapterListener?.onUserRewarded(maxReward)
    }

    companion object {
        private val TAG: String = ProdegeMediationAdapter::class.java.simpleName
        const val LOCAL_EXTRA_USER_ID = "prodege_mediation_user_id"
        const val LOCAL_EXTRA_TEST_MODE = "prodege_mediation_test_mode"
        const val LOCAL_EXTRA_REQUEST_UUID = "prodege_mediation_request_uuid"
        const val LOCAL_EXTRA_MUTED = "prodege_mediation_muted"
        const val LOCAL_EXTRA_SURVEY_FORMAT = "prodege_mediation_survey_format"
        const val LOCAL_EXTRA_PLACEMENT_ID = "prodege_mediation_placement_id"
    }

}
