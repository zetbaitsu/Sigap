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

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.adapter.BenihRecyclerAdapter.OnItemClickListener;
import id.zelory.benih.adapter.viewholder.BenihItemViewHolder;

import static id.zelory.benih.adapter.BenihRecyclerAdapter.OnLongItemClickListener;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class UserViewHolder extends BenihItemViewHolder<User> {

    @Bind(R.id.email) TextView email;
    @Bind(R.id.name) TextView name;

    public UserViewHolder(View itemView, OnItemClickListener itemClickListener, OnLongItemClickListener longItemClickListener) {
        super(itemView, itemClickListener, longItemClickListener);
    }

    @Override
    public void bind(User user) {
        email.setText(user.getEmail());
        name.setText(user.getName());
    }
}
