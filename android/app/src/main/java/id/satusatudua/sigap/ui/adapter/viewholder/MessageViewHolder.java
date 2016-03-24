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

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.util.MapUtils;
import id.zelory.benih.ui.adapter.viewholder.BenihItemViewHolder;
import id.zelory.benih.ui.view.BenihImageView;

import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnItemClickListener;
import static id.zelory.benih.ui.adapter.BenihRecyclerAdapter.OnLongItemClickListener;

/**
 * Created on : January 14, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class MessageViewHolder extends BenihItemViewHolder<Message> {

    @Bind(R.id.date) TextView date;
    @Bind(R.id.time) TextView time;
    @Nullable @Bind(R.id.message) TextView content;
    @Nullable @Bind(R.id.icon_check) ImageView checkIcon;
    @Nullable @Bind(R.id.sender) TextView sender;
    @Nullable @Bind(R.id.picture) BenihImageView picture;

    private boolean showDate;

    public MessageViewHolder(View itemView, OnItemClickListener itemClickListener, OnLongItemClickListener longItemClickListener) {
        super(itemView, itemClickListener, longItemClickListener);
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    @Override
    public void bind(Message message) {
        if (showDate) {
            date.setText(DateUtils.getRelativeTimeSpanString(message.getDate().getTime(),
                                                             System.currentTimeMillis(),
                                                             DateUtils.DAY_IN_MILLIS));
            date.setVisibility(View.VISIBLE);
        } else {
            date.setVisibility(View.GONE);
        }

        time.setText(new SimpleDateFormat("HH:mm").format(message.getDate()));

        if (content != null) {
            if (message.getContent().startsWith("[DANGER]") && message.getContent().endsWith("[/DANGER]")) {
                content.setText(message.getContent().replace("[DANGER]", "").replace("[/DANGER]", ""));
            } else if (message.getContent().startsWith("[INITIAL]") && message.getContent().endsWith("[/INITIAL]")) {
                content.setText(message.getContent().replace("[INITIAL]", "").replace("[/INITIAL]", ""));
            } else if (message.getContent().startsWith("[CLOSED]") && message.getContent().endsWith("[/CLOSED]")) {
                content.setText(message.getContent().replace("[CLOSED]", "").replace("[/CLOSED]", ""));
            } else {
                if (message.isAttachment()) {
                    content.setText(Html.fromHtml("File: <u>" + message.getAttachmentName() + "</u>"));
                } else if (message.isLocation()) {
                    content.setText(message.getLocationName());
                    if (picture != null) {
                        picture.setImageUrl(MapUtils.getImageUrl(message.getLatLng().latitude, message.getLatLng().longitude));
                    }
                } else {
                    content.setText(message.getContent());
                }
            }
        } else if (picture != null) {
            picture.setImageUrl(message.getCompressedUrl());
        }

        if (sender != null) {
            if (message.getSenderId().equals("Sigap")) {
                sender.setText("~Sigap");
            } else if (message.getContent().startsWith("[DANGER]") && message.getContent().endsWith("[/DANGER]")) {
                sender.setText("~" + message.getSender().getName());
            } else if (!message.isFromMe()) {
                sender.setText("~" + message.getSender().getName());
            }
        }
        if (checkIcon != null) {
            checkIcon.setImageResource(message.isSending() ? R.drawable.ic_non_centang : R.drawable.ic_centang);
        }
    }
}
