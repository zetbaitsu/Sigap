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
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : December 27, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class FindHelperPresenter extends BenihPresenter<FindHelperPresenter.View> {

    public FindHelperPresenter(View view) {
        super(view);
    }

    private void findHelper(String caseId) {
        List<UserLocation> nearbyUsers = CacheManager.pluck().getNearbyUsers();
        if (nearbyUsers == null) {
            view.showError("Tidak dapat menemukan pengguna lain disekitar anda.");
            view.dismissLoading();
        } else {
            Observable.from(nearbyUsers)
                    .map(userLocation -> RxFirebase.observeOnce(FirebaseApi.pluck().users(userLocation.getUserId()))
                            .map(dataSnapshot -> dataSnapshot.getValue(User.class)))
                    .flatMap(userObservable -> userObservable)
                    .filter(user -> user.getStatus() == User.Status.SIAP)
                    .map(user -> {
                        Map<String, String> data = new HashMap<>();
                        data.put("userId", user.getUserId());
                        FirebaseApi.pluck().helperCases(caseId).push().setValue(data);

                        data.clear();
                        data.put("caseId", caseId);
                        FirebaseApi.pluck().userHelps(user.getUserId()).push().setValue(data);

                        return user;
                    })
                    .subscribe(user -> {
                        if (view != null) {
                            view.onHelperFound(user);
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
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void onHelperFound(User user);
    }
}
