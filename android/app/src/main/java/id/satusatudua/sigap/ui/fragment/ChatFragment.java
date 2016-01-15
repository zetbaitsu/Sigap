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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.ChatPresenter;
import id.satusatudua.sigap.ui.HelpingActivity;
import id.satusatudua.sigap.ui.MainActivity;
import id.satusatudua.sigap.ui.adapter.ChatAdapter;
import id.satusatudua.sigap.ui.adapter.HelperAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import timber.log.Timber;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ChatFragment extends BenihFragment implements ChatPresenter.View {
    private static final String KEY_CASE = "extra_case";
    private static final String KEY_REPORTER = "extra_reporter";


    @Bind(R.id.list_message) BenihRecyclerView listMessage;
    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.button_helpers) ImageView buttonHelpers;
    @Bind(R.id.divider) View divider;
    @Bind(R.id.field_message) EditText messageField;
    @Bind(R.id.button_send) ImageView buttonSend;

    private ChatAdapter chatAdapter;
    private HelperAdapter helperAdapter;
    private Case theCase;
    private User reporter;
    private ChatPresenter chatPresenter;
    private ProgressDialog progressDialog;

    public static ChatFragment newInstance(Case theCase, User reporter) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CASE, theCase);
        bundle.putParcelable(KEY_REPORTER, reporter);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        resolveTheCase(savedInstanceState);
        resolveReporter(savedInstanceState);

        chatAdapter = new ChatAdapter(getActivity());
        listMessage.setUpAsBottomList();
        listMessage.setAdapter(chatAdapter);

        helperAdapter = new HelperAdapter(getActivity());
        listHelper.setUpAsHorizontalList();
        helperAdapter.add(transformReporter());
        listHelper.setAdapter(helperAdapter);
        helperAdapter.setOnItemClickListener((view, position) -> onItemHelperClicked(helperAdapter.getData().get(position)));

        chatPresenter = new ChatPresenter(this, theCase);
        if (savedInstanceState == null) {
            chatPresenter.loadHelper();
        } else {
            chatPresenter.loadState(savedInstanceState);
        }
    }

    private CandidateHelper transformReporter() {
        CandidateHelper reporter = new CandidateHelper();
        reporter.setCandidate(this.reporter);
        reporter.setCandidateId(this.reporter.getUserId());
        reporter.setStatus(CandidateHelper.Status.MENOLONG);
        return reporter;
    }

    private void resolveTheCase(Bundle savedInstanceState) {
        theCase = getArguments().getParcelable(KEY_CASE);

        if (theCase == null && savedInstanceState != null) {
            theCase = savedInstanceState.getParcelable(KEY_CASE);
        }

        if (theCase == null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void resolveReporter(Bundle savedInstanceState) {
        reporter = getArguments().getParcelable(KEY_REPORTER);

        if (reporter == null && savedInstanceState != null) {
            reporter = savedInstanceState.getParcelable(KEY_REPORTER);
        }

        if (reporter == null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @OnTextChanged(R.id.field_message)
    public void fieldMessageChanged(CharSequence message) {
        if (message == null || "".equals(message.toString())) {
            buttonSend.setImageResource(R.drawable.ic_grey_send);
        } else {
            buttonSend.setImageResource(R.drawable.ic_send);
        }
    }

    private void onItemHelperClicked(CandidateHelper candidateHelper) {
        Timber.d(candidateHelper.toString());
    }

    @OnClick(R.id.button_send)
    public void sendMessage() {
        if (theCase.getStatus() != Case.Status.DITUTUP) {
            String content = messageField.getText().toString();
            if (!content.isEmpty()) {
                messageField.setText("");
                chatPresenter.sendMessage(content);
            }
        } else {
            HelpingActivity activity = (HelpingActivity) getActivity();
            activity.onCaseClosed(theCase);
        }
    }

    public void sendDangerMessage() {
        chatPresenter.sendMessage("[DANGER]BAHAYA!!!! BAHAYA!!! BAHAYA!!! SEGERA HUBUNGI PIHAK BERWAJIB!!!!![/DANGER]");
    }

    @OnClick(R.id.button_helpers)
    public void toggleHelpers() {
        if (listHelper.getVisibility() == View.VISIBLE) {
            listHelper.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            buttonHelpers.setImageResource(R.drawable.ic_grey_orang);
        } else {
            listHelper.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            buttonHelpers.setImageResource(R.drawable.ic_orang);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        chatPresenter.saveState(outState);
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CASE, theCase);
        outState.putParcelable(KEY_REPORTER, reporter);
    }

    @Override
    public void showHelpers(List<CandidateHelper> helpers) {
        helperAdapter.addOrUpdate(helpers);
    }

    @Override
    public void onHelperStatusChanged(CandidateHelper candidateHelper) {
        helperAdapter.addOrUpdate(candidateHelper);
    }

    @Override
    public void onSendingMessage(Message message) {
        chatAdapter.add(message);
        listMessage.post(() -> listMessage.smoothScrollToPosition(chatAdapter.getItemCount() - 1));
    }

    @Override
    public void onSuccessSendMessage(Message message) {
        chatAdapter.addOrUpdate(message);
    }

    @Override
    public void onFailedSendMessage(Message message) {
        chatAdapter.remove(message);
    }

    @Override
    public void onNewMessage(Message message) {
        chatAdapter.addOrUpdate(message);
        listMessage.post(() -> listMessage.smoothScrollToPosition(chatAdapter.getItemCount() - 1));
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(listMessage, errorMessage, Snackbar.LENGTH_LONG);
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
}
