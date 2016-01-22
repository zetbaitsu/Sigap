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

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ActivityHistory;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.ui.adapter.viewholder.BenihItemViewHolder;

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
public class HistoryViewHolder extends BenihItemViewHolder<ActivityHistory> {

    @Bind(R.id.title) TextView title;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.address) TextView address;

    private boolean isMyHistory;
    private User owner;

    public HistoryViewHolder(View itemView, OnItemClickListener itemClickListener, OnLongItemClickListener longItemClickListener) {
        super(itemView, itemClickListener, longItemClickListener);
    }

    public void setMyHistory(boolean isMyHistory) {
        this.isMyHistory = isMyHistory;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public void bind(ActivityHistory history) {

        if (isMyHistory) {
            if (history.isFromMe()) {
                title.setText("Anda membutuhkan bantuan");
            } else {
                title.setText("Anda menolong " + history.getUser().getName());
            }
        } else {
            String firstName = owner.getName().split(" ")[0];
            if (history.isFromMe()) {
                title.setText(firstName + " membutuhkan bantuan");
            } else {
                title.setText(firstName + " menolong " + history.getUser().getName());
            }
        }

        date.setText(new SimpleDateFormat("dd MMMM yyyy").format(history.getTheCase().getDate()));
        address.setText(history.getTheCase().getAddress() == null
                                || history.getTheCase().getAddress().trim().isEmpty()
                                ? "Alamat Tidak Diketahui" : history.getTheCase().getAddress());
    }
}
