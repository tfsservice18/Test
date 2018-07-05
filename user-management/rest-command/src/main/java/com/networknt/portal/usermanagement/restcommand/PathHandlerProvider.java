
package com.networknt.portal.usermanagement.restcommand;

import com.networknt.config.Config;
import com.networknt.handler.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;
import com.networknt.info.ServerInfoGetHandler;
import com.networknt.health.HealthGetHandler;
import com.networknt.portal.usermanagement.restcommand.handler.*;

public class PathHandlerProvider implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
        
            .add(Methods.GET, "/v1/health", new HealthGetHandler())
        
            .add(Methods.DELETE, "/v1/user/{id}", new UserIdDeleteHandler())
        
            .add(Methods.PUT, "/v1/user/{id}", new UserIdPutHandler())
        
            .add(Methods.GET, "/v1/user/token/{id}", new UserTokenIdGetHandler())
        
            .add(Methods.POST, "/v1/user", new UserPostHandler())
        
            .add(Methods.GET, "/v1/server/info", new ServerInfoGetHandler())
        
        ;
    }
}
