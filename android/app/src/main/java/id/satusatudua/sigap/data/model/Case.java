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
public class Case implements Parcelable {
    private String caseId;
    private Date date;
    private String userId;
    private boolean open;

    public Case() {

    }

    protected Case(Parcel in) {
        caseId = in.readString();
        userId = in.readString();
        open = in.readByte() != 0;
    }

    public static final Creator<Case> CREATOR = new Creator<Case>() {
        @Override
        public Case createFromParcel(Parcel in) {
            return new Case(in);
        }

        @Override
        public Case[] newArray(int size) {
            return new Case[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        return o instanceof Case && ((Case) o).caseId.equals(caseId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caseId);
        dest.writeString(userId);
        dest.writeByte((byte) (open ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Case{" +
                "caseId='" + caseId + '\'' +
                ", date=" + date +
                ", userId='" + userId + '\'' +
                ", open=" + open +
                '}';
    }
}
