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
package com.github.tarcv.tongs;

import com.github.tarcv.tongs.api.devices.Device;
import com.github.tarcv.tongs.api.devices.DisplayGeometry;

import java.util.Map;

public class ComputedPooling {
    public enum Characteristic implements DeviceCharacteristicReader {
        sw {
            @Override
            public boolean canPool(Device device) {
                return device.getGeometry() != null;
            }

            @Override
            public int getParameter(Device device) {
                DisplayGeometry geometry = device.getGeometry();
                if (geometry != null) {
                    return geometry.getSwDp();
                } else {
                    return 0;
                }
            }

            @Override
            public String getBaseName() {
                return this.name();
            }
        },
        api {
            @Override
            public boolean canPool(Device device) {
                return true;
            }

            @Override
            public int getParameter(Device device) {
                return device.getOsApiLevel();
            }

            @Override
            public String getBaseName() {
                return this.name();
            }
        }
    }

    public Characteristic characteristic;
    public Map<String, Integer> groups;
}
