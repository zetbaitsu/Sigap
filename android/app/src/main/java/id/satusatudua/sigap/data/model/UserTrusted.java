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
public class UserTrusted implements Parcelable {
    private String userId;
    private List<String> trustedUserIds;

    public UserTrusted() {

    }

    protected UserTrusted(Parcel in) {
        userId = in.readString();
        trustedUserIds = in.createStringArrayList();
    }

    public static final Creator<UserTrusted> CREATOR = new Creator<UserTrusted>() {
        @Override
        public UserTrusted createFromParcel(Parcel in) {
            return new UserTrusted(in);
        }

        @Override
        public UserTrusted[] newArray(int size) {
            return new UserTrusted[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTrustedUserIds() {
        return trustedUserIds;
    }

    public void setTrustedUserIds(List<String> trustedUserIds) {
        this.trustedUserIds = trustedUserIds;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserTrusted && ((UserTrusted) o).userId.equals(userId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeStringList(trustedUserIds);
    }

    @Override
    public String toString() {
        return "UserTrusted{" +
                "userId='" + userId + '\'' +
                ", trustedUserIds=" + trustedUserIds +
                '}';
    }
}
