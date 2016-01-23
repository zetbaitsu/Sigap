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
import android.os.Handler;
import android.support.design.widget.Snackbar;

import com.skyfishjy.library.RippleBackground;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.presenter.TombolPresenter;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : December 24, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TombolActivity extends BenihActivity implements TombolPresenter.View {
    private static final String KEY_STATUS_BAR = "extra_status_bar";

    @Bind(R.id.ripple) RippleBackground rippleBackground;

    private ProgressDialog progressDialog;
    private TombolPresenter tombolPresenter;
    private boolean doublePressedOnce = false;

    public static Intent generateIntent(Context context, boolean fromStatusBar) {
        Intent intent = new Intent(context, TombolActivity.class);
        intent.putExtra(KEY_STATUS_BAR, fromStatusBar);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_tombol;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        boolean fromStatusBar = getIntent().getBooleanExtra(KEY_STATUS_BAR, false);
        rippleBackground.startRippleAnimation();
        tombolPresenter = new TombolPresenter(this);

        if (fromStatusBar) {
            tombolPresenter.createCase();
        }
    }

    @OnClick(R.id.button_emergency)
    public void onEmergency() {
        if (doublePressedOnce) {
            tombolPresenter.createCase();
            return;
        }
        doublePressedOnce = true;
        showError("Tekan sekali lagi untuk meminta bantuan!");
        new Handler().postDelayed(() -> doublePressedOnce = false, 2000);
    }

    @OnClick(R.id.button_main)
    public void startMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(rippleBackground, errorMessage, Snackbar.LENGTH_LONG);
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

    @Override
    public void onCaseCreated(String caseId) {
        Intent intent = new Intent(this, EmergencyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
