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
import android.widget.RadioButton;

import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.controller.RegisterController;
import id.satusatudua.sigap.data.model.Location;
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
public class RegisterActivity extends BenihActivity implements RegisterController.Presenter {

    @Bind(R.id.nama) EditText nama;
    @Bind(R.id.email) EditText email;
    @Bind(R.id.tgl_lahir) EditText tanggalLahir;
    @Bind(R.id.laki) RadioButton laki;

    private RegisterController registerController;
    private ProgressDialog progressDialog;

    @Override
    protected int getActivityView() {
        return R.layout.activity_register;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        registerController = new RegisterController(this);
    }

    @OnClick(R.id.register)
    public void register() {
        String nama = this.nama.getText().toString();
        String email = this.email.getText().toString();
        String tanggalLahir = this.tanggalLahir.getText().toString();


        if (nama.isEmpty()) {
            this.nama.setError("Mohon masukan nama anda!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Mohon masukan alamat email yang valid!");
        } else if (tanggalLahir.isEmpty()) {
            this.tanggalLahir.setError("Mohon masukan tanggal lahir anda!");
        } else {
            User user = new User();
            user.setName(nama);
            user.setEmail(email);
            user.setBirthDate(tanggalLahir);
            user.setMale(laki.isChecked());
            user.setLocation(new Location(123, 120));
            user.setFromApps(true);

            registerController.register(user, "adadsadasda");
        }
    }

    @OnClick(R.id.login)
    public void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onSuccessRegister(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFailedRegister(FirebaseError error) {
        Snackbar snackbar = Snackbar.make(laki, error.getMessage(), Snackbar.LENGTH_LONG);
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
