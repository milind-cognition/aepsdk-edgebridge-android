/*
  Copyright 2022 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.edge.bridge

internal object EdgeBridgeConstants {

    const val LOG_TAG = "EdgeBridge"
    const val EXTENSION_NAME = "com.adobe.edge.bridge"
    const val FRIENDLY_NAME = "Edge Bridge"
    const val EXTENSION_VERSION = "3.0.1"

    object MobileCoreKeys {
        const val ACTION = "action"
        const val CONTEXT_DATA = "contextdata"
        const val STATE = "state"
    }

    object AnalyticsKeys {
        const val ADOBE = "__adobe"
        const val ANALYTICS = "analytics"
        const val APPLICATION_IDENTIFIER = "a.AppID"
        const val CONTEXT_DATA = "contextData"
        const val CUSTOMER_PERSPECTIVE = "cp"
        const val LINK_NAME = "linkName"
        const val LINK_TYPE = "linkType"
        const val PAGE_NAME = "pageName"
    }

    object AnalyticsValues {
        const val APP_STATE_BACKGROUND = "background"
        const val APP_STATE_FOREGROUND = "foreground"
        const val OTHER = "other"
        const val PREFIX = "&&"
    }

    object EventNames {
        const val EDGE_BRIDGE_REQUEST = "Edge Bridge Request"
    }

    object JsonValues {
        const val EVENT_TYPE = "analytics.track"
    }
}
