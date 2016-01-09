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

package id.satusatudua.sigap.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.ui.adapter.viewholder.ContactViewHolder;
import id.zelory.benih.ui.adapter.BenihRecyclerAdapter;

/**
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ContactAdapter extends
        BenihRecyclerAdapter<ImportantContact, ContactViewHolder> implements SectionIndexer {

    public ContactAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemResourceLayout(int viewType) {
        return R.layout.item_contact;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(getView(parent, viewType), itemClickListener, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        if (position == 0) {
            holder.setShowInitial(true);
        } else if (data.get(position).isBookmarked()) {
            holder.setShowInitial(false);
        } else if (!data.get(position).isBookmarked() && data.get(position - 1).isBookmarked()) {
            holder.setShowInitial(true);
        } else if (!data.get(position).getName().substring(0, 1).equalsIgnoreCase(data.get(position - 1).getName().substring(0, 1))) {
            holder.setShowInitial(true);
        } else {
            holder.setShowInitial(false);
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public Object[] getSections() {
        return data.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return position < data.size() ? position : position - 1;
    }
}
