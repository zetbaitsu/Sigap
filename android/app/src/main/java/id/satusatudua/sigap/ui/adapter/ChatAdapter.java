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
import android.text.format.DateUtils;
import android.view.ViewGroup;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.ui.adapter.viewholder.MessageViewHolder;
import id.zelory.benih.ui.adapter.BenihRecyclerAdapter;

/**
 * Created on : January 14, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ChatAdapter extends BenihRecyclerAdapter<Message, MessageViewHolder> {
    private final int TYPE_MESSAGE_ME = 1;
    private final int TYPE_MESSAGE_OTHER = 2;

    public ChatAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isFromMe() ? TYPE_MESSAGE_ME : TYPE_MESSAGE_OTHER;
    }

    @Override
    protected int getItemResourceLayout(int viewType) {
        return viewType == TYPE_MESSAGE_ME ? R.layout.item_message_me : R.layout.item_message;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(getView(parent, viewType), itemClickListener, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        if (position == 0) {
            holder.setShowDate(true);
        } else {
            holder.setShowDate(!checkDate(data.get(position), data.get(position - 1)));
        }
        super.onBindViewHolder(holder, position);
    }

    private boolean checkDate(Message message1, Message message2) {
        long now = System.currentTimeMillis();
        String date1 = DateUtils.getRelativeTimeSpanString(message1.getDate().getTime(), now,
                                                           DateUtils.DAY_IN_MILLIS).toString();
        String date2 = DateUtils.getRelativeTimeSpanString(message2.getDate().getTime(), now,
                                                           DateUtils.DAY_IN_MILLIS).toString();

        return date1.equals(date2);
    }
}
