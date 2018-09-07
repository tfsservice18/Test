package net.lightapi.portal.user.common.event;

public class UserDeletedEvent implements UserEvent {

    private String id;

    private UserDeletedEvent() {
    }

    public UserDeletedEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}