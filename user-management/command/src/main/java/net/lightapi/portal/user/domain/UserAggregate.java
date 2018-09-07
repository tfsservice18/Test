package net.lightapi.portal.user.domain;


import com.networknt.eventuate.common.Event;
import com.networknt.eventuate.common.EventUtil;
import com.networknt.eventuate.common.ReflectiveMutableCommandProcessingAggregate;

import net.lightapi.portal.user.command.*;
import net.lightapi.portal.user.common.event.UserConfirmedEvent;
import net.lightapi.portal.user.common.event.UserDeletedEvent;
import net.lightapi.portal.user.common.event.UserSignedUpEvent;
import net.lightapi.portal.user.common.event.UserUpdatedEvent;

import java.util.Collections;
import java.util.List;


public class UserAggregate extends ReflectiveMutableCommandProcessingAggregate<UserAggregate, UserCommand> {

    String id;
    String tokenId;
    String data;
    boolean deleted;

    public List<Event> process(CreateUserCommand cmd) {
        if(this.deleted) {
            return Collections.emptyList();
        }
        return EventUtil.events(new UserSignedUpEvent(cmd.getUser()));
    }

    public List<Event> process(UpdateUserCommand cmd) {
        if (this.deleted) {
            return Collections.emptyList();
        }
        return EventUtil.events(new UserUpdatedEvent(cmd.getId(), cmd.getUser()));
    }


    public List<Event> process(DeleteUserCommand cmd) {
        if (this.deleted) {
            return Collections.emptyList();
        }
        return EventUtil.events(new UserDeletedEvent(cmd.getId()));
    }


    public List<Event> process(ActivateUserCommand cmd) {
        if (this.deleted) {
            return Collections.emptyList();
        }
        return EventUtil.events(new UserConfirmedEvent( cmd.getId(), cmd.getTokenId()));
    }

    public void apply(UserSignedUpEvent event) {
        this.data = event.getUser();
    }

    public void apply(UserUpdatedEvent event) {
        this.id = event.getId();
        this.data = event.getUser();
    }

    public void apply(UserDeletedEvent event) {
        this.deleted = true;
        this.id = event.getId();
    }

    public void apply(UserConfirmedEvent event) {
        this.id = event.getId();
        this.tokenId = event.getTokenId();
    }

    public String getUser() {
        return data;
    }

}


