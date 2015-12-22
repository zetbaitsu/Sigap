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
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import timber.log.Timber;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class VerificationPresenter extends BenihPresenter<VerificationPresenter.View> {

    private User currentUser;

    public VerificationPresenter(View view) {
        super(view);
        listenCurrentUser();
    }

    private void listenCurrentUser() {
        CacheManager.pluck().getCurrentUser()
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                .subscribe(user -> {
                    if (user != null) {
                        currentUser = user;
                        Timber.d("currentUser : " + currentUser);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    public void verify(String token) {
        view.showLoading();
        FirebaseApi.pluck()
                .getApi()
                .authWithPassword(currentUser.getEmail(), token, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        currentUser.setUid(authData.getUid());
                        CacheManager.pluck().cacheCurrentUser(currentUser);
                        StateManager.pluck().setState(StateManager.State.SET_PASSWORD);
                        StateManager.pluck().setToken(token);
                        if (view != null) {
                            view.onSuccessVerification(currentUser);
                            view.dismissLoading();
                        }
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        if (view != null) {
                            view.showError(firebaseError.getMessage());
                            view.dismissLoading();
                        }
                    }
                });

    }

    public void resendCode() {
        view.showLoading();
        FirebaseApi.pluck().getApi().resetPassword(currentUser.getEmail(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                if (view != null) {
                    view.onSuccessResendCode();
                    view.dismissLoading();
                }
            }

            @Override
            public void onError(FirebaseError firebaseError) {
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
        void onSuccessVerification(User user);

        void onSuccessResendCode();

    }
}
