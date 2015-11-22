package id.satusatudua.sigap.data.model;

/**
 * Created by zetbaitsu on 8/21/15.
 */
public class User {
    private String username;
    private String password;
    private String name;
    private String gender;
    private String birthday;
    private String status;
    private Location location;

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public static class Builder {
        private User user;

        public Builder() {
            user = new User();
        }

        public Builder setUsername(String username) {
            user.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            user.password = password;
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
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", status='" + status + '\'' +
                ", location=" + location +
                '}';
    }
}
