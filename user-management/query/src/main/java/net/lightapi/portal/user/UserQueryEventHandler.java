package net.lightapi.portal.user;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.eventuate.common.DispatchedEvent;
import com.networknt.eventuate.common.EventHandlerMethod;
import com.networknt.eventuate.common.EventSubscriber;
import net.lightapi.portal.user.common.event.UserDeletedEvent;
import net.lightapi.portal.user.common.event.UserSignedUpEvent;
import net.lightapi.portal.user.common.event.UserUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventSubscriber(id = "userQuerySideEventHandlers")
public class UserQueryEventHandler {
    static final Logger logger = LoggerFactory.getLogger(UserQueryEventHandler.class);
    private UserRepository repo = SingletonServiceFactory.getBean(UserRepository.class);

    public UserQueryEventHandler() {
    }

    @EventHandlerMethod
    public void createUser(DispatchedEvent<UserSignedUpEvent> de) {
        if(logger.isDebugEnabled()) logger.debug("createUser is called with event = " + de);
        String data = de.getEvent().getUser();
        repo.createUser(de.getEntityId(), data);
    }

    @EventHandlerMethod
    public void deleteUser(DispatchedEvent<UserDeletedEvent> de) {
        if(logger.isDebugEnabled()) logger.debug("deleteUser is called with event = " + de);
        repo.removeUser(de.getEntityId());
    }

    @EventHandlerMethod
    public void updateUser(DispatchedEvent<UserUpdatedEvent> de) {
        if(logger.isDebugEnabled()) logger.debug("updateMenu is called with event = " + de);
        String data = de.getEvent().getUser();
        repo.updateUser(de.getEntityId(), data);
    }
}
