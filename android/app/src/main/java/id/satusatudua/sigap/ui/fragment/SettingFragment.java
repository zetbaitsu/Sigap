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

package id.satusatudua.sigap.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.CurrentUserPresenter;
import id.satusatudua.sigap.ui.CalibrateActivity;
import id.satusatudua.sigap.ui.LoginActivity;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.util.KeyboardUtil;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class SettingFragment extends BenihFragment implements CurrentUserPresenter.View {

    @Bind(R.id.vibrate) CheckBox vibrate;
    @Bind(R.id.nearby) CheckBox nearby;
    @Bind(R.id.status_bar) CheckBox statusBar;
    @Bind(R.id.shake) CheckBox shake;
    @Bind(R.id.calibrate_title) TextView calibrateTitle;

    private CurrentUserPresenter currentUserPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {

        vibrate.setChecked(CacheManager.pluck().isVibrate());
        vibrate.setOnCheckedChangeListener((buttonView, isChecked) -> CacheManager.pluck().setVibrate(isChecked));

        nearby.setChecked(CacheManager.pluck().isNotifyNearby());
        nearby.setOnCheckedChangeListener((buttonView, isChecked) -> CacheManager.pluck().setNotifyNearby(isChecked));

        statusBar.setChecked(CacheManager.pluck().isShowOnStatusBar());
        statusBar.setOnCheckedChangeListener((buttonView, isChecked) -> CacheManager.pluck().setShowOnStatusBar(isChecked));

        shake.setChecked(CacheManager.pluck().isShakeToNotify());
        shake.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CacheManager.pluck().setShakeToNotify(isChecked);
            if (isChecked) {
                calibrateTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
            } else {
                calibrateTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
            }
        });

        if (CacheManager.pluck().isShakeToNotify()) {
            calibrateTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        } else {
            calibrateTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        }

        currentUserPresenter = new CurrentUserPresenter(this);
    }

    @OnClick(R.id.ringtone)
    public void chooseRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Pilih Nada Dering");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, CacheManager.pluck().getRingtone() == null ? null :
                Uri.parse(CacheManager.pluck().getRingtone()));
        startActivityForResult(intent, 5);
    }

    @OnClick(R.id.calibrate)
    public void calibrate() {
        startActivity(new Intent(getActivity(), CalibrateActivity.class));
    }

    @OnClick(R.id.feedback)
    public void onFeedbackClick() {
        EditText editText = new EditText(getActivity());
        editText.setHint("Feedback");
        editText.setInputType(InputType.TYPE_CLASS_TEXT
                                      | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Kirim Feedback")
                .setView(editText)
                .setPositiveButton("Kirim", (dialog, which) -> {
                    if (!editText.getText().toString().isEmpty()) {
                        sendFeedBack(editText.getText().toString());
                        KeyboardUtil.hideKeyboard(getActivity(), editText);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Batal", (dialog, which1) -> {
                    dialog.dismiss();
                })
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        alertDialog.show();
    }

    private void sendFeedBack(String feedback) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:zetra@mail.ugm.ac.id"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sigap Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, feedback + "\n\nDikirim memalului aplikasi Sigap.");
        startActivity(intent);

    }

    @OnClick(R.id.rate)
    public void onRateClick() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                                 Uri.parse("http://play.google.com/store/apps/details?id=id.zelory.codepolitan")));
    }

    @OnClick(R.id.about)
    public void onAboutDeveloperClick() {

    }

    @OnClick(R.id.logout)
    public void logout() {
        currentUserPresenter.logout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                CacheManager.pluck().setRingtone(uri.toString());
            }
        }
    }

    @Override
    public void onCurrentUserChanged(User currentUser) {

    }

    @Override
    public void onSuccessLogout() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(shake, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Silahkan tunggu....");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
