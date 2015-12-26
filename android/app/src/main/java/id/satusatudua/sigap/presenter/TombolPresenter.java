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

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TombolPresenter extends BenihPresenter<TombolPresenter.View> {

    public TombolPresenter(TombolPresenter.View view) {
        super(view);
    }

    public void createCase() {
        view.showLoading();
        User currentUser = CacheManager.pluck().getCurrentUser();
        Case theCase = new Case();
        theCase.setUserId(currentUser.getUserId());
        theCase.setDate(new Date());
        theCase.setOpen(true);
        Firebase newCase = FirebaseApi.pluck().cases().push();
        newCase.setValue(theCase, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError(firebaseError.getMessage());
                    view.dismissLoading();
                }
            } else {
                theCase.setCaseId(newCase.getKey());
                createCaseLocation(newCase.getKey());
                changeUserStatus(currentUser);
                createUserCase(currentUser.getUserId(), theCase.getCaseId());
                createChatRoom(theCase.getCaseId(), currentUser);

                if (view != null) {
                    view.onCaseCreated(theCase);
                }
            }
        });
    }

    private void changeUserStatus(User currentUser) {
        currentUser.setStatus(User.Status.BAHAYA);
        FirebaseApi.pluck().users(currentUser.getUserId()).setValue(currentUser);
        CacheManager.pluck().cacheCurrentUser(currentUser);
    }

    private void createCaseLocation(String caseId) {
        Location currentLocation = CacheManager.pluck().getUserLocation();
        FirebaseApi.pluck()
                .caseLocations()
                .setLocation(caseId,
                             new GeoLocation(currentLocation.getLatitude(),
                                             currentLocation.getLongitude()));
    }

    private void createUserCase(String userId, String caseId) {
        Map<String, String> newCase = new HashMap<>();
        newCase.put("caseId", caseId);
        FirebaseApi.pluck().userCases(userId).push().setValue(newCase);
    }

    private void createChatRoom(String caseId, User currentUser) {
        Message message = new Message();
        message.setDate(new Date());
        message.setUserId("0");
        message.setContent(currentUser.getName() + " sedang dalam bahaya, segara bantu dia kawan!");
        FirebaseApi.pluck().caseMessages(caseId).push().setValue(message, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError(firebaseError.getMessage());
                    view.dismissLoading();
                }
            } else {
                findHelper(caseId);
                if (view != null) {
                    view.onChatRoomCreated(caseId);

                }
            }
        });
    }

    private void findHelper(String caseId) {
        List<UserLocation> nearbyUsers = CacheManager.pluck().getNearbyUsers();
        if (nearbyUsers == null) {
            view.showError("Tidak dapat menemukan pengguna lain disekitar anda.");
            view.dismissLoading();
        } else {
            Observable.from(nearbyUsers)
                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                    .map(userLocation -> RxFirebase.observeOnce(FirebaseApi.pluck().users(userLocation.getUserId()))
                            .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
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
        void onCaseCreated(Case theCase);

        void onChatRoomCreated(String caseId);

        void onHelperFound(User user);
    }
}
