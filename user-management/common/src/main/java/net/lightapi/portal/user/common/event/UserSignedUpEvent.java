package net.lightapi.portal.user.common.event;

public class UserSignedUpEvent implements UserEvent {
    String data;

    private UserSignedUpEvent() {
    }

    public UserSignedUpEvent(String data) {
        this.data = data;
    }

    public String getUser() {
        return data;
    }

    public void setUser(String data) {
        this.data = data;
    }
}