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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TrustMePresenter extends BenihPresenter<TrustMePresenter.View> {

    public User currentUser;
    private List<UserTrusted> trustMeList;

    public TrustMePresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
        trustMeList = new ArrayList<>();
        listenTrustMeAdded();
        listenTrustMeRemoved();
    }

    public void accept(UserTrusted userTrusted) {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("userTrusted/" + userTrusted.getUserTrustedId() + "/" + currentUser.getUserId() + "/status/", "DITERIMA");
        data.put("trustedOf/" + currentUser.getUserId() + "/" + userTrusted.getUserTrustedId() + "/status/", "DITERIMA");

        FirebaseApi.pluck()
                .getApi()
                .updateChildren(data, (firebaseError, firebase) -> {
                    if (firebaseError != null) {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError("Gagal mengkonfirmasi user!");
                            view.dismissLoading();
                        }
                    } else {
                        userTrusted.setStatus(UserTrusted.Status.DITERIMA);
                        if (view != null) {
                            view.onAccepted(userTrusted);
                            view.dismissLoading();
                        }
                    }
                });
    }

    public void decline(UserTrusted userTrusted) {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("userTrusted/" + userTrusted.getUserTrustedId() + "/" + currentUser.getUserId() + "/status/", "DITOLAK");
        data.put("trustedOf/" + currentUser.getUserId() + "/" + userTrusted.getUserTrustedId() + "/status/", "DITOLAK");

        FirebaseApi.pluck()
                .getApi()
                .updateChildren(data, (firebaseError, firebase) -> {
                    if (firebaseError != null) {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError("Gagal menolak user!");
                            view.dismissLoading();
                        }
                    } else {
                        userTrusted.setStatus(UserTrusted.Status.DITOLAK);
                        if (view != null) {
                            view.onDeclined(userTrusted);
                            view.dismissLoading();
                        }
                    }
                });
    }

    public void loadTrustMeUser() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().trustedOf(currentUser.getUserId()))
                .doOnNext(dataSnapshots -> {
                    if (dataSnapshots.getValue() == null) {
                        if (view != null) {
                            view.dismissLoading();
                        }
                    }
                })
                .flatMap(dataSnapshots -> Observable.from(dataSnapshots.getChildren()))
                .filter(dataSnapshot -> !dataSnapshot.child("status").getValue().toString().equals("DITOLAK"))
                .map(dataSnapshot -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(dataSnapshot.getKey());
                    userTrusted.setStatus(UserTrusted.Status.valueOf(dataSnapshot.child("status").getValue().toString()));

                    int x = trustMeList.indexOf(userTrusted);
                    if (x >= 0) {
                        trustMeList.get(x).setStatus(userTrusted.getStatus());
                    } else {
                        trustMeList.add(userTrusted);
                    }
                    return userTrusted;
                })
                .flatMap(userTrusted -> RxFirebase.observeOnce(FirebaseApi.pluck().users(userTrusted.getUserTrustedId()))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class)))
                .map(user -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(user.getUserId());

                    int x = trustMeList.indexOf(userTrusted);
                    if (x >= 0) {
                        trustMeList.get(x).setUser(user);
                    }

                    return trustMeList.get(x);
                })
                .subscribe(userTrusted -> {
                    if (view != null) {
                        view.onTrustMeUserAdded(userTrusted);
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

    private void listenTrustMeAdded() {
        RxFirebase.observeChildAdded(FirebaseApi.pluck().trustedOf(currentUser.getUserId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .filter(dataSnapshot -> !dataSnapshot.child("status").getValue().toString().equals("DITOLAK"))
                .map(dataSnapshot -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(dataSnapshot.getKey());
                    userTrusted.setStatus(UserTrusted.Status.valueOf(dataSnapshot.child("status").getValue().toString()));

                    int x = trustMeList.indexOf(userTrusted);
                    if (x >= 0) {
                        trustMeList.get(x).setStatus(userTrusted.getStatus());
                    } else {
                        trustMeList.add(userTrusted);
                    }
                    return userTrusted;
                })
                .flatMap(userTrusted -> RxFirebase.observeOnce(FirebaseApi.pluck().users(userTrusted.getUserTrustedId()))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class)))
                .map(user -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(user.getUserId());

                    int x = trustMeList.indexOf(userTrusted);
                    if (x >= 0) {
                        trustMeList.get(x).setUser(user);
                    }

                    return trustMeList.get(x);
                })
                .subscribe(userTrusted -> {
                    if (view != null) {
                        view.onTrustMeUserAdded(userTrusted);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    private void listenTrustMeRemoved() {
        RxFirebase.observeChildRemoved(FirebaseApi.pluck().trustedOf(currentUser.getUserId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(dataSnapshot.getKey());
                    return userTrusted;
                })
                .subscribe(userTrusted -> {
                    trustMeList.remove(userTrusted);
                    if (view != null) {
                        view.onTrustMeUserRemoved(userTrusted);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelableArrayList("trust_me", (ArrayList<UserTrusted>) trustMeList);
    }

    @Override
    public void loadState(Bundle bundle) {
        trustMeList = bundle.getParcelableArrayList("trust_me");
        if (trustMeList == null) {
            loadTrustMeUser();
        } else {
            int size = trustMeList.size();
            for (int i = 0; i < size; i++) {
                view.onTrustMeUserAdded(trustMeList.get(i));
            }
        }
    }

    public interface View extends BenihPresenter.View {
        void onAccepted(UserTrusted userTrusted);

        void onDeclined(UserTrusted userTrusted);

        void onTrustMeUserAdded(UserTrusted userTrusted);

        void onTrustMeUserRemoved(UserTrusted userTrusted);
    }
}
