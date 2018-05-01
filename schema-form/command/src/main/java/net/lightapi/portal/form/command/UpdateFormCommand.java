package net.lightapi.portal.form.command;

public class UpdateFormCommand implements FormCommand {

    String data;

    public UpdateFormCommand(String data) {
        this.data = data;
    }

    public String getForm() {
        return data;
    }

}
