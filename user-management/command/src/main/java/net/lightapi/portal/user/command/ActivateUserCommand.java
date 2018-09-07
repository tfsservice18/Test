package net.lightapi.portal.user.command;

public class ActivateUserCommand implements UserCommand {

    private String tokenId;
    private String id;

    public ActivateUserCommand(String id, String tokenId) {
        this.id = id;
        this.tokenId = tokenId;
    }

    public String getId() {
        return id;
    }

    public String getTokenId() {
        return this.tokenId;
    }
}
