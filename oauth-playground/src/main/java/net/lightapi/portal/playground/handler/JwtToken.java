
package net.lightapi.portal.playground.handler;

import com.networknt.security.JwtIssuer;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/playground/generateJwt/0.1.0")
public class JwtToken implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(JwtToken.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        JwtClaims claims = JwtIssuer.getDefaultJwtClaims();
        ((Map<String, Object>)input).forEach((k, v) -> claims.setClaim(k, v));
        String jwt = "";
        try {
            jwt = JwtIssuer.getJwt(claims);
        } catch (JoseException e) {
            logger.error("JoseException:", e);
        }
        return NioUtils.toByteBuffer(jwt);
    }
}
