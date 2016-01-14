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

package id.satusatudua.sigap.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class Message implements Parcelable {
    private String messageId;
    private String senderId;
    private Date date;
    private String content;
    private User sender;
    private boolean fromMe;
    private boolean sending;

    public Message() {

    }

    protected Message(Parcel in) {
        messageId = in.readString();
        senderId = in.readString();
        date = new Date(in.readLong());
        content = in.readString();
        sender = in.readParcelable(User.class.getClassLoader());
        fromMe = in.readByte() != 0;
        sending = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Message && ((Message) o).messageId.equals(messageId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", date=" + date +
                ", content='" + content + '\'' +
                ", sender=" + sender +
                ", fromMe=" + fromMe +
                ", sending=" + sending +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageId);
        dest.writeString(senderId);
        dest.writeLong(date.getTime());
        dest.writeString(content);
        dest.writeParcelable(sender, flags);
        dest.writeByte((byte) (fromMe ? 1 : 0));
        dest.writeByte((byte) (sending ? 1 : 0));
    }
}
