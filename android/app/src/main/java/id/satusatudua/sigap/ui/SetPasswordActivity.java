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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.SetPasswordPresenter;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class SetPasswordActivity extends BenihActivity implements SetPasswordPresenter.View {

    @Bind(R.id.password1) EditText password1;
    @Bind(R.id.password2) EditText password2;

    private SetPasswordPresenter setPasswordPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_set_password;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        setPasswordPresenter = new SetPasswordPresenter(this);
    }

    @OnClick(R.id.simpan)
    public void save() {
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();

        if (pass1.isEmpty()) {
            password1.setError("Mohon isi form ini!");
        } else if (pass2.isEmpty()) {
            password2.setError("Mohon isi form ini!");
        } else if (pass1.length() < 8) {
            password1.setError("Kata sandi harus minimal 8 karakter!");
        } else if (!pass1.equals(pass2)) {
            password1.setError("Kata sandi tidak sama");
            password2.setError("Kata sandi tidak sama");
        } else {
            setPasswordPresenter.updatePassword(pass1);
        }
    }

    @Override
    public void onPasswordUpdated(User currentUser) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setMessage("Sebelum anda dapat menggunakan fitur Sigap, anda harus memasukan minimal 3 pengguna lain yang anda percayai.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, AddTrustedUserActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(password2, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
