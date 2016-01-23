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
import android.util.Patterns;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.presenter.LoginPresenter;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LoginActivity extends BenihActivity implements LoginPresenter.View {

    @Bind(R.id.email) EditText email;
    @Bind(R.id.password) EditText password;

    private LoginPresenter loginPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        loginPresenter = new LoginPresenter(this);
    }

    @OnClick(R.id.login)
    public void login() {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Mohon masukan alamat email yang valid!");
        } else if (password.length() <= 0) {
            this.password.setError("Mohon masukan password anda disini!");
        } else {
            loginPresenter.login(email, password);
        }
    }

    @OnClick(R.id.forgot_password)
    public void forgotPassword() {
        String email = this.email.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Mohon masukan alamat email yang valid terlebih dahulu!");
        } else {
            showLoading();
            FirebaseApi.pluck().getApi().resetPassword(email, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    dismissLoading();
                    new AlertDialog.Builder(LoginActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setMessage("Kami telah mengirimkan sebuah kode ke email yang anda masukan tadi, masukan kode tersebut ke form selanjutnya untuk mengubah kata sandi anda.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                StateManager.pluck().setState(StateManager.State.ENTER_CODE);
                                StateManager.pluck().setLastEmail(email);
                                Intent intent = new Intent(LoginActivity.this, EnterCodeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            })
                            .show()
                            .getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    dismissLoading();
                    Snackbar snackbar = Snackbar.make(password, firebaseError.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundResource(R.color.colorAccent);
                    snackbar.show();
                }
            });
        }
    }

    @OnClick(R.id.register)
    public void register() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void onSuccessLogin(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(password, errorMessage, Snackbar.LENGTH_LONG);
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
