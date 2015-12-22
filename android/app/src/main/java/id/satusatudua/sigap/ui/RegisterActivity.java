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
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.RadioButton;

import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.RegisterPresenter;
import id.satusatudua.sigap.util.TimeUtils;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class RegisterActivity extends BenihActivity implements RegisterPresenter.View {

    @Bind(R.id.nama) EditText nama;
    @Bind(R.id.email) EditText email;
    @Bind(R.id.tgl_lahir) EditText tanggalLahir;
    @Bind(R.id.laki) RadioButton laki;
    @Bind(R.id.perempuan) RadioButton perempuan;

    private RegisterPresenter registerPresenter;
    private ProgressDialog progressDialog;
    private DatePickerDialog datePickerDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        registerPresenter = new RegisterPresenter(this);
        laki.setOnCheckedChangeListener((buttonView, isChecked) -> {
            laki.setTextColor(ContextCompat.getColor(this, isChecked ? R.color.primary_text : R.color.secondary_text));
            perempuan.setTextColor(ContextCompat.getColor(this, !isChecked ? R.color.primary_text : R.color.secondary_text));
        });

        datePickerDialog =
                new DatePickerDialog(this,
                                     R.style.DatePickerStyle,
                                     (view, year, monthOfYear, dayOfMonth)
                                             -> tanggalLahir.setText(dayOfMonth + "-" + monthOfYear + "-" + year),
                                     1980, 1, 1);
    }

    @OnClick(R.id.date_picker)
    public void showDatePicker() {
        datePickerDialog.show();
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
            user.setBirthDate(TimeUtils.getBirthDate(tanggalLahir));
            user.setMale(laki.isChecked());
            user.setLocation(new Location(123, 120));
            user.setFromApps(true);
            registerPresenter.register(user);
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
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setMessage("Kami telah mengirimkan password akun anda ke email yang anda daftarkan tadi, masukan password tersebut ke form selanjutnya untuk memverikasi alamat email anda.")
                .setPositiveButton("OK", (dialog, which) -> {
                    sendBroadcast(new Intent("id.satusatudua.sigap.ACTION_START"));
                    Intent intent = new Intent(this, VerificationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onFailedRegister(FirebaseError error) {
        Snackbar snackbar = Snackbar.make(laki, error.getMessage(), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showError(String errorMessage) {

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
