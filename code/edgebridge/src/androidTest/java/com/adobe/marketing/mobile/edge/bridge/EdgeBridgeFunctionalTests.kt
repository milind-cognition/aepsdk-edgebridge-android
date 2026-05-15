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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adobe.marketing.mobile.Edge
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.edge.identity.Identity
import com.adobe.marketing.mobile.services.HttpConnecting
import com.adobe.marketing.mobile.services.HttpMethod.GET
import com.adobe.marketing.mobile.services.HttpMethod.POST
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.util.ElementCount
import com.adobe.marketing.mobile.util.JSONAsserts.assertExactMatch
import com.adobe.marketing.mobile.util.MockNetworkService
import com.adobe.marketing.mobile.util.NodeConfig.Scope.Subtree
import com.adobe.marketing.mobile.util.TestHelper
import com.adobe.marketing.mobile.util.TestHelper.LogOnErrorRule
import com.adobe.marketing.mobile.util.TestHelper.SetupCoreRule
import com.adobe.marketing.mobile.util.TestHelper.getAsset
import com.adobe.marketing.mobile.util.ValueTypeMatch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class EdgeBridgeFunctionalTests {

    companion object {
        private val mockNetworkService = MockNetworkService()

        // Fake Edge Configuration ID, needed for Edge extension initialization
        private const val CONFIG_ID = "1234abcd-abcd-1234-5678-123456abcdef"
        // Edge Network interact endpoint
        private const val EDGE_INTERACT_ENDPOINT = "https://edge.adobedc.net/ee/v1/interact"
    }

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(LogOnErrorRule()).around(SetupCoreRule())

    @Before
    fun setup() {
        ServiceProvider.getInstance().setNetworkService(mockNetworkService)

        val config = mutableMapOf<String, Any>(
            "edge.configId" to CONFIG_ID
        )
        MobileCore.updateConfiguration(config)

        CountDownLatch(1).also { latch ->
            MobileCore.registerExtensions(
                listOf(Edge.EXTENSION, Identity.EXTENSION, EdgeBridge.EXTENSION)
            ) { latch.countDown() }
            latch.await()
        }
        resetTestExpectations()
    }

    @After
    fun tearDown() {
        resetTestExpectations()
    }

    @Test
    fun testTrackState_sendsEdgeExperienceEvent() {
        mockNetworkService.setExpectationForNetworkRequest(EDGE_INTERACT_ENDPOINT, POST, 1)

        MobileCore.trackState(
            "state name",
            mapOf(
                "key1" to "value1",
                "&&c1" to "propValue1"
            )
        )

        mockNetworkService.assertAllNetworkRequestExpectations()

        val networkRequests = mockNetworkService.getNetworkRequestsWith(
            EDGE_INTERACT_ENDPOINT,
            POST,
            1000
        )

        assertEquals(1, networkRequests.size)

        val expected =
            "{" +
                "  \"events\": [" +
                "    {" +
                "      \"xdm\": {" +
                "        \"eventType\": \"analytics.track\"," +
                "        \"timestamp\": \"STRING_TYPE\"," +
                "        \"_id\": \"STRING_TYPE\"" +
                "      }," +
                "      \"data\": {" +
                "        \"__adobe\": {" +
                "          \"analytics\": {" +
                "            \"cp\": \"foreground\"," +
                "            \"pageName\": \"state name\"," +
                "            \"c1\": \"propValue1\"," +
                "            \"contextData\": {" +
                "              \"key1\": \"value1\"," +
                "              \"a.AppID\": \"com.adobe.marketing.mobile.edge.bridge.test\"" +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }" +
                "  ]" +
                "}"

        assertExactMatch(
            expected,
            networkRequests[0].bodyJson,
            ElementCount(17, Subtree),
            ValueTypeMatch("events[0].xdm.timestamp", "events[0].xdm._id")
        )
    }

    @Test
    fun testTrackAction_sendsCorrectRequestEvent() {
        mockNetworkService.setExpectationForNetworkRequest(EDGE_INTERACT_ENDPOINT, POST, 1)

        MobileCore.trackAction(
            "action name",
            mapOf(
                "key1" to "value1",
                "&&c1" to "propValue1"
            )
        )

        mockNetworkService.assertAllNetworkRequestExpectations()

        val networkRequests = mockNetworkService.getNetworkRequestsWith(
            EDGE_INTERACT_ENDPOINT,
            POST
        )
        assertEquals(1, networkRequests.size)

        val expected =
            "{" +
                "\"events\": [" +
                "    {" +
                "        \"xdm\": {" +
                "            \"eventType\": \"analytics.track\"," +
                "            \"timestamp\": \"STRING_TYPE\"," +
                "            \"_id\": \"STRING_TYPE\"" +
                "        }," +
                "        \"data\": {" +
                "            \"__adobe\": {" +
                "                \"analytics\": {" +
                "                    \"cp\": \"foreground\"," +
                "                    \"linkName\": \"action name\"," +
                "                    \"linkType\": \"other\"," +
                "                    \"c1\": \"propValue1\"," +
                "                    \"contextData\": {" +
                "                        \"key1\": \"value1\"," +
                "                        \"a.AppID\": \"com.adobe.marketing.mobile.edge.bridge.test\"" +
                "                    }" +
                "                }" +
                "            }" +
                "        }" +
                "    }" +
                "]" +
                "}"

        assertExactMatch(
            expected,
            networkRequests[0].bodyJson,
            ElementCount(18, Subtree),
            ValueTypeMatch("events[0].xdm.timestamp", "events[0].xdm._id")
        )
    }

    @Test
    @Throws(InterruptedException::class, IOException::class)
    fun testRulesEngineResponse_sendsCorrectRequestEvent() {
        updateConfigurationWithRules("rules_analytics")
        resetTestExpectations()

        mockNetworkService.setExpectationForNetworkRequest(EDGE_INTERACT_ENDPOINT, POST, 1)

        // Triggers Analytics rule
        MobileCore.collectPii(
            mapOf("key" to "value")
        )

        mockNetworkService.assertAllNetworkRequestExpectations()

        val networkRequests = mockNetworkService.getNetworkRequestsWith(
            EDGE_INTERACT_ENDPOINT,
            POST,
            1000
        )

        assertEquals(1, networkRequests.size)

        val expected =
            "{" +
                "\"events\": [" +
                "    {" +
                "        \"xdm\": {" +
                "            \"eventType\": \"analytics.track\"," +
                "            \"timestamp\": \"STRING_TYPE\"," +
                "            \"_id\": \"STRING_TYPE\"" +
                "        }," +
                "        \"data\": {" +
                "            \"__adobe\": {" +
                "                \"analytics\": {" +
                "                    \"cp\": \"foreground\"," +
                "                    \"linkName\": \"Rule Action\"," +
                "                    \"linkType\": \"other\"," +
                "                    \"pageName\": \"Rule State\"," +
                "                    \"contextData\": {" +
                // Data is defined in the rule, not from the dispatched PII event
                "                        \"testKey\": \"testValue\"," +
                "                        \"a.AppID\": \"com.adobe.marketing.mobile.edge.bridge.test\"" +
                "                    }" +
                "                }" +
                "            }" +
                "        }" +
                "    }" +
                "]" +
                "}"

        assertExactMatch(
            expected,
            networkRequests[0].bodyJson,
            ElementCount(18, Subtree),
            ValueTypeMatch("events[0].xdm.timestamp", "events[0].xdm._id")
        )
    }

    /**
     * Helper function to update configuration with rules URL and mock response with a local zip file.
     * @param localRulesName name of bundled Assets file with rules definition, without '.zip' extension.
     */
    @Throws(InterruptedException::class, IOException::class)
    private fun updateConfigurationWithRules(localRulesName: String) {
        val rules = getAsset("$localRulesName.zip")
        assertNotNull("Local rules file '$localRulesName' was not found!", rules)

        val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }

        val response = object : HttpConnecting {
            private val props = mutableMapOf(
                "Last-Modified" to format.format(Date())
            )

            override fun getInputStream(): InputStream {
                return rules
            }

            override fun getErrorStream(): InputStream? {
                return null
            }

            override fun getResponseCode(): Int {
                return 200
            }

            override fun getResponseMessage(): String? {
                return null
            }

            override fun getResponsePropertyValue(s: String): String? {
                return props[s]
            }

            override fun close() {}
        }

        val rulesUrl = "https://rules.com/$localRulesName.zip"
        mockNetworkService.setMockResponseFor(rulesUrl, GET, response)
        mockNetworkService.setExpectationForNetworkRequest(rulesUrl, GET, 1)

        MobileCore.updateConfiguration(
            mutableMapOf<String, Any>(
                "rules.url" to rulesUrl
            )
        )
        mockNetworkService.assertAllNetworkRequestExpectations()
    }

    private fun resetTestExpectations() {
        mockNetworkService.reset()
        TestHelper.resetTestExpectations()
    }
}
