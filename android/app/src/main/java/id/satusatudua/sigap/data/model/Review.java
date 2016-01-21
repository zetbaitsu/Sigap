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
 * Created on : January 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class Review implements Parcelable {
    private String userId;
    private Date date;
    private int rate;
    private String title;
    private String description;
    private User user;

    public Review() {

    }

    protected Review(Parcel in) {
        userId = in.readString();
        date = new Date(in.readLong());
        rate = in.readInt();
        title = in.readString();
        description = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeLong(date.getTime());
        dest.writeInt(rate);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(user, flags);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Review && ((Review) o).userId.equals(userId);
    }

    @Override
    public String toString() {
        return "Review{" +
                "userId='" + userId + '\'' +
                ", date=" + date +
                ", rate=" + rate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
