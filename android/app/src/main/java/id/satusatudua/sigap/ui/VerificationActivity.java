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
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.VerificationPresenter;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class VerificationActivity extends BenihActivity implements VerificationPresenter.View {

    @Bind(R.id.token) EditText token;

    private VerificationPresenter verificationPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_verification;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        verificationPresenter = new VerificationPresenter(this);
    }

    @OnClick(R.id.resend)
    public void resendCode() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Kirim ulang kode ke email anda?")
                .setPositiveButton("YA", (dialog, which) -> {
                    verificationPresenter.resendCode();
                    dialog.dismiss();
                })
                .setNegativeButton("TIDAK", (dialog, which1) -> {
                    dialog.dismiss();
                })
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        alertDialog.show();
    }

    @OnClick(R.id.submit)
    public void submit() {
        if (token.getText().toString().isEmpty()) {
            token.setError("Mohon isi form ini!");
        } else {
            verificationPresenter.verify(token.getText().toString());
        }
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(token, errorMessage, Snackbar.LENGTH_LONG);
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
            progressDialog.setMessage("Silahkan tunggu...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void onSuccessVerification(User user) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setMessage("Kode berhasil diverifikasi, Selanjutnya silahkan atur kata sandi anda pada halaman berikutnya.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, SetPasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onSuccessResendCode() {
        try {
            Snackbar snackbar = Snackbar.make(token, "Kode sudah kami kirim ke email anda!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
            snackbar.show();
        } catch (Exception e) {

        }
    }
}
