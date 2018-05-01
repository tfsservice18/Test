package net.lightapi.portal.form.command;


public class DeleteFormCommand implements FormCommand {

    String data;

    public DeleteFormCommand(String data) {
        this.data = data;
    }

    public String getForm() {
        return data;
    }
}
