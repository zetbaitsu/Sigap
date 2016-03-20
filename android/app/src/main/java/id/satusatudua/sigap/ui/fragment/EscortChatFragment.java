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
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.GuardCandidate;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.presenter.EscortChatPresenter;
import id.satusatudua.sigap.ui.MainActivity;
import id.satusatudua.sigap.ui.ProfileActivity;
import id.satusatudua.sigap.ui.adapter.ChatAdapter;
import id.satusatudua.sigap.ui.adapter.GuardAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EscortChatFragment extends BenihFragment implements EscortChatPresenter.View {
    private static final String KEY_ESCORT = "extra_escort";

    @Bind(R.id.list_message) BenihRecyclerView listMessage;
    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.button_helpers) ImageView buttonHelpers;
    @Bind(R.id.divider) View divider;
    @Bind(R.id.field_message) EditText messageField;
    @Bind(R.id.button_send) ImageView buttonSend;

    private ChatAdapter chatAdapter;
    private GuardAdapter guardAdapter;
    private Escort escort;
    private EscortChatPresenter chatPresenter;
    private ProgressDialog progressDialog;

    public static EscortChatFragment newInstance(Escort escort) {
        EscortChatFragment chatFragment = new EscortChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_ESCORT, escort);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        resolveData(savedInstanceState);

        chatAdapter = new ChatAdapter(getActivity());
        listMessage.setUpAsBottomList();
        listMessage.setAdapter(chatAdapter);

        guardAdapter = new GuardAdapter(getActivity());
        listHelper.setUpAsHorizontalList();
        listHelper.setAdapter(guardAdapter);
        guardAdapter.setOnItemClickListener((view, position) -> onItemGuardClicked(guardAdapter.getData().get(position)));

        chatPresenter = new EscortChatPresenter(this, escort);
        if (savedInstanceState == null) {
            chatPresenter.loadGuards();
        } else {
            chatPresenter.loadState(savedInstanceState);
        }
    }

    private void onItemGuardClicked(GuardCandidate guardCandidate) {
        startActivity(ProfileActivity.generateIntent(getActivity(), guardCandidate.getUser()));
    }

    private void resolveData(Bundle savedInstanceState) {
        escort = getArguments().getParcelable(KEY_ESCORT);

        if (escort == null && savedInstanceState != null) {
            escort = savedInstanceState.getParcelable(KEY_ESCORT);
        }

        if (escort == null) {
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

    @OnClick(R.id.button_send)
    public void sendMessage() {
        String content = messageField.getText().toString();
        if (!content.isEmpty()) {
            messageField.setText("");
            chatPresenter.sendMessage(content);
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
        outState.putParcelable(KEY_ESCORT, escort);
    }

    @Override
    public void showGuards(List<GuardCandidate> guardCandidates) {
        guardAdapter.addOrUpdate(guardCandidates);
    }

    @Override
    public void onGuardStatusChanged(GuardCandidate guardCandidate) {
        guardAdapter.addOrUpdate(guardCandidate);
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
        try {
            Snackbar snackbar = Snackbar.make(listMessage, errorMessage, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
