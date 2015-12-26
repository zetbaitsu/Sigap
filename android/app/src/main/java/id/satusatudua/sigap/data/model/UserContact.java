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

import java.util.List;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class UserContact implements Parcelable {
    private String userId;
    private List<String> contactIds;

    public UserContact() {

    }

    protected UserContact(Parcel in) {
        userId = in.readString();
        contactIds = in.createStringArrayList();
    }

    public static final Creator<UserContact> CREATOR = new Creator<UserContact>() {
        @Override
        public UserContact createFromParcel(Parcel in) {
            return new UserContact(in);
        }

        @Override
        public UserContact[] newArray(int size) {
            return new UserContact[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getContactIds() {
        return contactIds;
    }

    public void setContactIds(List<String> contactIds) {
        this.contactIds = contactIds;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserContact && ((UserContact) o).userId.equals(userId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeStringList(contactIds);
    }

    @Override
    public String toString() {
        return "UserContact{" +
                "userId='" + userId + '\'' +
                ", contactIds=" + contactIds +
                '}';
    }
}
