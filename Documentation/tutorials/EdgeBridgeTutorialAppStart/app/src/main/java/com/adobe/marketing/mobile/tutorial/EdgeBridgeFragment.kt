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

package com.adobe.marketing.mobile.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.tutorial.databinding.FragmentFirstBinding

class EdgeBridgeFragment : Fragment() {

    private var binding: FragmentFirstBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.trackActionButton?.setOnClickListener {
            MobileCore.trackAction(
                "purchase",
                hashMapOf(
                    "&&products" to ";Running Shoes;1;69.95;event1|event2=55.99;eVar1=12345,;Running Socks;10;29.99;event2=10.95;eVar1=54321",
                    "&&events" to "event5,purchase",
                    "myapp.promotion" to "a0138"
                )
            )
        }

        binding?.trackStateButton?.setOnClickListener {
            MobileCore.trackState(
                "products/189025/runningshoes/12345",
                hashMapOf(
                    "&&products" to ";Running Shoes;1;69.95;prodView|event2=55.99;eVar1=12345",
                    "myapp.category" to "189025",
                    "myapp.promotion" to "a0138"
                )
            )
        }

        binding?.triggerRuleButton?.setOnClickListener {
            MobileCore.collectPii(
                hashMapOf("key" to "trigger")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
