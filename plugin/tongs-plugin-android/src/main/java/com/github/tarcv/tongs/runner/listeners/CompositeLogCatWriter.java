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
package com.github.tarcv.tongs.runner.listeners;

import com.android.ddmlib.logcat.LogCatMessage;

import java.util.Arrays;
import java.util.List;

class CompositeLogCatWriter implements LogCatWriter {

	private final LogCatWriter[] logCatWriters;

	CompositeLogCatWriter(LogCatWriter... logCatWriters) {
		this.logCatWriters = Arrays.copyOf(logCatWriters, logCatWriters.length);
	}

	@Override
	public void writeLogs(List<LogCatMessage> logCatMessages) {
		for (LogCatWriter logCatWriter : logCatWriters) {
			logCatWriter.writeLogs(logCatMessages);
		}
	}

}
