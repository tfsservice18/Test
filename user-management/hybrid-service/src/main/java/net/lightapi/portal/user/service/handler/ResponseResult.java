
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ResponseResult {


    private String message;

    private boolean completed;



    public ResponseResult() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
