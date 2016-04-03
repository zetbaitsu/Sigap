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
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.presenter.FeedbackCasePresenter;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : January 15, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class FeedbackCaseActivity extends BenihActivity implements FeedbackCasePresenter.View {
    private static final String KEY_CASE = "extra_case";

    @Bind(R.id.feedback) EditText feedbackField;

    private Case theCase;
    private FeedbackCasePresenter presenter;
    private ProgressDialog progressDialog;

    public static Intent generateIntent(Context context, Case theCase) {
        Intent intent = new Intent(context, FeedbackCaseActivity.class);
        intent.putExtra(KEY_CASE, theCase);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_feedback_case;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveTheCase(savedInstanceState);

        presenter = new FeedbackCasePresenter(this, theCase);
    }

    private void resolveTheCase(Bundle savedInstanceState) {
        theCase = getIntent().getParcelableExtra(KEY_CASE);

        if (theCase == null && savedInstanceState != null) {
            theCase = savedInstanceState.getParcelable(KEY_CASE);
        }

        if (theCase == null) {
            Intent intent = new Intent(this, TombolActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.button_save)
    public void save() {
        if (!feedbackField.getText().toString().isEmpty()) {
            presenter.sendFeedBack(feedbackField.getText().toString());
        } else {
            feedbackField.setError("Mohon isi form ini terlebih dahulu");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CASE, theCase);
    }

    @Override
    public void onCaseClosed() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(feedbackField, errorMessage, Snackbar.LENGTH_LONG);
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
