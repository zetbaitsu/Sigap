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

import java.util.List;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : December 25, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class NearbyPresenter extends BenihPresenter<NearbyPresenter.View> {

    public NearbyPresenter(View view) {
        super(view);
    }

    public void loadNearbyUsers() {
        view.showLoading();
        List<UserLocation> nearbyUsers = CacheManager.pluck().getNearbyUsers();
        if (nearbyUsers == null) {
            view.showError("Tidak dapat menemukan pengguna lain disekitar anda.");
            view.dismissLoading();
        } else {
            Observable.from(nearbyUsers)
                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                    .map(user -> RxFirebase.observeOnce(FirebaseApi.pluck().users(user.getUserId()))
                            .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                            .map(dataSnapshot -> dataSnapshot.getValue(User.class)))
                    .flatMap(userObservable -> userObservable)
                    .toList()
                    .subscribe(users -> {
                        if (view != null) {
                            view.showNearbyUsers(users);
                            view.dismissLoading();
                        }
                    }, throwable -> {
                        Timber.e(throwable.getMessage());
                        if (view != null) {
                            view.showError(throwable.getMessage());
                        }
                    });
        }
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void showNearbyUsers(List<User> nearbyUsers);
    }
}
