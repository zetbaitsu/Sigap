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

package id.satusatudua.sigap.presenter;

import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import id.satusatudua.sigap.data.LocalDataManager;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class RegisterPresenter extends BenihPresenter<RegisterPresenter.View> {

    public RegisterPresenter(View view) {
        super(view);
    }

    public void register(User user, String password) {
        view.showLoading();
        FirebaseApi.pluck()
                .getApi()
                .createUser(user.getEmail(), password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {
                        Timber.d("Logged with data: " + stringObjectMap.toString());
                        FirebaseApi.pluck()
                                .getApi()
                                .authWithPassword(user.getEmail(), password, new Firebase.AuthResultHandler() {
                                    @Override
                                    public void onAuthenticated(AuthData authData) {
                                        user.setUid(authData.getUid());
                                        FirebaseApi.pluck()
                                                .getApi()
                                                .child("users")
                                                .child(user.getUid())
                                                .setValue(user, (firebaseError, firebase) -> {
                                                    if (firebaseError != null) {
                                                        Timber.e(firebaseError.getMessage());
                                                        view.onFailedRegister(firebaseError);
                                                        view.dismissLoading();
                                                    } else if (view != null) {
                                                        LocalDataManager.saveCurrentUser(user);
                                                        view.onSuccessRegister(user);
                                                        view.dismissLoading();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onAuthenticationError(FirebaseError firebaseError) {
                                        if (view != null) {
                                            view.onFailedRegister(firebaseError);
                                            view.dismissLoading();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Timber.e("Failed to create user because " + firebaseError.getMessage());
                        if (view != null) {
                            view.onFailedRegister(firebaseError);
                            view.dismissLoading();
                        }
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void onSuccessRegister(User user);

        void onFailedRegister(FirebaseError error);
    }
}
