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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.controller.LoginController;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.BenihActivity;
import id.zelory.benih.controller.event.BenihErrorEvent;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LoginActivity extends BenihActivity implements LoginController.Presenter {

    @Bind(R.id.email) EditText email;
    @Bind(R.id.password) EditText password;

    private LoginController loginController;
    private ProgressDialog progressDialog;

    @Override
    protected int getActivityView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        loginController = new LoginController(this);
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
            loginController.login(email, password);
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
                    Snackbar snackbar = Snackbar.make(password, "Sebuah email telah dikirimkan untuk mengatur ulang kata sandi.", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
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
    public void onFailedLogin(FirebaseError error) {
        Snackbar snackbar = Snackbar.make(password, error.getMessage(), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showError(BenihErrorEvent errorEvent) {

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
