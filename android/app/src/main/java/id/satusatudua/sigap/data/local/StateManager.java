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
import rx.Observable;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public enum StateManager {
    HARVEST;

    private final SharedPreferences sharedPreferences;
    private final RxSharedPreferences rxPreferences;

    StateManager() {
        sharedPreferences = SigapApp.pluck().getSharedPreferences("sigap.state", Context.MODE_PRIVATE);
        rxPreferences = RxSharedPreferences.create(sharedPreferences);
    }

    public State getState() {
        return State.valueOf(sharedPreferences.getString("user_state", "NEW"));
    }

    public Observable<State> listenState() {
        return rxPreferences.getString("user_state", "NEW").asObservable().map(State::valueOf);
    }

    public void setState(State state) {
        sharedPreferences.edit().putString("user_state", state.name()).apply();
    }

    public State recoveryState() {
        return State.valueOf(sharedPreferences.getString("backup_state", "NEW"));
    }

    public void backupState() {
        sharedPreferences.edit().putString("backup_state", getState().name()).apply();
    }

    public String getToken() {
        return sharedPreferences.getString("user_token", "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString("user_token", token).apply();
    }

    public void setLastEmail(String email) {
        sharedPreferences.edit().putString("last_email_try", email).apply();
    }

    public String getLastEmail() {
        return sharedPreferences.getString("last_email_try", "");
    }

    public static StateManager pluck() {
        return HARVEST;
    }

    public enum State {
        NEW, VERIFY_EMAIL, SET_PASSWORD, LOGGED, LOGOUT, DITOLONG, ADDING_TRUSTED_USER, MENOLONG, ENTER_CODE, UPDATE_PASSWORD
    }
}
