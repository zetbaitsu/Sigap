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
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : January 19, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EditProfilePresenter extends BenihPresenter<EditProfilePresenter.View> {

    private User currentUser;

    public EditProfilePresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void updateProfile(String name, String phoneNumber, boolean isMale) {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("phoneNumber", phoneNumber);
        data.put("male", isMale);

        FirebaseApi.pluck().users(currentUser.getUserId())
                .updateChildren(data, (firebaseError, firebase) -> {
                    if (firebaseError == null) {
                        currentUser.setName(name);
                        currentUser.setMale(isMale);
                        currentUser.setPhoneNumber(phoneNumber);
                        CacheManager.pluck().cacheCurrentUser(currentUser);

                        if (view != null) {
                            view.onProfileUpdated();
                            view.dismissLoading();
                        }
                    } else {
                        Timber.e(firebaseError.getMessage());
                        if (view != null) {
                            view.showError("Gagal memperbaharui data profil anda!");
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
        void onProfileUpdated();
    }
}
