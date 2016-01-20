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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ImportantContact implements Parcelable {
    private String contactId;
    private String name;
    private String phoneNumber;
    private String userId;
    private String address;
    private Date createdAt;
    private double avgRate;
    private double totalRate;
    private double totalUserRate;
    private boolean bookmarked;
    private User user;

    public ImportantContact() {

    }

    protected ImportantContact(Parcel in) {
        contactId = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        userId = in.readString();
        address = in.readString();
        createdAt = new Date(in.readLong());
        avgRate = in.readDouble();
        totalRate = in.readDouble();
        totalUserRate = in.readDouble();
        bookmarked = in.readByte() != 0;
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ImportantContact> CREATOR = new Creator<ImportantContact>() {
        @Override
        public ImportantContact createFromParcel(Parcel in) {
            return new ImportantContact(in);
        }

        @Override
        public ImportantContact[] newArray(int size) {
            return new ImportantContact[size];
        }
    };

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(double avgRate) {
        this.avgRate = avgRate;
    }

    public double getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(double totalRate) {
        this.totalRate = totalRate;
    }

    public double getTotalUserRate() {
        return totalUserRate;
    }

    public void setTotalUserRate(double totalUserRate) {
        this.totalUserRate = totalUserRate;
    }

    @JsonIgnore
    public boolean isBookmarked() {
        return bookmarked;
    }

    @JsonIgnore
    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ImportantContact && ((ImportantContact) o).contactId.equals(contactId);
    }

    @Override
    public String toString() {
        return "ImportantContact{" +
                "contactId='" + contactId + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", avgRate=" + avgRate +
                ", totalRate=" + totalRate +
                ", totalUserRate=" + totalUserRate +
                ", bookmarked=" + bookmarked +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactId);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(userId);
        dest.writeString(address);
        dest.writeLong(createdAt.getTime());
        dest.writeDouble(avgRate);
        dest.writeDouble(totalRate);
        dest.writeDouble(totalUserRate);
        dest.writeByte((byte) (bookmarked ? 1 : 0));
        dest.writeParcelable(user, flags);
    }
}
