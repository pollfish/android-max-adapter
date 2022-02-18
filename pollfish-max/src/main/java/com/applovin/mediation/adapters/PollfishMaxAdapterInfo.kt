package com.applovin.mediation.adapters

import android.os.Bundle
import android.util.Log
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters
import com.applovin.mediation.adapters.pollfish.BuildConfig.DEBUG

data class PollfishMaxAdapterInfo(
    val apiKey: String,
    val releaseMode: Boolean?,
    val offerwallMode: Boolean?,
    val requestUUID: String?
) {

    override fun toString(): String =
        "{api_key: $apiKey, release_mode: $releaseMode, offerwall_mode: $offerwallMode, request_mode: $requestUUID}"

    companion object {
        fun fromParams(parameters: MaxAdapterResponseParameters): PollfishMaxAdapterInfo? {
            val remoteParams = parameters.serverParameters["custom_parameters"] as? Bundle

            val apiKey =
                (parameters.localExtraParameters[PollfishConstants.POLLFISH_API_KEY_EXTRA_PARAM_KEY] as? String)
                    ?: (remoteParams?.get(PollfishConstants.POLLFISH_API_KEY_EXTRA_PARAM_KEY) as? String)

            val releaseMode =
                ((parameters.localExtraParameters[PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_PARAM_KEY] as? Boolean)
                    ?: remoteParams?.get(PollfishConstants.POLLFISH_RELEASE_MODE_EXTRA_PARAM_KEY) as? Boolean)

            val requestUUID =
                parameters.localExtraParameters[PollfishConstants.POLLFISH_REQUEST_UUID_EXTRA_PARAM_KEY] as? String
                    ?: remoteParams?.get(PollfishConstants.POLLFISH_REQUEST_UUID_EXTRA_PARAM_KEY) as? String

            val offerwallMode =
                (parameters.localExtraParameters[PollfishConstants.POLLFISH_OFFERWALL_MODE_EXTRA_PARAM_KEY] as? Boolean)
                    ?: remoteParams?.get(PollfishConstants.POLLFISH_OFFERWALL_MODE_EXTRA_PARAM_KEY) as? Boolean

            return apiKey?.let {
                PollfishMaxAdapterInfo(
                    apiKey, releaseMode, offerwallMode, requestUUID
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
