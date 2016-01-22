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
import com.firebase.client.FirebaseError;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.PasswordUtils;
import id.zelory.benih.presenter.BenihPresenter;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class UpdatePasswordPresenter extends BenihPresenter<UpdatePasswordPresenter.View> {

    public UpdatePasswordPresenter(View view) {
        super(view);
    }

    public void updatePassword(String newPassword) {
        view.showLoading();
        User currentUser = CacheManager.pluck().getCurrentUser();
        FirebaseApi.pluck()
                .getApi()
                .changePassword(currentUser.getEmail(),
                                StateManager.pluck().getToken(),
                                PasswordUtils.hashPassword(newPassword),
                                new Firebase.ResultHandler() {
                                    @Override
                                    public void onSuccess() {
                                        StateManager.pluck().setState(StateManager.State.LOGGED);
                                        if (view != null) {
                                            view.onPasswordUpdated(currentUser);
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
        void onPasswordUpdated(User currentUser);
    }
}
