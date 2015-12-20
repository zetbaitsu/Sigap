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
    private String email;
    private String name;
    private boolean male;
    private String birthDate;
    private boolean fromApps;
    private Location location;

    public User() {

    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        name = in.readString();
        male = in.readByte() != 0;
        birthDate = in.readString();
        fromApps = in.readByte() != 0;
        location = in.readParcelable(Location.class.getClassLoader());
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

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isFromApps() {
        return fromApps;
    }

    public void setFromApps(boolean fromApps) {
        this.fromApps = fromApps;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User && ((User) o).uid.equals(uid);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeByte((byte) (male ? 1 : 0));
        dest.writeString(birthDate);
        dest.writeByte((byte) (fromApps ? 1 : 0));
        dest.writeParcelable(location, flags);
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", male='" + male + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", fromApps=" + fromApps +
                ", location=" + location +
                '}';
    }
}
