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

    @Bind(R.id.id_number) EditText idNumber;
    @Bind(R.id.name) EditText name;
    @Bind(R.id.email) EditText email;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.birthdate) EditText birthDate;
    @Bind(R.id.male) RadioButton male;

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
        String idNumber = this.idNumber.getText().toString();
        String name = this.name.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String birthDate = this.birthDate.getText().toString();

        if (idNumber.isEmpty()) {
            this.idNumber.setError("Please fill this form!");
        } else if (name.isEmpty()) {
            this.idNumber.setError("Please fill this form!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Please add valid email address!");
        } else if (password.length() < 6) {
            this.password.setError("Password at least 6 characters!");
        } else if (birthDate.isEmpty()) {
            this.birthDate.setError("Please fill this form!");
        } else {
            User user = new User.Builder()
                    .setIdNumber(idNumber)
                    .setName(name)
                    .setEmail(email)
                    .setBirthday(birthDate)
                    .setGender(male.isChecked() ? "M" : "F")
                    .setLocation(new Location(123, 120))
                    .setStatus("Online")
                    .build();

            registerController.register(user, password);
        }
    }

    @OnClick(R.id.login)
    public void login() {
        onBackPressed();
    }

    @Override
    public void onSuccessRegister(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFailedRegister(FirebaseError error) {
        Snackbar snackbar = Snackbar.make(male, error.getMessage(), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showError(BenihErrorEvent errorEvent) {

    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
