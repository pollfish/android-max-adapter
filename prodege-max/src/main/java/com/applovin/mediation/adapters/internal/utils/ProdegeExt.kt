package com.applovin.mediation.adapters.internal.utils

import android.os.Bundle
import com.applovin.mediation.adapter.MaxAdapterError
import com.prodege.listener.ProdegeException
import com.prodege.listener.ProdegeRewardedAdInfo
import com.prodege.listener.ProdegeRewardedInfo
import com.prodege.listener.ProdegeRewardedSurveyInfo

internal fun ProdegeRewardedInfo.asBundle(): Bundle {
    val bundle = Bundle()
    bundle.putString(ProdegeMaxAdapterConstants.CURRENCY_EXTRA_KEY, currency)
    bundle.putInt(ProdegeMaxAdapterConstants.POINTS_EXTRA_KEY, points)
    bundle.putString(ProdegeMaxAdapterConstants.PLACEMENT_EXTRA_KEY, placement)

    if (this is ProdegeRewardedSurveyInfo) {
        bundle.putString(ProdegeMaxAdapterConstants.SURVEY_CLASS_KEY, surveyClass)
        bundle.putInt(ProdegeMaxAdapterConstants.CPA_EXTRA_KEY, cpa)

        incidenceRate?.let { bundle.putInt(ProdegeMaxAdapterConstants.INCIDENCE_RATE_EXTRA_KEY, it) }
        lengthOfInterview?.let { bundle.putInt(ProdegeMaxAdapterConstants.LOI_EXTRA_KEY, it) }
        remainingCompletes?.let {
            bundle.putInt(
                ProdegeMaxAdapterConstants.REMAINING_COMPLETES_EXTRA_KEY,
                it
            )
        }
    } else if (this is ProdegeRewardedAdInfo) {
        bundle.putString(ProdegeMaxAdapterConstants.AD_TYPE_EXTRA_KEY, type.name)
        bundle.putBoolean(ProdegeMaxAdapterConstants.AD_SKIPPABLE_EXTRA_KEY, skippable)
    }

    return bundle
}

internal fun ProdegeException.asMaxAdapterError(): MaxAdapterError {
    return when (this) {
        ProdegeException.NoFill -> MaxAdapterError.NO_FILL
        ProdegeException.ConnectionError -> MaxAdapterError.NO_CONNECTION
        ProdegeException.WrongPlacementId -> MaxAdapterError.INVALID_CONFIGURATION
        ProdegeException.EmptyPlacementId -> MaxAdapterError.INVALID_CONFIGURATION
        ProdegeException.WrongApiKey -> MaxAdapterError.INVALID_CONFIGURATION
        ProdegeException.EmptyApiKey -> MaxAdapterError.INVALID_CONFIGURATION
        ProdegeException.NotInitialized -> MaxAdapterError.NOT_INITIALIZED
        is ProdegeException.InternalError -> MaxAdapterError.INTERNAL_ERROR
        else -> MaxAdapterError.UNSPECIFIED
    }
}