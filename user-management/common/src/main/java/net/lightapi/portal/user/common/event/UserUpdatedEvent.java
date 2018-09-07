package net.lightapi.portal.user.common.event;

public class UserUpdatedEvent implements UserEvent {

    private String data;
    private String id;

    private UserUpdatedEvent() {
    }

    public UserUpdatedEvent(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getUser() {
        return data;
    }

    public void setUser(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }
}