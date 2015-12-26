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
public class HelperCase implements Parcelable {
    private String caseId;
    private List<String> helperUserIds;

    public HelperCase(){

    }

    protected HelperCase(Parcel in) {
        caseId = in.readString();
        helperUserIds = in.createStringArrayList();
    }

    public static final Creator<HelperCase> CREATOR = new Creator<HelperCase>() {
        @Override
        public HelperCase createFromParcel(Parcel in) {
            return new HelperCase(in);
        }

        @Override
        public HelperCase[] newArray(int size) {
            return new HelperCase[size];
        }
    };

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public List<String> getHelperUserIds() {
        return helperUserIds;
    }

    public void setHelperUserIds(List<String> helperUserIds) {
        this.helperUserIds = helperUserIds;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HelperCase && ((HelperCase) o).caseId.equals(caseId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caseId);
        dest.writeStringList(helperUserIds);
    }

    @Override
    public String toString() {
        return "HelperCase{" +
                "caseId='" + caseId + '\'' +
                ", helperUserIds=" + helperUserIds +
                '}';
    }
}
