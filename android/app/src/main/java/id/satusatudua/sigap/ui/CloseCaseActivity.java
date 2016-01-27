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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.presenter.CloseCasePresenter;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : January 15, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class CloseCaseActivity extends BenihActivity implements CloseCasePresenter.View {
    private static final String KEY_CASE = "extra_case";
    private static final String KEY_HELPERS = "extra_helpers";

    @Bind(R.id.kronologi) EditText chronologyField;

    private Case theCase;
    private List<CandidateHelper> helpers;
    private CloseCasePresenter presenter;
    private ProgressDialog progressDialog;

    public static Intent generateIntent(Context context, Case theCase, List<CandidateHelper> helpers) {
        Intent intent = new Intent(context, CloseCaseActivity.class);
        intent.putExtra(KEY_CASE, theCase);
        intent.putParcelableArrayListExtra(KEY_HELPERS, (ArrayList<CandidateHelper>) helpers);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_close_case;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveTheCase(savedInstanceState);
        resolveHelpers(savedInstanceState);

        presenter = new CloseCasePresenter(this, theCase, helpers);
    }

    private void resolveTheCase(Bundle savedInstanceState) {
        theCase = getIntent().getParcelableExtra(KEY_CASE);

        if (theCase == null && savedInstanceState != null) {
            theCase = savedInstanceState.getParcelable(KEY_CASE);
        }

        if (theCase == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void resolveHelpers(Bundle savedInstanceState) {
        helpers = getIntent().getParcelableArrayListExtra(KEY_HELPERS);

        if (helpers == null && savedInstanceState != null) {
            helpers = savedInstanceState.getParcelableArrayList(KEY_HELPERS);
        }

        if (helpers == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.button_save)
    public void save() {
        if (!chronologyField.getText().toString().isEmpty()) {
            presenter.sendChronology(chronologyField.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CASE, theCase);
        outState.putParcelableArrayList(KEY_HELPERS, (ArrayList<CandidateHelper>) helpers);
    }

    @Override
    public void onCaseClosed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(chronologyField, errorMessage, Snackbar.LENGTH_LONG);
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
