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
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.ui.adapter.viewholder.TrustMeViewHolder;
import id.zelory.benih.ui.adapter.BenihRecyclerAdapter;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TrustMeAdapter extends BenihRecyclerAdapter<UserTrusted, TrustMeViewHolder> {
    public TrustMeAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemResourceLayout(int viewType) {
        return R.layout.item_trust_me;
    }

    @Override
    public TrustMeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrustMeViewHolder(getView(parent, viewType), itemClickListener, longItemClickListener);
    }
}
