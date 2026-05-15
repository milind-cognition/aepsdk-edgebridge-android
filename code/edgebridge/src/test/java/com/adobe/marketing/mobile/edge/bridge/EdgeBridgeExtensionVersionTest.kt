/*
  Copyright 2023 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.edge.bridge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

class EdgeBridgeExtensionVersionTest {

    companion object {
        private const val GRADLE_PROPERTIES_PATH = "../gradle.properties"
        private const val PROPERTY_MODULE_VERSION = "moduleVersion"
    }

    @Test
    fun extensionVersion_verifyModuleVersionInPropertiesFile_asEqual() {
        val properties = loadProperties(GRADLE_PROPERTIES_PATH)

        assertNotNull(EdgeBridge.extensionVersion())
        assertFalse(EdgeBridge.extensionVersion().isEmpty())

        val moduleVersion = properties.getProperty(PROPERTY_MODULE_VERSION)
        assertNotNull(moduleVersion)
        assertFalse(moduleVersion.isEmpty())

        assertEquals(
            "Expected version to match in gradle.properties ($moduleVersion) and extensionVersion API (${EdgeBridge.extensionVersion()})",
            moduleVersion,
            EdgeBridge.extensionVersion()
        )
    }

    private fun loadProperties(filepath: String): Properties {
        val properties = Properties()
        try {
            FileInputStream(filepath).use { input ->
                properties.load(input)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return properties
    }
}
