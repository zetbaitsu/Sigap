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

package id.satusatudua.sigap.data;

import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.util.BenihPreferenceUtils;
import id.zelory.benih.util.Bson;

/**
 * Created on : November 23, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LocalDataManager {

    public static void saveCurrentUser(User user) {
        BenihPreferenceUtils.putString(SigapApp.pluck().getApplicationContext(),
                                       "current_user",
                                       Bson.pluck().getParser().toJson(user));
    }

    public static User getCurrentUser() {
        return Bson.pluck()
                .getParser()
                .fromJson(BenihPreferenceUtils.getString(SigapApp.pluck().getApplicationContext(), "current_user"), User.class);
    }
}
