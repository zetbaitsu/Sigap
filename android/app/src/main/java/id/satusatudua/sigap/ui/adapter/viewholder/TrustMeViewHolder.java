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

package id.satusatudua.sigap.ui.adapter.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.event.AcceptTrustMeClick;
import id.satusatudua.sigap.event.DeclineTrustMeClick;
import id.zelory.benih.ui.adapter.viewholder.BenihItemViewHolder;
import id.zelory.benih.util.BenihBus;

import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnItemClickListener;
import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnLongItemClickListener;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TrustMeViewHolder extends BenihItemViewHolder<UserTrusted> {

    @Bind(R.id.tv_name) TextView name;
    @Bind(R.id.tv_email) TextView email;
    @Bind(R.id.tv_status) TextView status;
    @Bind(R.id.root_button) CardView rootButton;
    @Bind(R.id.button_accept) TextView buttonAccept;
    @Bind(R.id.button_decline) TextView buttonDecline;

    public TrustMeViewHolder(View itemView, OnItemClickListener itemClickListener, OnLongItemClickListener longItemClickListener) {
        super(itemView, itemClickListener, longItemClickListener);
    }

    @Override
    public void bind(UserTrusted userTrusted) {

        name.setText(userTrusted.getUser().getName());
        email.setText(userTrusted.getUser().getEmail());

        if (userTrusted.getStatus() == UserTrusted.Status.MENUNGGU) {
            status.setText("[Menunggu Konfimasi]");
            status.setVisibility(View.VISIBLE);
            name.setTextColor(ContextCompat.getColor(SigapApp.pluck().getApplicationContext(), R.color.secondary_text));
            status.setTextColor(ContextCompat.getColor(SigapApp.pluck().getApplicationContext(), R.color.secondary_text));
            rootButton.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.GONE);
            name.setTextColor(ContextCompat.getColor(SigapApp.pluck().getApplicationContext(), R.color.primary_text));
            rootButton.setVisibility(View.GONE);
        }

        buttonAccept.setOnClickListener(v -> acceptTrustedUser(userTrusted));
        buttonDecline.setOnClickListener(v -> declineTrustedUser(userTrusted));
    }

    private void declineTrustedUser(UserTrusted userTrusted) {
        BenihBus.pluck().send(new DeclineTrustMeClick(userTrusted));
    }

    private void acceptTrustedUser(UserTrusted userTrusted) {
        BenihBus.pluck().send(new AcceptTrustMeClick(userTrusted));
    }
}
