/*
 * Copyright 2020 TarCV
 * Copyright 2018 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.github.tarcv.tongs.util;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.github.tarcv.tongs.model.Pool;
import com.github.tarcv.tongs.model.TestCase;
import com.github.tarcv.tongs.runner.TestCaseRunResult;
import com.github.tarcv.tongs.runner.listeners.TongsTestListener;
import com.github.tarcv.tongs.summary.ResultStatus;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static com.github.tarcv.tongs.model.Device.TEST_DEVICE;
import static com.github.tarcv.tongs.model.Pool.Builder.aDevicePool;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class TestPipelineEmulator {
    private final String trace;
    private final String fatalErrorMessage;
    private final Pool testPool = aDevicePool()
            .addDevice(TEST_DEVICE)
            .build();

    private TestPipelineEmulator(Builder builder) {
        this.trace = builder.trace;
        this.fatalErrorMessage = builder.fatalErrorMessage;
    }

    public void emulateFor(TongsTestListener testRunListener, TestCase testCase) {
        TestIdentifier test = new TestIdentifier(testCase.getTestClass(), testCase.getTestMethod());
        testRunListener.onTestStarted();
        if (trace != null) {
            testRunListener.onTestFailed(failureResult(testCase, ResultStatus.FAIL, trace));
        } else if (fatalErrorMessage != null) {
            testRunListener.onTestFailed(failureResult(testCase, ResultStatus.ERROR, fatalErrorMessage));
        } else {
            testRunListener.onTestSuccessful();
        }
    }

    @NotNull
    private TestCaseRunResult failureResult(TestCase testCase, ResultStatus status, String trace) {
        return new TestCaseRunResult(testPool, TEST_DEVICE, testCase, status, trace,
                Instant.now(), Instant.now().plusMillis(100), Instant.now(), Instant.now().plusMillis(100),
                0, emptyMap(), null, emptyList());
    }

    // TODO: Rewrite for new Tongs pipeline
    public static class Builder {
        private String trace;
        private String fatalErrorMessage;

        public static Builder testPipelineEmulator() {
            return new Builder();
        }

        public Builder withTrace(String trace) {
            this.trace = trace;
            return this;
        }

        public Builder withFatalErrorMessage(String fatalErrorMessage) {
            this.fatalErrorMessage = fatalErrorMessage;
            return this;
        }

        public TestPipelineEmulator build() {
            return new TestPipelineEmulator(this);
        }
    }
}
