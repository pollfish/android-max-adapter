package com.applovin.mediation.adapters.internal.config

import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters
import com.applovin.mediation.adapters.ProdegeMediationAdapter
import com.applovin.mediation.adapters.internal.utils.ProdegeMaxAdapterConstants
import com.applovin.sdk.AppLovinSdkSettings

internal data class ProdegeMaxAdapterInitializationParameters(
    val apiKey: String,
    val userId: String?,
    val testMode: Boolean
) {

    override fun toString(): String =
        "ProdegeMaxAdapterInitializationParameters(apiKey=$apiKey, userId=$userId, testMode: $testMode)"

    companion object {
        fun fromParams(
            settingsUserId: String?,
            settings: AppLovinSdkSettings,
            parameters: MaxAdapterInitializationParameters
        ): ProdegeMaxAdapterInitializationParameters? = parameters.serverParameters.getString(
            ProdegeMaxAdapterConstants.APP_ID_PARAM_KEY,
            null
        )?.let { appId ->
            appId.ifBlank { null }
        }?.let { appId ->
            val userId =
                settings.extraParameters[ProdegeMediationAdapter.LOCAL_EXTRA_USER_ID]?.let { it.ifBlank { null } }
                    ?: settingsUserId?.let {
                        it.ifBlank { null }
                    }

            val testMode =
                parameters.isTesting || settings.extraParameters[ProdegeMediationAdapter.LOCAL_EXTRA_TEST_MODE].toBoolean()

            ProdegeMaxAdapterInitializationParameters(appId, userId, testMode)
        }
    }
}
