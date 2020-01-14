/*
 * Copyright 2020 TarCV
 * Copyright 2016 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.github.tarcv.tongs.plugin.android

import com.github.tarcv.tongs.model.TestCaseEvent
import com.github.tarcv.tongs.runner.rules.TestCaseRule
import com.github.tarcv.tongs.runner.rules.TestCaseRuleContext
import com.github.tarcv.tongs.runner.rules.TestCaseRuleFactory
import com.github.tarcv.tongs.suite.JUnitTestSuiteLoader

class PropertiesTestCaseRuleFactory: TestCaseRuleFactory<PropertiesTestCaseRule> {
    override fun testCaseRules(context: TestCaseRuleContext): Array<out PropertiesTestCaseRule> {
        return arrayOf(PropertiesTestCaseRule())
    }
}

class PropertiesTestCaseRule: TestCaseRule {
    override fun transform(testCaseEvent: TestCaseEvent): TestCaseEvent {
        val properties = HashMap<String, String>(testCaseEvent.testCase.properties)
        testCaseEvent.testCase.annotations.forEach {
            when (it.fullyQualifiedName) {
                "com.github.tarcv.tongs.TestProperties" -> {
                    val keys = it.properties["keys"] as List<String>
                    val values = it.properties["values"] as List<String>
                    keyValueArraysToProperties(properties, keys, values)
                }
                "com.github.tarcv.tongs.TestPropertyPairs" -> {
                    val values = it.properties["value"] as List<String>
                    keyValuePairsToProperties(properties, values)
                }
            }
        }

        return TestCaseEvent.newTestCase(
                testCaseEvent.testCase.testMethod,
                testCaseEvent.testCase.testClass,
                properties,
                testCaseEvent.testCase.annotations,
                testCaseEvent.excludedDevices
        )
    }
}

private fun keyValueArraysToProperties(properties: MutableMap<String, String>, keys: List<String>, values: List<String>) {
    if (keys.size != values.size) {
        throw RuntimeException("Numbers of key and values in test properties annotations should be the same")
    }
    for (i in keys.indices) {
        properties[keys[i]] = values[i]
    }
}

private fun keyValuePairsToProperties(properties: MutableMap<String, String>, values: List<String>) {
    if (values.size != values.size / 2 * 2) {
        throw RuntimeException("Number of values in test property pairs annotations should be even")
    }
    var i = 0
    while (i < values.size) {
        properties[values[i]] = values[i + 1]
        i += 2
    }
}
