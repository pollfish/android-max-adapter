package com.applovin.mediation.adapters.internal.config

import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters
import com.applovin.mediation.adapters.ProdegeMediationAdapter
import com.applovin.mediation.adapters.internal.utils.ProdegeMaxAdapterConstants
import com.prodege.builder.SurveyFormat

internal data class ProdegeMaxAdapterResponseParameters(
    val placementId: String,
    val requestUuid: String?,
    val surveyFormat: SurveyFormat?
) {
    override fun toString(): String =
        "ProdegeMaxAdapterResponseParameters(placementId=$placementId, requestUuid=$requestUuid, surveyFormat=$surveyFormat)"

    companion object {
        fun fromParams(parameters: MaxAdapterResponseParameters): ProdegeMaxAdapterResponseParameters? {
            val remoteParams =
                parameters.serverParameters.getBundle(ProdegeMaxAdapterConstants.REMOTE_PARAMS_KEY)

            return parameters.thirdPartyAdPlacementId?.let {
                it.ifBlank { null }
            }?.let { placementId ->
                val requestUUID =
                    ((parameters.localExtraParameters[ProdegeMediationAdapter.LOCAL_EXTRA_REQUEST_UUID] as? String)
                        ?.let { it.ifBlank { null } }
                        ?: remoteParams?.getString(ProdegeMaxAdapterConstants.REMOTE_REQUEST_UUID_KEY)
                            )?.let { it.ifBlank { null } }

                val surveyFormat =
                    ((parameters.localExtraParameters[ProdegeMediationAdapter.LOCAL_EXTRA_SURVEY_FORMAT] as? Int)?.let {
                        SurveyFormat.values().getOrNull(it)
                    }
                        ?: remoteParams?.getInt(ProdegeMaxAdapterConstants.REMOTE_FORMAT_KEY)?.let {
                            SurveyFormat.values().getOrNull(it)
                        })

                ProdegeMaxAdapterResponseParameters(placementId, requestUUID, surveyFormat)
            }
        }
    }
}