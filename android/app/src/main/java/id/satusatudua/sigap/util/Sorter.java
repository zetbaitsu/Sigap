/*
 * Copyright (c) 2015 SatuSatuDua.
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
 */

package id.satusatudua.sigap.util;

import java.util.Collections;
import java.util.List;

import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;

/**
 * Created on : December 25, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class Sorter {

    public static List<UserLocation> sortUserLocation(List<UserLocation> locations) {
        Location currentLocation = CacheManager.pluck().getUserLocation();
        Collections.sort(locations, (lhs, rhs) -> {
            Double distance1 = currentLocation.getDistance(lhs);
            Double distance2 = currentLocation.getDistance(rhs);
            return distance1.compareTo(distance2);
        });

        return locations;
    }
}
