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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.adobe.marketing.mobile.Assurance
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.tutorial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOG_SOURCE = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle deep linking, and connecting to Assurance
        val data = intent.data

        if (data != null) {
            Assurance.startSession("$data")
            Log.debug(MainApp.LOG_TAG, LOG_SOURCE, "Deep link received $data")
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause()
    }

    override fun onResume() {
        super.onResume()
        MobileCore.setApplication(application)
        MobileCore.lifecycleStart(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.connectToAssurance) {
            val intent = Intent(this, AssuranceActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
