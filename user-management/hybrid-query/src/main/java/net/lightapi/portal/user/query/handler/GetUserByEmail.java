package net.lightapi.portal.user.query.handler;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

@ServiceHandler(id="lightapi.net/user/getUserByEmail/0.1.0")
public class GetUserByEmail implements Handler {
    static final Logger logger = LoggerFactory.getLogger(GetUserByEmail.class);
    static final String USER_NOT_FOUND_BY_EMAIL = "ERR11607";
    UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String email = ((Map<String, String>)input).get("email");
        if(logger.isDebugEnabled()) logger.debug("email = " + email);
        String result = userQueryRepository.getUserByEmail(email);
        if(result == null) {
            return NioUtils.toByteBuffer(getStatus(exchange, USER_NOT_FOUND_BY_EMAIL, email));
        } else {
            return NioUtils.toByteBuffer(result);
        }
    }
}
