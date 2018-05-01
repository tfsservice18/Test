package net.lightapi.portal.form.common.event;

public class FormDeletedEvent implements FormEvent {

    String data;

    public FormDeletedEvent() {
    }

    public FormDeletedEvent(String data) {
        this.data = data;
    }

    public String getForm() {
        return data;
    }

    public void setForm(String data) {
        this.data = data;
    }
}
