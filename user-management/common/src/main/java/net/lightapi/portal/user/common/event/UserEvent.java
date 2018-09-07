package net.lightapi.portal.user.common.event;

import com.networknt.eventuate.common.Event;
import com.networknt.eventuate.common.EventEntity;

/**
 * Created by gavin on 2017-06-12.
 */
@EventEntity(entity = "net.lightapi.portal.user.domain.UserAggregate")
public interface UserEvent<T> extends Event{
}



