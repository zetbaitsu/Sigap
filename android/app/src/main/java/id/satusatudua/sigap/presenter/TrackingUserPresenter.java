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

import com.google.android.gms.maps.model.LatLng;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : March 23, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TrackingUserPresenter extends BenihPresenter<TrackingUserPresenter.View> {

    public TrackingUserPresenter(View view) {
        super(view);
    }

    public void loadAndTrackUserLocation(User user) {
        RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("userLocations").child(user.getUserId()))
                .map(dataSnapshot -> dataSnapshot.child("l"))
                .subscribe(dataSnapshot -> {
                    view.onUserMoved(new LatLng(dataSnapshot.child("0").getValue(Double.class),
                                                dataSnapshot.child("1").getValue(Double.class)));
                    track(user);
                }, Throwable::printStackTrace);
    }

    public void track(User user) {
        RxFirebase.observeChildChanged(FirebaseApi.pluck().getApi().child("userLocations").child(user.getUserId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .filter(dataSnapshot -> dataSnapshot.getKey().equals("l"))
                .subscribe(dataSnapshot -> {
                    view.onUserMoved(new LatLng(dataSnapshot.child("0").getValue(Double.class),
                                                dataSnapshot.child("1").getValue(Double.class)));
                }, Throwable::printStackTrace);
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void onUserMoved(LatLng latLng);
    }
}
