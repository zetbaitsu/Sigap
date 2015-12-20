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

import java.util.ArrayList;
import java.util.List;

import id.satusatudua.sigap.data.LocalDataManager;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.IteratorUtils;
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

public class UserPresenter extends BenihPresenter<UserPresenter.View> {

    private List<User> users;

    public UserPresenter(View view) {
        super(view);
        users = new ArrayList<>();
        listenUserAdded();
        listenUserChanged();
        listenUserRemoved();
    }

    public void loadUsers() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("users"))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(DataSnapshot::getChildren)
                .map(dataSnapshots -> IteratorUtils.toList(dataSnapshots, User.class))
                .map(users -> {
                    this.users = users;
                    return this.users;
                })
                .subscribe(users -> {
                    if (view != null) {
                        view.showUsers(users);
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

    public void loadUser(String uid) {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("users").child(uid))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
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

    private void listenUserAdded() {
        RxFirebase.observeChildAdded(FirebaseApi.pluck().getApi().child("users"))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .map(user -> {
                    if (users.contains(user)) {
                        return null;
                    }
                    users.add(user);
                    return user;
                })
                .subscribe(user -> {
                    if (user != null && view != null) {
                        view.onUserAdded(user);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null && users != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    private void listenUserChanged() {
        RxFirebase.observeChildChanged(FirebaseApi.pluck().getApi().child("users"))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .map(user -> {
                    int i = users.indexOf(user);
                    if (i >= 0) {
                        users.set(i, user);
                    } else {
                        users.add(user);
                    }
                    return user;
                })
                .subscribe(user -> {
                    if (user.equals(LocalDataManager.getCurrentUser())) {
                        LocalDataManager.saveCurrentUser(user);
                    }
                    if (view != null) {
                        view.onUserChanged(user);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    private void listenUserRemoved() {
        RxFirebase.observeChildRemoved(FirebaseApi.pluck().getApi().child("users"))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .map(user -> {
                    users.remove(user);
                    return user;
                })
                .subscribe(user -> {
                    if (view != null) {
                        view.onUserRemoved(user);
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
        Timber.d("saveState " + getClass().getSimpleName());
        bundle.putParcelableArrayList("users", (ArrayList<User>) users);
    }

    @Override
    public void loadState(Bundle bundle) {
        Timber.d("loadState " + getClass().getSimpleName());
        users = bundle.getParcelableArrayList("users");
        if (users != null) {
            view.showUsers(users);
        } else {
            Timber.d("failed loadState users in " + getClass().getSimpleName());
            loadUsers();
        }
    }

    public interface View extends BenihPresenter.View {
        void showUsers(List<User> users);

        void showUser(User user);

        void onUserAdded(User user);

        void onUserChanged(User user);

        void onUserRemoved(User user);
    }
}
