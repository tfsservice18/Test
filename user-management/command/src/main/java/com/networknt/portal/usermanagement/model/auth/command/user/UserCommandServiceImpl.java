package com.networknt.portal.usermanagement.model.auth.command.user;

import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EntityWithIdAndVersion;

import com.networknt.portal.usermanagement.model.common.domain.UserDto;

import java.util.concurrent.CompletableFuture;


public class UserCommandServiceImpl implements UserCommandService {


    private AggregateRepository<UserAggregate, UserCommand> aggregateRepository;


    public UserCommandServiceImpl(AggregateRepository<UserAggregate, UserCommand> userRepository) {
        this.aggregateRepository = userRepository;
    }


    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> add(UserDto userDto) {
        return aggregateRepository.save(new CreateUserCommand(userDto));
    }

    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> remove(String id) {

        return aggregateRepository.update(id, new DeleteUserCommand(id));
    }

    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> confirm(String id, String token) {

        return aggregateRepository.update(id, new UserActionCommand(id, token));
    }

    @Override
    public CompletableFuture<EntityWithIdAndVersion<UserAggregate>> update(String id, UserDto newUser) {

        return aggregateRepository.update(id, new UpdateUserCommand(id, newUser));
    }


}
