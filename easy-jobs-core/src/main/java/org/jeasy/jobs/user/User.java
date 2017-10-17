package org.jeasy.jobs.user;

public class User {

    private String name;
    private String password; // the md5 hash of the password

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
