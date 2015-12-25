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
import id.satusatudua.sigap.data.model.User;

/**
 * Created on : December 25, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class UsersSorter {

    public static List<User> sortByLocation(List<User> users) {
        User currentUser = CacheManager.pluck().getCurrentUser();
        Collections.sort(users, (lhs, rhs) -> {
            Double distance1 = currentUser.getLocation().getDistance(lhs.getLocation());
            Double distance2 = currentUser.getLocation().getDistance(rhs.getLocation());
            return distance1.compareTo(distance2);
        });

        return users;
    }
}
