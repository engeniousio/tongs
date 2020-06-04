/*
 * Copyright 2020 TarCV
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.tarcv.tongs.runner

import com.android.ddmlib.logcat.LogCatMessage
import com.github.tarcv.tongs.device.clearLogcat
import com.github.tarcv.tongs.model.AndroidDevice
import com.github.tarcv.tongs.runner.listeners.LogcatReceiver
import com.github.tarcv.tongs.api.run.TestCaseRunRule
import com.github.tarcv.tongs.api.run.TestCaseRunRuleAfterArguments
import com.github.tarcv.tongs.suite.JUnitTestSuiteLoader
import com.github.tarcv.tongs.suite.JUnitTestSuiteLoader.Companion.logcatWaiterSleep
import com.github.tarcv.tongs.suite.TestCollectingListener
import com.google.gson.JsonObject
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch

internal class AndroidCollectingTestCaseRunRule(
        private val device: AndroidDevice,
        private val testCollectingListener: TestCollectingListener,
        private val latch: CountDownLatch
): TestCaseRunRule {
    var logCatCollector: LogcatReceiver = LogcatReceiver(device)
    val jsonInfoDecoder = JsonInfoDecorder()

    override fun before() {
        clearLogcat(device.deviceInterface)
        logCatCollector.start("TestSuiteLoader")
    }

    override fun after(arguments: TestCaseRunRuleAfterArguments) {
        try {
            sleep(logcatWaiterSleep) // make sure all logcat messages are read
        } finally {
            try {
                logCatCollector.stop()

                tryCollectingAndDecodingInfos()
            } finally {
                latch.countDown()
            }
        }
    }

    private fun tryCollectingAndDecodingInfos() {
        try {
            val rawMessages = extractTestInfoMessages(logCatCollector.messages)

            val items = jsonInfoDecoder.decodeStructure(rawMessages)

            val testToInfoMap = items
                    .asReversed() // make sure the first entry for duplicate keys is used
                    .associateBy { it.identifier }
            testCollectingListener.publishTestInfo(testToInfoMap)
        } catch (e: Exception) {
            logger.warn("Failed to collect annotation and structure information about tests", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AndroidCollectingTestCaseRunRule::class.java)
    }
}

fun extractTestInfoMessages(messages: List<LogCatMessage>): List<JsonObject> {
    return messages.stream()
            .filter { logCatMessage -> "Tongs.TestInfo" == logCatMessage.tag }
            .collect(JUnitTestSuiteLoader.TestInfoCatCollector())
}
