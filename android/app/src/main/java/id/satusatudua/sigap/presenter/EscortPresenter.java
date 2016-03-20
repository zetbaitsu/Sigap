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

import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : March 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EscortPresenter extends BenihPresenter<EscortPresenter.View> {

    private User currentUser;

    public EscortPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void finishEscort(Escort escort) {
        view.showLoading();
        Firebase api = FirebaseApi.pluck().getApi();

        Map<String, Object> data = new HashMap<>();
        data.put("escorts/" + escort.getEscortId() + "/closed/", true);
        data.put("users/" + currentUser.getUserId() + "/status/", "SIAP");

        api.updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Maaf kami gagal memproses pemintaan anda!");
                    view.dismissLoading();
                }
            } else {
                currentUser.setStatus(User.Status.SIAP);
                CacheManager.pluck().cacheCurrentUser(currentUser);
                StateManager.pluck().setState(StateManager.State.LOGGED);

                if (view != null) {
                    view.onEscortFinished();
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
        void onEscortFinished();
    }
}
