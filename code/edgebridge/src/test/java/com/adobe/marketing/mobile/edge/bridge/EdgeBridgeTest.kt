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

import android.app.Application
import android.content.Context
import com.adobe.marketing.mobile.Extension
import com.adobe.marketing.mobile.MobileCore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class EdgeBridgeTest {

    @Mock
    lateinit var mockApplication: Application

    @Mock
    lateinit var mockContext: Context

    @Before
    fun setup() {
        Mockito.reset(mockApplication)
        Mockito.reset(mockContext)
    }

    // ========================================================================================
    // registerExtension(s)
    // ========================================================================================
    @Test
    @Throws(InterruptedException::class)
    fun test_registerExtension() {
        MobileCore.setApplication(mockApplication)

        val latch = CountDownLatch(1)
        val extensions = listOf<Class<out Extension>>(EdgeBridge.EXTENSION)
        MobileCore.registerExtensions(extensions) { latch.countDown() }
        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS))
    }

    // ========================================================================================
    // publicExtensionConstants
    // ========================================================================================
    @Test
    fun test_publicExtensionConstants() {
        assertEquals(EdgeBridgeExtension::class.java, EdgeBridge.EXTENSION)
        val extensions = listOf<Class<out Extension>>(EdgeBridge.EXTENSION)
        MobileCore.registerExtensions(extensions, null)
    }

    @Test
    fun testExtensionVersion_verifyModuleVersionInPropertiesFile_asEqual() {
        val properties = loadProperties("../gradle.properties")

        assertNotNull(EdgeBridge.extensionVersion())
        assertFalse(EdgeBridge.extensionVersion().isEmpty())

        val moduleVersion = properties.getProperty("moduleVersion")
        assertNotNull(moduleVersion)
        assertFalse(moduleVersion.isEmpty())

        assertEquals(
            String.format(
                "Expected version to match in gradle.properties (%s) and extensionVersion API (%s)",
                moduleVersion,
                EdgeBridge.extensionVersion()
            ),
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
