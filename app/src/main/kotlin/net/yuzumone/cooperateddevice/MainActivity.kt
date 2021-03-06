/*
 * Copyright (C) 2017 yuzumone
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.yuzumone.cooperateddevice

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.yuzumone.cooperateddevice.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private val regex = Regex("(([0-9a-f]{2}:){5}[0-9a-f]{2})")
    lateinit private var binding: ActivityMainBinding
    private val adapter: ConfigurationAdapter by lazy {
        ConfigurationAdapter(this)
    }
    private val wifiManager: WifiManager by lazy {
        getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        getConfiguredNetworks()
        binding.buttonConnect.setOnClickListener {
            enableUsbTethering()
            val mac = obtainMacAddress()
            binding.text.text = "MACAddress: $mac"
        }
        binding.list.setOnItemClickListener { parent, view, position, id ->
            val configuration = adapter.getItem(position)
            changeWifi(configuration)
        }
    }

    private fun enableUsbTethering() {
        // for android 6.0.0
        val command = arrayOf("su", "-c", "service call connectivity 30 i32 1")
        val process = Runtime.getRuntime().exec(command)
        process.waitFor()
    }

    private fun obtainMacAddress(): String {
        var mac = ""
        val command = arrayOf("ip", "link")
        val process = Runtime.getRuntime().exec(command)
        val result = process.inputStream.reader().use { it.readLines() }
        try {
            mac = regex.findAll(result.last()).single { it.value != "ff:ff:ff:ff:ff:ff" }.value
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return mac
    }

    private fun getConfiguredNetworks() {
        if (wifiManager.configuredNetworks != null) {
            adapter.addAll(wifiManager.configuredNetworks)
            adapter.notifyDataSetChanged()
            binding.list.adapter = adapter
        }
    }

    private fun changeWifi(configuration: WifiConfiguration) {
        val info = wifiManager.connectionInfo
        if (info.ssid == configuration.SSID) {
            return
        } else {
            wifiManager.disableNetwork(info.networkId)
            wifiManager.enableNetwork(info.networkId, false)
        }
        wifiManager.updateNetwork(configuration)
        wifiManager.enableNetwork(configuration.networkId, true)
    }
}
