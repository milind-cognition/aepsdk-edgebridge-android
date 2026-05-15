/*
  Copyright 2024 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.edge.bridge

import com.adobe.marketing.mobile.edge.bridge.EdgeBridgeConstants.LOG_TAG
import com.adobe.marketing.mobile.services.AppState
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.util.StringUtils

internal object EdgeBridgeProperties {

    private const val LOG_SOURCE = "EdgeBridgeProperties"

    /**
     * Provides the current customer perspective based on the application's state.
     *
     * @return A [String] representing the customer perspective, either
     * [EdgeBridgeConstants.AnalyticsValues.APP_STATE_FOREGROUND] for foreground or
     * [EdgeBridgeConstants.AnalyticsValues.APP_STATE_BACKGROUND] for background,
     * defaulting to foreground if the state cannot be determined.
     */
    @JvmStatic
    fun getCustomerPerspective(): String {
        val appContextService = ServiceProvider.getInstance().appContextService
        if (appContextService == null) {
            Log.trace(
                LOG_TAG,
                LOG_SOURCE,
                "getCustomerPerspective - Unable to access platform services to retrieve foreground/background state. Defaulting customer perspective to foreground."
            )
            return EdgeBridgeConstants.AnalyticsValues.APP_STATE_FOREGROUND
        }

        val appState: AppState? = appContextService.appState
        return if (appState == AppState.BACKGROUND) {
            EdgeBridgeConstants.AnalyticsValues.APP_STATE_BACKGROUND
        } else {
            EdgeBridgeConstants.AnalyticsValues.APP_STATE_FOREGROUND
        }
    }

    /**
     * Generates the Application ID string from Application name, version and version code.
     *
     * @return string representation of the Application ID, or null if device info is unavailable.
     */
    @JvmStatic
    fun getApplicationIdentifier(): String? {
        val deviceInfoService = ServiceProvider.getInstance().deviceInfoService ?: return null

        val applicationName = deviceInfoService.applicationName
        val applicationVersion = deviceInfoService.applicationVersion
        val applicationVersionCode = deviceInfoService.applicationVersionCode

        val versionPart = if (!StringUtils.isNullOrEmpty(applicationVersion)) " $applicationVersion" else ""
        val versionCodePart = if (!StringUtils.isNullOrEmpty(applicationVersionCode)) " ($applicationVersionCode)" else ""

        return "$applicationName$versionPart$versionCodePart"
    }
}
