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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.presenter.EditContactPresenter;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.util.KeyboardUtil;

/**
 * Created on : January 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EditContactActivity extends BenihActivity implements EditContactPresenter.View {
    public static final String KEY_CONTACT = "extra_contact";

    @Bind(R.id.name) EditText name;
    @Bind(R.id.phone) EditText phone;
    @Bind(R.id.address) EditText address;

    private ImportantContact contact;
    private ProgressDialog progressDialog;
    private EditContactPresenter presenter;

    public static Intent generateIntent(Context context, ImportantContact contact) {
        Intent intent = new Intent(context, EditContactActivity.class);
        intent.putExtra(KEY_CONTACT, contact);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_edit_contact;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveContact(savedInstanceState);

        name.setText(contact.getName());
        phone.setText(contact.getPhoneNumber());
        address.setText(contact.getAddress());

        presenter = new EditContactPresenter(this);
    }

    private void resolveContact(Bundle savedInstanceState) {
        contact = getIntent().getParcelableExtra(KEY_CONTACT);

        if (contact == null && savedInstanceState != null) {
            contact = savedInstanceState.getParcelable(KEY_CONTACT);
        }

        if (contact == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
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
            presenter.editContact(contact, name.getText().toString(), phone.getText().toString(), address.getText().toString());
            contact.setName(name.getText().toString());
            contact.setPhoneNumber(phone.getText().toString());
            contact.setAddress(address.getText().toString());
            KeyboardUtil.hideKeyboard(this, address);
        }
    }

    @Override
    public void onContactEdited() {
        Intent data = new Intent();
        data.putExtra(KEY_CONTACT, contact);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(address, errorMessage, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
