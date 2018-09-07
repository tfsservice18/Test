package net.lightapi.portal.user.command;

public class UpdateUserCommand implements UserCommand {

    private String data;
    private String id;


    public UpdateUserCommand(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getUser() {
        return data;
    }

    public String getId() {
        return id;
    }
}
