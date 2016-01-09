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
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.zelory.benih.ui.adapter.viewholder.BenihItemViewHolder;

import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnItemClickListener;
import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnLongItemClickListener;

/**
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ContactViewHolder extends BenihItemViewHolder<ImportantContact> {
    @Bind(R.id.name) TextView name;
    @Bind(R.id.rate) TextView rate;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.icon_bookmarked) ImageView bookmarkedIcon;
    @Bind(R.id.text_initial) TextView initial;

    private boolean showInitial;

    public ContactViewHolder(View itemView, OnItemClickListener itemClickListener, OnLongItemClickListener longItemClickListener) {
        super(itemView, itemClickListener, longItemClickListener);
    }

    public void setShowInitial(boolean showInitial) {
        this.showInitial = showInitial;
    }

    @Override
    public void bind(ImportantContact importantContact) {
        if (showInitial && importantContact.isBookmarked()) {
            bookmarkedIcon.setVisibility(View.VISIBLE);
            initial.setVisibility(View.INVISIBLE);
        } else if (showInitial) {
            initial.setText(importantContact.getName().substring(0, 1).toUpperCase());
            initial.setVisibility(View.VISIBLE);
            bookmarkedIcon.setVisibility(View.INVISIBLE);
        } else {
            bookmarkedIcon.setVisibility(View.INVISIBLE);
            initial.setVisibility(View.INVISIBLE);
        }

        name.setText(importantContact.getName());
        rate.setText(importantContact.getAvgRate() + "");
        author.setText(importantContact.getUserId());
    }
}
