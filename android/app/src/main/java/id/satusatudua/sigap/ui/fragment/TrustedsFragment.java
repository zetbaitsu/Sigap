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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.event.AcceptTrustMeClick;
import id.satusatudua.sigap.event.DeclineTrustMeClick;
import id.satusatudua.sigap.event.RemoveTrustedUserClick;
import id.satusatudua.sigap.presenter.TrustMePresenter;
import id.satusatudua.sigap.presenter.TrustedUserPresenter;
import id.satusatudua.sigap.ui.adapter.TrustMeAdapter;
import id.satusatudua.sigap.ui.adapter.TrustedUserAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihBus;
import id.zelory.benih.util.KeyboardUtil;
import timber.log.Timber;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TrustedsFragment extends BenihFragment implements TrustedUserPresenter.View,
        TrustMePresenter.View {

    @Bind(R.id.trusted) TextView trusted;
    @Bind(R.id.trust_me) TextView trustMe;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.trusted_count) TextView trustedCounter;
    @Bind(R.id.trust_me_count) TextView trustMeCount;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;
    @Bind(R.id.fab) FloatingActionButton fab;

    private TrustedUserPresenter trustedUserPresenter;
    private TrustMePresenter trustMePresenter;
    private ProgressDialog progressDialog;
    private TrustedUserAdapter trustedUserAdapter;
    private TrustMeAdapter trustMeAdapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_trusteds;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        BenihBus.pluck().receive().subscribe(o -> {
            if (o instanceof RemoveTrustedUserClick) {
                removeTrustedUser(((RemoveTrustedUserClick) o).getUserTrusted());
            } else if (o instanceof AcceptTrustMeClick) {
                acceptTrustMe(((AcceptTrustMeClick) o).getUserTrusted());
            } else if (o instanceof DeclineTrustMeClick) {
                declineTrustMe(((DeclineTrustMeClick) o).getUserTrusted());
            }
        }, throwable -> Timber.e(throwable.getMessage()));

        recyclerView.setUpAsList();
        trustedUserAdapter = new TrustedUserAdapter(getActivity());
        recyclerView.setAdapter(trustedUserAdapter);

        trustMeAdapter = new TrustMeAdapter(getActivity());

        trustedUserPresenter = new TrustedUserPresenter(this);
        trustedUserPresenter.loadTrustedUser();

        trustMePresenter = new TrustMePresenter(this);
        trustMePresenter.loadTrustMeUser();
    }

    private void declineTrustMe(UserTrusted userTrusted) {
        trustMePresenter.decline(userTrusted);
    }

    private void acceptTrustMe(UserTrusted userTrusted) {
        trustMePresenter.accept(userTrusted);
    }

    @OnClick(R.id.fab)
    public void addNewContact() {
        EditText editText = new EditText(getActivity());
        editText.setHint("Email");
        editText.setInputType(InputType.TYPE_CLASS_TEXT
                                      | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Masukan alamat email kontak")
                .setView(editText)
                .setPositiveButton("Kirim", (dialog, which) -> {
                    String email = editText.getText().toString();

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        showError("Mohon masukan alamat email yang valid!");
                    } else if (email.equalsIgnoreCase(CacheManager.pluck().getCurrentUser().getEmail())) {
                        showError("Mohon masukan alamat email selain email anda sendiri!");
                    } else {
                        trustedUserPresenter.searchUser(email);
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

    @OnClick(R.id.trusted)
    public void showTrustedUser() {
        if (fab.getVisibility() == View.GONE) {
            trusted.setBackgroundResource(R.color.colorPrimary);
            trusted.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            trustMe.setBackgroundResource(R.color.white);
            trustMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.divider));
            title.setText("Daftar kontak yang anda percayai");
            trustedCounter.setVisibility(View.VISIBLE);
            trustMeCount.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(trustedUserAdapter);
        }
    }

    @OnClick(R.id.trust_me)
    public void showTrustMe() {
        if (fab.getVisibility() == View.VISIBLE) {
            trustMe.setBackgroundResource(R.color.colorPrimary);
            trustMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            trusted.setBackgroundResource(R.color.white);
            trusted.setTextColor(ContextCompat.getColor(getActivity(), R.color.divider));
            title.setText("Daftar kontak yang mempercayai anda");
            trustMeCount.setVisibility(View.VISIBLE);
            trustedCounter.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            recyclerView.setAdapter(trustMeAdapter);
        }
    }

    private void removeTrustedUser(UserTrusted userTrusted) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Apakah anda ingin menghapus " + userTrusted.getUser().getName() + " dari daftar kontak terpercaya anda?")
                .setPositiveButton("YA, Hapus Dia", (dialog, which) -> {
                    trustedUserPresenter.removeTrustedUser(userTrusted);
                    dialog.dismiss();
                })
                .setNegativeButton("TIDAK", (dialog, which1) -> {
                    dialog.dismiss();
                })
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        alertDialog.show();
    }

    @Override
    public void onFoundUser(User user) {
        trustedUserPresenter.addTrustedUser(user);
    }

    @Override
    public void onNotFoundUser(String email) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Email Tidak Terdaftar")
                .setMessage("Email yang anda masukan tidak terdaftar, undang teman anda untuk bergabung ke Sigap?")
                .setPositiveButton("YA, Undang Dia", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Saya Mengundang Anda Bergabung Ke Sigap");
                    intent.putExtra(Intent.EXTRA_TEXT, "Halo...!\nSaya membutuhkan anda untuk menjadi kontak yang saya percayai di Sigap, mari bergabung bersama saya di Sigap untuk mewujudkan kehidupan yang lebih Siap dan Tanggap terhadap segala ancaman bahaya. Untuk bergabung ke Sigap silahkan download aplikasinya di url ini [URL].\nTerimakasih banyak atas perhatiannya.\n\nSalam,\n"
                            + CacheManager.pluck().getCurrentUser().getName());
                    startActivity(intent);
                })
                .setNegativeButton("TIDAK", (dialog, which1) -> {
                    dialog.dismiss();
                })
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        alertDialog.show();
    }

    @Override
    public void onTrustedUserAdded(UserTrusted userTrusted) {
        trustedUserAdapter.addOrUpdate(userTrusted);
        trustedCounter.setText("(" + trustedUserAdapter.getData().size() + ")");
    }

    @Override
    public void onTrustedUserRemoved(UserTrusted userTrusted) {
        trustedUserAdapter.remove(userTrusted);
        trustedCounter.setText("(" + trustedUserAdapter.getData().size() + ")");
    }

    @Override
    public void onTrustedUserChanged(UserTrusted userTrusted) {
        trustedUserAdapter.addOrUpdate(userTrusted);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Silahkan tunggu...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void onAccepted(UserTrusted userTrusted) {
        trustMeAdapter.addOrUpdate(userTrusted);
        trustMeCount.setText("(" + trustMeAdapter.getData().size() + ")");
    }

    @Override
    public void onDeclined(UserTrusted userTrusted) {
        trustMeAdapter.remove(userTrusted);
        trustMeCount.setText("(" + trustMeAdapter.getData().size() + ")");
    }

    @Override
    public void onTrustMeUserAdded(UserTrusted userTrusted) {
        trustMeAdapter.addOrUpdate(userTrusted);
        trustMeCount.setText("(" + trustMeAdapter.getData().size() + ")");
    }

    @Override
    public void onTrustMeUserRemoved(UserTrusted userTrusted) {
        trustMeAdapter.remove(userTrusted);
        trustMeCount.setText("(" + trustMeAdapter.getData().size() + ")");
    }
}
