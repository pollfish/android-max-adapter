package com.applovin.mediation.adapters

import android.util.Log
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters
import com.applovin.mediation.adapters.pollfish.BuildConfig.DEBUG

data class PollfishMaxAdapterInfo(
    val apiKey: String,
    val releaseMode: Boolean?,
    val requestUUID: String?,
    val userId: String?
) {

    override fun toString(): String =
        "{api_key: $apiKey, release_mode: $releaseMode, request_mode: $requestUUID}"

    companion object {
        fun fromParams(parameters: MaxAdapterResponseParameters): PollfishMaxAdapterInfo? {
            val remoteParams =
                parameters.serverParameters.getBundle(PollfishConstants.REMOTE_PARAMS_KEY)
            val placementId: String? = parameters.thirdPartyAdPlacementId?.let {
                it.ifBlank { null }
            }

            val apiKey =
                ((parameters.localExtraParameters[PollfishConstants.POLLFISH_API_KEY_EXTRA_PARAM_KEY] as? String)
                    ?: remoteParams?.getString(PollfishConstants.POLLFISH_API_KEY_EXTRA_PARAM_KEY)
                    ?: placementId)?.let { it.ifBlank { null } }

            val releaseMode =
                ((parameters.localExtraParameters[PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_PARAM_KEY] as? Boolean)
                    ?: remoteParams?.get(PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_PARAM_KEY) as? Boolean)

            val requestUUID =
                (parameters.localExtraParameters[PollfishConstants.POLLFISH_REQUEST_UUID_EXTRA_PARAM_KEY] as? String
                    ?: remoteParams?.getString(PollfishConstants.POLLFISH_REQUEST_UUID_EXTRA_PARAM_KEY))?.let { it.ifBlank { null } }

            val userId =
                (parameters.localExtraParameters[PollfishConstants.POLLFISH_USER_ID_EXTRA_PARAM] as? String)?.let {
                    it.ifBlank { null }
                }

            return apiKey?.let {
                PollfishMaxAdapterInfo(
                    apiKey, releaseMode, requestUUID, userId
                ).apply {
                    if (DEBUG)
                        Log.v(
                            "PollfishAdapterInfo",
                            "Initializing Pollfish with the following params: $this"
                        )
                }
            }
        }
    }
}
