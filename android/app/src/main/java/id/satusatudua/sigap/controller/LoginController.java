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

package id.satusatudua.sigap.controller;

import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import id.satusatudua.sigap.controller.event.ErrorEvent;
import id.satusatudua.sigap.data.LocalDataManager;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.controller.BenihController;
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
public class LoginController extends BenihController<LoginController.Presenter> {

    public LoginController(Presenter presenter) {
        super(presenter);
    }

    public void login(String email, String password) {
        presenter.showLoading();
        if (FirebaseApi.pluck().getApi().getAuth() == null) {
            FirebaseApi.pluck()
                    .getApi()
                    .authWithPassword(email, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Timber.d("Logged with data: " + authData);
                            RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("users").child(authData.getUid()))
                                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                                    .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                                    .subscribe(user -> {
                                        if (presenter != null) {
                                            LocalDataManager.saveCurrentUser(user);
                                            presenter.onSuccessLogin(user);
                                            presenter.dismissLoading();
                                        }
                                    }, throwable -> {
                                        Timber.e(throwable.getMessage());
                                        if (presenter != null) {
                                            presenter.showError(ErrorEvent.LOAD_USER);
                                            presenter.dismissLoading();
                                        }
                                    });
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            Timber.e("Failed to create user because " + firebaseError.getMessage());
                            if (presenter != null) {
                                presenter.onFailedLogin(firebaseError);
                                presenter.dismissLoading();
                            }
                        }
                    });
        } else {
            presenter.dismissLoading();
        }
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface Presenter extends BenihController.Presenter {
        void onSuccessLogin(User user);

        void onFailedLogin(FirebaseError error);
    }
}
