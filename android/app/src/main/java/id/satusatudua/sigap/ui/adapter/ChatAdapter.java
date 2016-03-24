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
    private static final int TYPE_MESSAGE_ME = 1;
    private static final int TYPE_MESSAGE_OTHER = 2;
    private static final int TYPE_MESSAGE_DANGER = 3;
    private static final int TYPE_PICTURE_ME = 4;
    private static final int TYPE_PICTURE_OTHER = 5;
    private static final int TYPE_FILE_ME = 6;
    private static final int TYPE_FILE_OTHER = 7;
    private static final int TYPE_LOCATION_ME = 8;
    private static final int TYPE_LOCATION_OTHER = 9;

    public ChatAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = data.get(position);
        if (message.getContent().startsWith("[DANGER]") && message.getContent().endsWith("[/DANGER]")) {
            return TYPE_MESSAGE_DANGER;
        }
        if (message.isFromMe()) {
            if (message.isPicture()) {
                return TYPE_PICTURE_ME;
            } else if (message.isAttachment()) {
                return TYPE_FILE_ME;
            } else if (message.isLocation()) {
                return TYPE_LOCATION_ME;
            }
            return TYPE_MESSAGE_ME;
        } else {
            if (message.isPicture()) {
                return TYPE_PICTURE_OTHER;
            } else if (message.isAttachment()) {
                return TYPE_FILE_OTHER;
            } else if (message.isLocation()) {
                return TYPE_LOCATION_OTHER;
            }
            return TYPE_MESSAGE_OTHER;
        }
    }

    @Override
    protected int getItemResourceLayout(int viewType) {
        switch (viewType) {
            case TYPE_MESSAGE_DANGER:
                return R.layout.item_message_danger;
            case TYPE_MESSAGE_ME:
                return R.layout.item_message_me;
            case TYPE_PICTURE_ME:
                return R.layout.item_chat_picture_me;
            case TYPE_FILE_ME:
                return R.layout.item_chat_file_me;
            case TYPE_LOCATION_ME:
                return R.layout.item_chat_location_me;
            case TYPE_MESSAGE_OTHER:
                return R.layout.item_message;
            case TYPE_PICTURE_OTHER:
                return R.layout.item_chat_picture;
            case TYPE_FILE_OTHER:
                return R.layout.item_chat_file;
            case TYPE_LOCATION_OTHER:
                return R.layout.item_chat_location;
            default:
                return R.layout.item_message;
        }
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
