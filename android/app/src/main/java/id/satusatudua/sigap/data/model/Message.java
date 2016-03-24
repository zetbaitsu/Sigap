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

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import id.satusatudua.sigap.data.api.CloudImage;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class Message implements Parcelable {
    public static final String TAG_OPEN_ATTACH = "[ATTACH]";
    public static final String TAG_CLOSE_ATTACH = "[/ATTACH]";
    public static final String TAG_OPEN_NAME = "[NAME]";
    public static final String TAG_CLOSE_NAME = "[/NAME]";
    public static final String TAG_OPEN_URL = "[URL]";
    public static final String TAG_CLOSE_URL = "[/URL]";
    public static final String TAG_OPEN_PICTURE = "[PICTURE]";
    public static final String TAG_CLOSE_PICTURE = "[/PICTURE]";
    public static final String TAG_OPEN_LOCATION = "[LOCATION]";
    public static final String TAG_CLOSE_LOCATION = "[/LOCATION]";
    public static final String TAG_OPEN_LATLNG = "[LATLNG]";
    public static final String TAG_CLOSE_LATLNG = "[/LATLNG]";

    private String messageId;
    private String senderId;
    private Date date;
    private String content;
    private User sender;
    private boolean fromMe;
    private boolean sending;
    private boolean seen;

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
        seen = in.readByte() != 0;
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

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isAttachment() {
        return content.startsWith(TAG_OPEN_ATTACH) && content.endsWith(TAG_CLOSE_ATTACH);
    }

    public boolean isPicture() {
        return content.startsWith(TAG_OPEN_PICTURE) && content.endsWith(TAG_CLOSE_PICTURE);
    }

    public boolean isLocation() {
        return content.startsWith(TAG_OPEN_LOCATION) && content.endsWith(TAG_CLOSE_LOCATION);
    }

    public String getAttachmentName() {
        if (!isAttachment()) {
            throw new RuntimeException("This message not a attachment!");
        }

        String data = content.replace(TAG_OPEN_ATTACH, "").replace(TAG_CLOSE_ATTACH, "");
        data = data.substring(0, data.indexOf(TAG_CLOSE_NAME));
        return data.replace(TAG_OPEN_NAME, "").replace(TAG_CLOSE_NAME, "");
    }

    public String getUrl() {
        if (isPicture()) {
            return content.replace(TAG_OPEN_PICTURE, "").replace(TAG_CLOSE_PICTURE, "");
        } else if (isAttachment()) {
            String data = content.replace(TAG_OPEN_ATTACH, "").replace(TAG_CLOSE_ATTACH, "");
            data = data.substring(data.indexOf(TAG_OPEN_URL), data.indexOf(TAG_CLOSE_URL));
            return data.replace(TAG_OPEN_URL, "").replace(TAG_CLOSE_URL, "");
        }

        throw new RuntimeException("This message not a picture or attachment!");
    }

    public String getCompressedUrl() {
        if (!isPicture()) {
            throw new RuntimeException("This message not a picture!");
        }

        String url = getUrl();
        String compressedUrl = url.substring(0, url.lastIndexOf("/"));
        compressedUrl = compressedUrl + CloudImage.MESSAGE_TRANSFROM + url.substring(url.lastIndexOf("/"));
        return compressedUrl;
    }

    public static String generateAttachmentMessage(String name, String url) {
        return TAG_OPEN_ATTACH + TAG_OPEN_NAME + name + TAG_CLOSE_NAME
                + TAG_OPEN_URL + url + TAG_CLOSE_URL + TAG_CLOSE_ATTACH;
    }

    public static String generatePictureMessage(String url) {
        return TAG_OPEN_PICTURE + url + TAG_CLOSE_PICTURE;
    }

    public static String generateLocationMessage(String name, LatLng latLng) {
        return TAG_OPEN_LOCATION + TAG_OPEN_NAME + name + TAG_CLOSE_NAME
                + TAG_OPEN_LATLNG + +latLng.latitude + "," + latLng.longitude + TAG_CLOSE_LATLNG
                + TAG_CLOSE_LOCATION;
    }

    public String getLocationName() {
        if (!isLocation()) {
            throw new RuntimeException("This message not a location!");
        }

        String data = content.replace(TAG_OPEN_LOCATION, "").replace(TAG_CLOSE_LOCATION, "");
        data = data.substring(0, data.indexOf(TAG_CLOSE_NAME));
        return data.replace(TAG_OPEN_NAME, "").replace(TAG_CLOSE_NAME, "");
    }

    public LatLng getLatLng() {
        if (!isLocation()) {
            throw new RuntimeException("This message not a location!");
        }

        String data = content.replace(TAG_OPEN_LOCATION, "").replace(TAG_CLOSE_LOCATION, "");
        data = data.substring(data.indexOf(TAG_OPEN_LATLNG), data.lastIndexOf(TAG_CLOSE_LATLNG));
        data = data.replace(TAG_OPEN_LATLNG, "").replace(TAG_CLOSE_LOCATION, "");
        String[] location = data.split(",");
        return new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
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
        dest.writeByte((byte) (seen ? 1 : 0));
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
                ", seen=" + seen +
                '}';
    }
}
