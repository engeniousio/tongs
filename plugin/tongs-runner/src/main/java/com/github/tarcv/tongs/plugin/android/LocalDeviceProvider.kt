/*
 * Copyright 2019 TarCV
 * Copyright 2015 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.github.tarcv.tongs.plugin.android

import com.github.tarcv.tongs.injector.device.ConnectedDeviceProviderInjector
import com.github.tarcv.tongs.model.Device
import com.github.tarcv.tongs.plugin.DeviceProvider
import com.github.tarcv.tongs.plugin.DeviceProviderContext
import com.github.tarcv.tongs.plugin.DeviceProviderFactory
import com.github.tarcv.tongs.system.adb.ConnectedDeviceProvider
import java.util.stream.Collectors

class LocalDeviceProviderFactory: DeviceProviderFactory<LocalDeviceProvider> {
    override fun create(context: DeviceProviderContext): LocalDeviceProvider {
        return LocalDeviceProvider(
                ConnectedDeviceProviderInjector.connectedDeviceProvider(),
                HashSet(context.configuration.excludedSerials)
        )
    }
}

class LocalDeviceProvider(
        private val connectedDeviceProvider: ConnectedDeviceProvider,
        private val excludedSerials: Set<String>
) : DeviceProvider {
    override fun provideDevices(): Set<Device> {
        val deviceLoader = connectedDeviceProvider
        deviceLoader.init()
        return deviceLoader.getDevices().stream()
                .filter({ d -> !excludedSerials.contains(d.serial) })
                .collect(Collectors.toSet<Device>())
    }
}
