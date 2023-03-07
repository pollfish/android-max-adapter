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
import com.applovin.sdk.AppLovinSdk
import com.pollfish.Pollfish
import com.pollfish.builder.Params
import com.pollfish.builder.Platform
import com.pollfish.builder.SurveyFormat
import com.pollfish.callback.*

public class PollfishMediationAdapter(sdk: AppLovinSdk) : MediationAdapterBase(sdk),
    MaxRewardedAdapter,
    PollfishClosedListener,
    PollfishOpenedListener,
    PollfishSurveyCompletedListener,
    PollfishSurveyNotAvailableListener,
    PollfishSurveyReceivedListener,
    PollfishUserNotEligibleListener,
    PollfishUserRejectedSurveyListener {

    private var adapterListener: MaxRewardedAdapterListener? = null

    override fun initialize(
        parameters: MaxAdapterInitializationParameters?,
        activity: Activity?,
        onCompletionListener: MaxAdapter.OnCompletionListener?
    ) {
        onCompletionListener?.onCompletion(MaxAdapter.InitializationStatus.DOES_NOT_APPLY, null)
    }

    override fun getSdkVersion(): String {
        return PollfishConstants.POLLFISH_SDK_VERSION
    }

    override fun getAdapterVersion(): String {
        return PollfishConstants.POLLFISH_ADAPTER_VERSION
    }

    override fun onDestroy() {}

    override fun loadRewardedAd(
        parameters: MaxAdapterResponseParameters?,
        activity: Activity?,
        listener: MaxRewardedAdapterListener?
    ) {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.w(TAG, "Pollfish surveys will not run on targets lower than 21")
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        if (Pollfish.isPollfishPanelOpen()) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        if (parameters == null) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        if (parameters.isAgeRestrictedUser) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.NO_FILL)
            return
        }

        val adapterInfo = PollfishMaxAdapterInfo.fromParams(parameters)

        if (adapterInfo == null) {
            listener?.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        activity?.let { _ ->
            val params = Params.Builder(adapterInfo.apiKey)
                .apply {
                    (adapterInfo.requestUUID?.let {
                        this.requestUUID(it)
                    })
                    (adapterInfo.releaseMode?.let {
                        this.releaseMode(it)
                    })
                    (adapterInfo.userId?.let {
                        this.userId(it)
                    })
                    (adapterInfo.surveyFormat?.let { id ->
                        SurveyFormat.values().getOrNull(id)?.let {
                            this.surveyFormat(it)
                        }
                    })
                }
                .rewardMode(true)
                .pollfishClosedListener(this)
                .pollfishOpenedListener(this)
                .pollfishSurveyCompletedListener(this)
                .pollfishSurveyNotAvailableListener(this)
                .pollfishUserNotEligibleListener(this)
                .pollfishUserRejectedSurveyListener(this)
                .pollfishSurveyReceivedListener(this)
                .platform(Platform.MAX)
                .build()

            Pollfish.initWith(activity, params)

            this.adapterListener = listener
        }
    }

    override fun showRewardedAd(
        parameters: MaxAdapterResponseParameters?,
        activity: Activity?,
        listener: MaxRewardedAdapterListener?
    ) {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.w(TAG, "Pollfish surveys will not run on targets lower than 21")
            listener?.onRewardedAdDisplayFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        Pollfish.show()
    }

    companion object {
        val TAG: String = PollfishMediationAdapter::class.java.simpleName
    }

    override fun onPollfishClosed() {
        adapterListener?.onRewardedAdHidden()
    }

    override fun onPollfishOpened() {
        adapterListener?.onRewardedAdDisplayed()
    }

    override fun onPollfishSurveyCompleted(surveyInfo: SurveyInfo) {
        val reward: MaxReward =
            if (surveyInfo.rewardName != null && surveyInfo.rewardValue != null) {
                MaxRewardImpl.create(surveyInfo.rewardValue!!, surveyInfo.rewardName)
            } else {
                MaxRewardImpl.createDefault()
            }

        adapterListener?.onUserRewarded(reward)
    }

    override fun onPollfishSurveyNotAvailable() {
        adapterListener?.onRewardedAdLoadFailed(MaxAdapterError.NO_FILL)
    }

    override fun onPollfishSurveyReceived(surveyInfo: SurveyInfo?) {
        adapterListener?.onRewardedAdLoaded()
    }

    override fun onUserNotEligible() {
        adapterListener?.onRewardedAdLoadFailed(MaxAdapterError.NO_FILL)
    }

    override fun onUserRejectedSurvey() {
        adapterListener?.onRewardedAdHidden()
    }

}