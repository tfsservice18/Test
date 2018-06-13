
package com.networknt.portal.usermanagement.restcommand.handler;


import com.networknt.config.Config;
import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EventuateAggregateStore;
import com.networknt.handler.LightHttpHandler;
import com.networknt.portal.usermanagement.model.auth.command.user.UserAggregate;
import com.networknt.portal.usermanagement.model.auth.command.user.UserCommandService;
import com.networknt.portal.usermanagement.model.auth.command.user.UserCommandServiceImpl;
import com.networknt.service.SingletonServiceFactory;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.concurrent.CompletableFuture;

public class UserTokenIdGetHandler implements LightHttpHandler {
    private EventuateAggregateStore eventStore  = (EventuateAggregateStore) SingletonServiceFactory.getBean(EventuateAggregateStore.class);
    private AggregateRepository userRepository = new AggregateRepository(UserAggregate.class, eventStore);

    private UserCommandService service = new UserCommandServiceImpl(userRepository);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String id = exchange.getQueryParameters().get("id").getFirst();
        String token = exchange.getQueryParameters().get("token").getFirst();

        CompletableFuture<String> result = service.confirm(id, token).thenApply((e) -> {
            String m =  "active user: " + e.getEntityId();
            return m;
        });
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(result));

    }
}
