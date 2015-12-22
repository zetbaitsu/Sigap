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

package id.satusatudua.sigap.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.util.Bson;
import rx.Observable;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public enum CacheManager {
    HARVEST;

    private final SharedPreferences sharedPreferences;
    private final RxSharedPreferences rxPreferences;

    CacheManager() {
        sharedPreferences = SigapApp.pluck().getSharedPreferences("sigap.zl", Context.MODE_PRIVATE);
        rxPreferences = RxSharedPreferences.create(sharedPreferences);
    }

    public static CacheManager pluck() {
        return HARVEST;
    }

    public void cacheCurrentUser(User user) {
        sharedPreferences.edit().putString("current_user", Bson.pluck().getParser().toJson(user)).apply();
    }

    public Observable<User> getCurrentUser() {
        return rxPreferences.getString("current_user", "")
                .asObservable()
                .map(s -> Bson.pluck().getParser().fromJson(s, User.class));
    }
}
