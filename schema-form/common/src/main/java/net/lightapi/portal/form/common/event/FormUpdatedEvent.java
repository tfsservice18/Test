package net.lightapi.portal.form.common.event;

import java.util.Map;

public class FormUpdatedEvent implements FormEvent {
    String data;
    public FormUpdatedEvent() {
    }

    public FormUpdatedEvent(String data) {
        this.data = data;
    }

    public String getForm() {
        return data;
    }

    public void setForm(String data) {
        this.data = data;
    }

}
