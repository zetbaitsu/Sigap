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

package id.satusatudua.sigap.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.RadioButton;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.EditProfilePresenter;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : January 18, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EditProfileActivity extends BenihActivity implements EditProfilePresenter.View {

    @Bind(R.id.name) EditText name;
    @Bind(R.id.phone) EditText phoneNumber;
    @Bind(R.id.laki) RadioButton laki;
    @Bind(R.id.perempuan) RadioButton perempuan;

    private EditProfilePresenter editProfilePresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        User currentUser = CacheManager.pluck().getCurrentUser();

        name.setText(currentUser.getName());
        phoneNumber.setText(currentUser.getPhoneNumber());
        if (currentUser.isMale()) {
            laki.setChecked(true);
            perempuan.setChecked(false);
        } else {
            perempuan.setChecked(true);
            laki.setChecked(false);
        }

        editProfilePresenter = new EditProfilePresenter(this);
    }

    @OnClick(R.id.button_save)
    public void saveProfile() {
        String nama = name.getText().toString();
        String phoneNumber = this.phoneNumber.getText().toString();

        if (nama.isEmpty()) {
            name.setError("Mohon masukan nama anda!");
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            this.phoneNumber.setError("Mohon masukan no ponsel yang valid!");
        } else {
            editProfilePresenter.updateProfile(nama, phoneNumber, laki.isChecked());
        }
    }

    @Override
    public void onProfileUpdated() {
        finish();
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(phoneNumber, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Silahkan tunggu....");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
