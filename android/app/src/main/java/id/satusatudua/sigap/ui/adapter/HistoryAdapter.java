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

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.ActivityHistory;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.ui.adapter.viewholder.HistoryViewHolder;
import id.zelory.benih.ui.adapter.BenihRecyclerAdapter;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class HistoryAdapter extends BenihRecyclerAdapter<ActivityHistory, HistoryViewHolder> {
    private User owner;
    private boolean isMyHistory;

    public HistoryAdapter(Context context) {
        super(context);
        this.owner = CacheManager.pluck().getCurrentUser();
        isMyHistory = true;
    }

    public HistoryAdapter(Context context, User owner) {
        super(context);
        this.owner = owner;
        isMyHistory = owner.equals(CacheManager.pluck().getCurrentUser());
    }

    @Override
    protected int getItemResourceLayout(int viewType) {
        return R.layout.item_history;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryViewHolder viewHolder = new HistoryViewHolder(getView(parent, viewType), itemClickListener, longItemClickListener);
        viewHolder.setMyHistory(isMyHistory);
        viewHolder.setOwner(owner);
        return viewHolder;
    }
}
