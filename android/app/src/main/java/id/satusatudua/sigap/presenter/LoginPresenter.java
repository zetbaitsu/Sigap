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

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.PasswordUtils;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import timber.log.Timber;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LoginPresenter extends BenihPresenter<LoginPresenter.View> {

    public LoginPresenter(View view) {
        super(view);
    }

    public void login(String email, String password) {
        view.showLoading();
        FirebaseApi.pluck()
                .getApi()
                .authWithPassword(email, PasswordUtils.hashPassword(password), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("users").child(authData.getUid()))
                                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                                .subscribe(user -> {
                                    Timber.d("get logged data: " + user.toString());
                                    Timber.d("is user logged: " + user.isFromApps());
                                    if (user.isFromApps()) {
                                        FirebaseApi.pluck().getApi().unauth();
                                        if (view != null) {
                                            view.showError("Akun ini masih login di device lain!");
                                            view.dismissLoading();
                                        }
                                    } else {
                                        user.setUid(authData.getUid());
                                        user.setFromApps(true);
                                        FirebaseApi.pluck()
                                                .getApi()
                                                .child("users")
                                                .child(user.getUid())
                                                .setValue(user, (firebaseError, firebase) -> {
                                                    if (firebaseError != null) {
                                                        Timber.d(firebaseError.getMessage());
                                                        if (view != null) {
                                                            view.showError(firebaseError.getMessage());
                                                            view.dismissLoading();
                                                        }
                                                    } else {
                                                        CacheManager.pluck().cacheCurrentUser(user);
                                                        StateManager.pluck().setState(StateManager.State.LOGGED);
                                                        if (view != null) {
                                                            view.onSuccessLogin(user);
                                                            view.dismissLoading();
                                                        }
                                                    }
                                                });

                                    }
                                }, throwable -> {
                                    Timber.e(throwable.getMessage());
                                    if (view != null) {
                                        view.showError(throwable.getMessage());
                                        view.dismissLoading();
                                    }
                                });
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError(firebaseError.getMessage());
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
        void onSuccessLogin(User user);
    }
}
