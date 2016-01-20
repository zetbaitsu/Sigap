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

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.presenter.AddContactPresenter;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.util.KeyboardUtil;

/**
 * Created on : January 19, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class AddContactActivity extends BenihActivity implements AddContactPresenter.View {

    @Bind(R.id.name) EditText name;
    @Bind(R.id.phone) EditText phone;
    @Bind(R.id.address) EditText address;

    private AddContactPresenter addContactPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_add_contact;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        addContactPresenter = new AddContactPresenter(this);
    }

    @OnClick(R.id.button_save)
    public void save() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Mohon isi form ini!");
        } else if (phone.getText().toString().isEmpty()) {
            phone.setError("Mohon isi form ini!");
        } else if (name.getText().toString().length() < 5) {
            name.setError("Nama kontak minimal 5 karakter!");
        } else if (!Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
            phone.setError("Mohon isi dengan no telepon yang valid!");
        } else if (address.getText().toString().isEmpty()) {
            address.setError("Mohon isi form ini!");
        } else if (address.getText().toString().length() < 24) {
            address.setError("Mohon isi dengan alamat yang lengkap!");
        } else {
            addContactPresenter.addContact(name.getText().toString(), phone.getText().toString(), address.getText().toString());
            KeyboardUtil.hideKeyboard(this, address);
        }
    }

    @Override
    public void onContactAdded() {
        finish();
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(address, errorMessage, Snackbar.LENGTH_LONG);
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
