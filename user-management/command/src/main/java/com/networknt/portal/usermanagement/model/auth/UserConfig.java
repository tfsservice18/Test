package com.networknt.portal.usermanagement.model.auth;



/**
 * A User setting configuration file. It get from defined resource yml file
 *
 */
public class UserConfig {

    private String subject;
    private boolean emitEvent;
    private String content;
    private String serverHost;
    private boolean sendEmail;

    private boolean hybridServie;
    private String hybridLink;


    public UserConfig() {
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isEmitEvent() {
        return emitEvent;
    }

    public void setEmitEvent(boolean emitEvent) {
        this.emitEvent = emitEvent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }


    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isHybridServie() {
        return hybridServie;
    }

    public void setHybridServie(boolean hybridServie) {
        this.hybridServie = hybridServie;
    }

    public String getHybridLink() {
        return hybridLink;
    }

    public void setHybridLink(String hybridLink) {
        this.hybridLink = hybridLink;
    }
}
