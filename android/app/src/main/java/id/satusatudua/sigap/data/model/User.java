package id.satusatudua.sigap.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class User implements Parcelable {
    private String uid;
    private String idNumber;
    private String email;
    private String name;
    private String gender;
    private String birthday;
    private String status;
    private Location location;

    public User() {

    }

    protected User(Parcel in) {
        uid = in.readString();
        idNumber = in.readString();
        email = in.readString();
        name = in.readString();
        gender = in.readString();
        birthday = in.readString();
        status = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(idNumber);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(birthday);
        dest.writeString(status);
    }

    public static class Builder {
        private User user;

        public Builder() {
            user = new User();
        }

        public Builder setUid(String uid) {
            user.uid = uid;
            return this;
        }

        public Builder setIdNumber(String idNumber) {
            user.idNumber = idNumber;
            return this;
        }

        public Builder setEmail(String email) {
            user.email = email;
            return this;
        }

        public Builder setName(String name) {
            user.name = name;
            return this;
        }

        public Builder setGender(String gender) {
            user.gender = gender;
            return this;
        }

        public Builder setBirthday(String birthday) {
            user.birthday = birthday;
            return this;
        }

        public Builder setStatus(String status) {
            user.status = status;
            return this;
        }

        public Builder setLocation(Location location) {
            user.location = location;
            return this;
        }

        public User build() {
            return user;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User user = (User) o;
            return uid.equals(user.uid);
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", status='" + status + '\'' +
                ", location=" + location +
                '}';
    }
}
