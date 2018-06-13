
package com.networknt.portal.usermanagement.handler;

import com.networknt.handler.LightHttpHandler;
import io.undertow.server.HttpServerExchange;

public class SessionIdDeleteHandler implements LightHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        
            exchange.endExchange();
        
    }
}
