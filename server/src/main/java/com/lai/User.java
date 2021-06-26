package com.lai;

import java.util.Objects;

public class User {
    private String id;
    private int role; // 0 for manager, 1 for normal user
    private boolean initialised = false;

    public User(String uid) {
        this.id = uid;
        this.role = 1;
    }

    public User(String uid, int roleNum) {
        this.id = uid;
        this.role = roleNum;
    }

    public String getId() {
        return this.id;
    }

    public boolean getInitStatus() {
        return this.initialised;
    }

    public void setInitStatus(boolean status) {
        initialised = status;
    }

    public int getRole() {
        return this.role;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if(id.equals(other.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
