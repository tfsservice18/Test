package net.lightapi.portal.user;

import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EntityWithIdAndVersion;
import net.lightapi.portal.user.command.*;
import net.lightapi.portal.user.domain.UserAggregate;

import java.util.concurrent.CompletableFuture;

public class UserServiceImpl implements UserService {
    private AggregateRepository<UserAggregate, UserCommand> aggregateRepository;

    public UserServiceImpl(AggregateRepository<UserAggregate, UserCommand> aggregateRepository) {
        this.aggregateRepository = aggregateRepository;
    }


    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> create(String data) {
        return aggregateRepository.save(new CreateUserCommand(data));
    }

    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> remove(String id) {
        return aggregateRepository.update(id, new DeleteUserCommand(id));
    }

    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> update(String id, String data) {
        return aggregateRepository.update(id, new UpdateUserCommand(id, data));
    }

    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> confirm(String id, String token) {

        return aggregateRepository.update(id, new ActivateUserCommand(id, token));
    }

}
