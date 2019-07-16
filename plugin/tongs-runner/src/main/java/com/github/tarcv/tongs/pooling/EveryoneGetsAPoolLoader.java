/*
 * Copyright 2018 TarCV
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
package com.github.tarcv.tongs.pooling;

import com.github.tarcv.tongs.model.Device;
import com.github.tarcv.tongs.model.Pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.tarcv.tongs.model.Pool.Builder.aDevicePool;

/**
 * Assigns one pool per device
 */
public class EveryoneGetsAPoolLoader implements DevicePoolLoader {

	@Override
	public Collection<Pool> loadPools(List<Device> devices) {
        ArrayList<Pool> pools = new ArrayList<>();
        for (Device device : devices) {
            Pool pool = aDevicePool().addDevice(device).withName(device.getSerial()).build();
            pools.add(pool);
        }
		return pools;
	}
}
