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

import com.firebase.client.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.util.IteratorUtils;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import id.zelory.benih.util.BenihWorker;
import timber.log.Timber;

/**
 * Created on : January 11, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TrustedUserPresenter extends BenihPresenter<TrustedUserPresenter.View> {

    private User currentUser;

    public TrustedUserPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
        listenTrustedUserAdded();
        listenTrustedUserChanged();
    }

    public void searchUser(String email) {
        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            if (view != null) {
                view.showError("Masukan email valid selain email anda sendiri!");
                view.dismissLoading();
            }
        } else {
            view.showLoading();
            RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("users"))
                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                    .map(DataSnapshot::getChildren)
                    .map(dataSnapshots -> IteratorUtils.toList(dataSnapshots, User.class))
                    .subscribe(users -> {
                        final int[] x = {-1};
                        BenihWorker.pluck().doInComputation(() -> {
                            int size = users.size();
                            for (int i = 0; i < size; i++) {
                                if (users.get(i).getEmail().equalsIgnoreCase(email)) {
                                    x[0] = i;
                                    break;
                                }
                            }
                        }).subscribe(o -> {
                            if (x[0] == -1) {
                                if (view != null) {
                                    view.onNotFoundUser(email);
                                    view.dismissLoading();
                                }
                            } else {
                                if (view != null) {
                                    view.onFoundUser(users.get(x[0]));
                                    view.dismissLoading();
                                }
                            }
                        }, throwable -> {
                            Timber.e("Error search: " + throwable.getMessage());
                            if (view != null) {
                                view.showError(throwable.getMessage());
                                view.dismissLoading();
                            }
                        });
                    }, throwable -> {
                        Timber.e("Error search: " + throwable.getMessage());
                        if (view != null) {
                            view.showError(throwable.getMessage());
                            view.dismissLoading();
                        }
                    });
        }
    }

    private void listenTrustedUserAdded() {
        view.showLoading();
        final DataSnapshot[] temp = new DataSnapshot[1];
        RxFirebase.observeChildAdded(FirebaseApi.pluck().userTrusted(currentUser.getUserId()))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .doOnNext(dataSnapshot -> temp[0] = dataSnapshot)
                .flatMap(dataSnapshot -> RxFirebase.observeOnce(FirebaseApi.pluck().users(dataSnapshot.getKey()))
                        .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class)))
                .map(user -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(temp[0].getKey());
                    userTrusted.setStatus(UserTrusted.Status.valueOf(temp[0].child("status").getValue().toString()));
                    userTrusted.setUser(user);
                    return userTrusted;
                })
                .subscribe(userTrusted -> {
                    if (view != null) {
                        view.onTrustedUserAdded(userTrusted);
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

    private void listenTrustedUserChanged() {
        view.showLoading();
        final DataSnapshot[] temp = new DataSnapshot[1];
        RxFirebase.observeChildChanged(FirebaseApi.pluck().userTrusted(currentUser.getUserId()))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .doOnNext(dataSnapshot -> temp[0] = dataSnapshot)
                .flatMap(dataSnapshot -> RxFirebase.observeOnce(FirebaseApi.pluck().users(dataSnapshot.getKey()))
                        .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class)))
                .map(user -> {
                    UserTrusted userTrusted = new UserTrusted();
                    userTrusted.setUserTrustedId(temp[0].getKey());
                    userTrusted.setStatus(UserTrusted.Status.valueOf(temp[0].child("status").getValue().toString()));
                    userTrusted.setUser(user);
                    return userTrusted;
                })
                .subscribe(userTrusted -> {
                    if (view != null) {
                        view.onTrustedUserChanged(userTrusted);
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

    public void addTrustedUser(User user) {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("userTrusted/" + currentUser.getUserId() + "/" + user.getUserId() + "/status/", "MENUNGGU");
        data.put("trustedOf/" + user.getUserId() + "/" + currentUser.getUserId() + "/status/", "MENUNGGU");
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
                            UserTrusted userTrusted = new UserTrusted();
                            userTrusted.setUserTrustedId(user.getUserId());
                            userTrusted.setStatus(UserTrusted.Status.MENUNGGU);
                            userTrusted.setUser(user);
                            view.onTrustedUserAdded(userTrusted);
                            view.dismissLoading();
                        }
                    }
                });
    }

    public void removeTrustedUser(UserTrusted userTrusted) {
        view.showLoading();
        FirebaseApi.pluck()
                .userTrusted(currentUser.getUserId())
                .child(userTrusted.getUserTrustedId())
                .removeValue((firebaseError, firebase) -> {
                    if (firebaseError != null) {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError("Gagal menghapus data orang yang anda percaya!");
                            view.dismissLoading();
                        }
                    } else {
                        if (view != null) {
                            view.onTrustedUserRemoved(userTrusted);
                            view.dismissLoading();
                        }
                    }
                });

        FirebaseApi.pluck()
                .getApi()
                .child("trustedOf")
                .child(userTrusted.getUserTrustedId())
                .child(currentUser.getUserId())
                .removeValue();
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void onFoundUser(User user);

        void onNotFoundUser(String email);

        void onTrustedUserAdded(UserTrusted userTrusted);

        void onTrustedUserRemoved(UserTrusted userTrusted);

        void onTrustedUserChanged(UserTrusted userTrusted);
    }
}
