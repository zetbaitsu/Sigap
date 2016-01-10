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

    @Bind(R.id.ripple) RippleBackground rippleBackground;

    private ProgressDialog progressDialog;
    private TombolPresenter tombolPresenter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_tombol;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        rippleBackground.startRippleAnimation();
        tombolPresenter = new TombolPresenter(this);
    }

    @OnClick(R.id.button_emergency)
    public void onEmergency() {
        tombolPresenter.createCase();
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
