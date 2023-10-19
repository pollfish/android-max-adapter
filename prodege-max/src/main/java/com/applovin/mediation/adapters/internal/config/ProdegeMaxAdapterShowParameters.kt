package com.applovin.mediation.adapters.internal.config

import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters
import com.applovin.mediation.adapters.ProdegeMediationAdapter
import com.applovin.mediation.adapters.internal.utils.ProdegeMaxAdapterConstants
import com.applovin.sdk.AppLovinSdkSettings

data class ProdegeMaxAdapterShowParameters(
    val placementId: String,
    val muted: Boolean
) {
    override fun toString(): String =
        "ProdegeMaxAdapterShowParameters(muted=$muted)"

    companion object {
        fun fromParams(
            settings: AppLovinSdkSettings,
            parameters: MaxAdapterResponseParameters
        ): ProdegeMaxAdapterShowParameters? {
            val remoteParams =
                parameters.serverParameters.getBundle(ProdegeMaxAdapterConstants.REMOTE_PARAMS_KEY)

            return parameters.thirdPartyAdPlacementId?.let {
                it.ifBlank { null }
            }?.let { placementId ->
                val muted =
                    settings.isMuted || parameters.localExtraParameters[ProdegeMediationAdapter.LOCAL_EXTRA_MUTED] as? Boolean ?: remoteParams?.getBoolean(
                        ProdegeMaxAdapterConstants.REMOTE_MUTED_KEY
                    ) ?: false

                ProdegeMaxAdapterShowParameters(placementId, muted)
            }
        }
    }

}