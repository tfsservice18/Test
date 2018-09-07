package net.lightapi.portal.user;

import com.networknt.eventuate.common.EntityWithIdAndVersion;
import net.lightapi.portal.user.domain.UserAggregate;

import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<EntityWithIdAndVersion<UserAggregate>> create(String data);
    CompletableFuture<EntityWithIdAndVersion<UserAggregate>> remove(String id);
    CompletableFuture<EntityWithIdAndVersion<UserAggregate>> update(String id, String data);
    CompletableFuture<EntityWithIdAndVersion<UserAggregate>> confirm(String id, String token);
}
