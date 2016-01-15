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

import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ConfirmTrustedPresenter extends BenihPresenter<ConfirmTrustedPresenter.View> {

    public User currentUser;

    public ConfirmTrustedPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void loadUser(String userId) {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().users(userId))
                .doOnNext(dataSnapshot -> Timber.d("Load data: " + dataSnapshot))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .doOnNext(user -> Timber.d("Adding me: " + user))
                .subscribe(user -> {
                    if (view != null) {
                        view.showUser(user);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                        view.dismissLoading();
                    }
                });
    }

    public void accept(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userTrusted/" + userId + "/" + currentUser.getUserId() + "/status/", "DITERIMA");
        data.put("trustedOf/" + currentUser.getUserId() + "/" + userId + "/status/", "DITERIMA");

        FirebaseApi.pluck()
                .getApi()
                .updateChildren(data, (firebaseError, firebase) -> {
                    if (firebaseError != null) {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError("Gagal mengirimkan data orang yang anda percaya!");
                            view.dismissLoading();
                        }
                    } else {
                        if (view != null) {
                            view.onAccepted(userId);
                            view.dismissLoading();
                        }
                    }
                });
    }

    public void decline(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userTrusted/" + userId + "/" + currentUser.getUserId() + "/status/", "DITOLAK");
        data.put("trustedOf/" + currentUser.getUserId() + "/" + userId + "/status/", "DITOLAK");

        FirebaseApi.pluck()
                .getApi()
                .updateChildren(data, (firebaseError, firebase) -> {
                    if (firebaseError != null) {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError("Gagal mengirimkan data orang yang anda percaya!");
                            view.dismissLoading();
                        }
                    } else {
                        if (view != null) {
                            view.onDeclined(userId);
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
        void showUser(User user);

        void onAccepted(String userId);

        void onDeclined(String userId);
    }
}
