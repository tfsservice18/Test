package net.lightapi.portal.user.command;


public class DeleteUserCommand implements UserCommand {

    private String id;

    public DeleteUserCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
