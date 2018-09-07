package net.lightapi.portal.user.command;

public class CreateUserCommand implements UserCommand {

    private String data;

    public CreateUserCommand(String data) {
        this.data = data;
    }

    public String getUser() {
        return data;
    }
}
