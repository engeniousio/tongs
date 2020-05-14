/*
 * Copyright 2020 TarCV
 * Copyright 2014 Shazam Entertainment Limited
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
package com.github.tarcv.tongs.injector

import com.github.tarcv.tongs.TongsRunner
import com.github.tarcv.tongs.injector.pooling.PoolLoaderInjector.poolLoader
import com.github.tarcv.tongs.injector.runner.PoolTestRunnerFactoryInjector
import com.github.tarcv.tongs.injector.runner.ProgressReporterInjector
import com.github.tarcv.tongs.injector.summary.SummaryGeneratorHookInjector
import com.github.tarcv.tongs.plugin.android.PropertiesTestCaseRuleFactory
import com.github.tarcv.tongs.runner.rules.TestCaseRule
import com.github.tarcv.tongs.runner.rules.TestCaseRuleContext
import com.github.tarcv.tongs.runner.rules.TestCaseRuleFactory
import com.github.tarcv.tongs.utils.Utils
import org.slf4j.LoggerFactory

object TongsRunnerInjector {
    private val logger = LoggerFactory.getLogger(TongsRunnerInjector::class.java)

    @JvmStatic
    fun createTongsRunner(ruleManagerFactory: RuleManagerFactory): TongsRunner {
        val startNanos = System.nanoTime()

        val ruleManager: TestCaseRuleManager = ruleManagerFactory.create(
                TestCaseRuleFactory::class.java,
                listOf(PropertiesTestCaseRuleFactory()),
                { factory, context: TestCaseRuleContext -> factory.testCaseRules(context) }
        )
        val tongsRunner = TongsRunner(
                poolLoader(ruleManagerFactory),
                PoolTestRunnerFactoryInjector.poolTestRunnerFactory(ruleManagerFactory),
                ProgressReporterInjector.progressReporter(),
                SummaryGeneratorHookInjector.summaryGeneratorHook(),
                ruleManager
        )

        logger.debug("Bootstrap of TongsRunner took: {} milliseconds", Utils.millisSinceNanoTime(startNanos))
        return tongsRunner
    }
}

typealias TestCaseRuleManager = RuleManagerFactory.RuleManager<TestCaseRuleContext, TestCaseRule, TestCaseRuleFactory<TestCaseRule>>
