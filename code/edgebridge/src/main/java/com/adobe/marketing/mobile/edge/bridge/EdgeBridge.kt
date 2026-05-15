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

import com.adobe.marketing.mobile.Extension

/**
 * The Adobe Experience Platform Edge Bridge mobile extension provides functionality to facilitate
 * sending data from a mobile application to the Adobe Edge Network.
 *
 * This extension supports the following use-cases:
 * - Enables forwarding of [com.adobe.marketing.mobile.MobileCore.trackState]
 * and [com.adobe.marketing.mobile.MobileCore.trackAction] calls to the Adobe Edge Network. The
 * configured Data Collection datastream for the mobile application can define a mapping of the
 * track event's `contextdata` to an XDM schema using Data Prep for Data Collection.
 */
object EdgeBridge {

    @JvmField
    val EXTENSION: Class<out Extension> = EdgeBridgeExtension::class.java

    private const val LOG_SOURCE = "EdgeBridge"

    /**
     * Returns the version of the Edge Bridge extension.
     * @return the version of the Edge Bridge extension
     */
    @JvmStatic
    fun extensionVersion(): String = EdgeBridgeConstants.EXTENSION_VERSION
}
