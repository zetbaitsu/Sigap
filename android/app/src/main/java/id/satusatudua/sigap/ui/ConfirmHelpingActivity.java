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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.ConfirmHelpingPresenter;
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
public class ConfirmHelpingActivity extends BenihActivity implements ConfirmHelpingPresenter.View {
    private static final String KEY_CASE_ID = "extra_case_id";

    @Bind(R.id.name) TextView reporterName;
    @Bind(R.id.address) TextView caseAddress;
    @Bind(R.id.map_capture) BenihImageView mapCapture;
    @Bind(R.id.root_content) LinearLayout content;
    @Bind(R.id.root_declined) LinearLayout declinedContent;

    private ConfirmHelpingPresenter presenter;
    private String caseId;
    private ProgressDialog progressDialog;
    private MediaPlayer phoneRingPlayer;
    private Vibrator vibrator;

    public static Intent generateIntent(Context context, String caseId) {
        Intent intent = new Intent(context, ConfirmHelpingActivity.class);
        intent.putExtra(KEY_CASE_ID, caseId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_confirm_helping;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        resolveCaseId(savedInstanceState);

        phoneRingPlayer = MediaPlayer.create(this, R.raw.sound_sirine);
        phoneRingPlayer.setLooping(true);
        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        startRingAndVibrate();

        presenter = new ConfirmHelpingPresenter(this);
        presenter.loadCaseData(caseId);
    }

    private void resolveCaseId(Bundle savedInstanceState) {
        caseId = getIntent().getStringExtra(KEY_CASE_ID);

        if (caseId == null && savedInstanceState != null) {
            caseId = savedInstanceState.getString(KEY_CASE_ID);
        }

        if (caseId == null) {
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

    @OnClick(R.id.button_close)
    public void close() {
        finish();
    }

    @Override
    public void showCaseData(Case theCase) {
        presenter.loadCaseAddress(theCase);
        presenter.loadReporterData(theCase);
        mapCapture.setImageUrl(MapUtils.getImageUrl(theCase.getLatitude(), theCase.getLongitude()));
    }

    @Override
    public void showCaseAddress(String address) {
        caseAddress.setText(address);
    }

    @Override
    public void showReporter(User reporter) {
        String text = "<b>" + reporter.getName().split(" ")[0] + "</b>";
        text = text + " membutuhkan pertolongan!";
        reporterName.setText(Html.fromHtml(text));
    }

    @Override
    public void onConfirmed(Case theCase, User reporter) {
        startActivity(HelpingActivity.generateIntent(this, theCase, reporter));
        stopRingAndVibrate();
    }

    @Override
    public void onDeclined() {
        content.setVisibility(View.GONE);
        declinedContent.setVisibility(View.VISIBLE);
        stopRingAndVibrate();
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

    public void startRingAndVibrate() {
        if (phoneRingPlayer != null) {
            phoneRingPlayer.start();
        }
        if (vibrator != null) {
            vibrator.vibrate(new long[]{500, 500}, 0);
        }
    }

    public void stopRingAndVibrate() {
        try {
            if (phoneRingPlayer != null) {
                phoneRingPlayer.stop();
                phoneRingPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingAndVibrate();
    }
}
