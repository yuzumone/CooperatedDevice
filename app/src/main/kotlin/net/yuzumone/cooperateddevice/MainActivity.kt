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

import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.yuzumone.cooperateddevice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        val regex = Regex("(([0-9a-f]{2}:){5}[0-9a-f]{2})")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.button.setOnClickListener {
            tryUsbTethering()
        }
    }

    private fun tryUsbTethering() {
        if (checkSu()) {
            // for android 5.1.1
            val command = arrayOf("su", "-c", "service call connectivity 31 i32 1")
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
            val mac = obtainMacAddress()
            binding.text.text = "MACAddress: $mac"
        } else {
            openTetheringSetting()
        }
    }

    private fun obtainMacAddress(): String {
        var mac = ""
        val command = arrayOf("ip", "link")
        val process = Runtime.getRuntime().exec(command)
        val result = process.inputStream.reader().use { it.readLines() }
        try {
            mac = regex.findAll(result.last()).single { it.value != "ff:ff:ff:ff:ff:ff" }.value
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return mac
    }

    private fun openTetheringSetting() {
        val intent = Intent()
        intent.setClassName("com.android.settings", "com.android.settings.TetherSettings")
        startActivity(intent)
    }

    private fun checkSu(): Boolean {
        try {
            packageManager.getApplicationInfo("eu.chainfire.supersu", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }
}