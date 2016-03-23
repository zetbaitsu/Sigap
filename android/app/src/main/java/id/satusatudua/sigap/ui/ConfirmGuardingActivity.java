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
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.ConfirmGuardingPresenter;
import id.satusatudua.sigap.util.MapUtils;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.view.BenihImageView;

/**
 * Created on : January 10, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ConfirmGuardingActivity extends BenihActivity implements ConfirmGuardingPresenter.View {
    private static final String KEY_ESCORT_ID = "extra_escort_id";

    @Bind(R.id.name) TextView reporterName;
    @Bind(R.id.address) TextView destinationAddress;
    @Bind(R.id.map_capture) BenihImageView mapCapture;

    private ConfirmGuardingPresenter presenter;
    private String escortId;
    private ProgressDialog progressDialog;

    public static Intent generateIntent(Context context, String escortId) {
        Intent intent = new Intent(context, ConfirmGuardingActivity.class);
        intent.putExtra(KEY_ESCORT_ID, escortId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_confirm_guarding;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        resolveCaseId(savedInstanceState);

        presenter = new ConfirmGuardingPresenter(this);
        presenter.loadEscortData(escortId);
    }

    private void resolveCaseId(Bundle savedInstanceState) {
        escortId = getIntent().getStringExtra(KEY_ESCORT_ID);

        if (escortId == null && savedInstanceState != null) {
            escortId = savedInstanceState.getString(KEY_ESCORT_ID);
        }

        if (escortId == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.button_confirm)
    public void confirm() {
        presenter.confirm();
    }

    @OnClick(R.id.button_decline)
    public void decline() {
        presenter.decline();
    }

    @Override
    public void showEscortData(Escort escort) {
        presenter.loadReporterData(escort);
        destinationAddress.setText(escort.getDestination());
        mapCapture.setImageUrl(MapUtils.getImageUrl(escort.getLatitude(), escort.getLongitude()));
    }

    @Override
    public void showReporter(User reporter) {
        reporterName.setText("Apakah Anda bersedia untuk mengawal perjalanan " + reporter.getName().split(" ")[0] + "?");
    }

    @Override
    public void onConfirmed(Escort escort, User reporter) {
        //startActivity(HelpingActivity.generateIntent(this, theCase, reporter));
    }

    @Override
    public void onDeclined() {
        finish();
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(new View(this), errorMessage, Snackbar.LENGTH_LONG);
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
}
