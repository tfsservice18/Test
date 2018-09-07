
package net.lightapi.portal.user.query.handler;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/user/getUserByUserId/0.1.0")
public class GetUserByUserId implements Handler {
    static final Logger logger = LoggerFactory.getLogger(GetUserByUserId.class);
    static final String USER_NOT_FOUND_BY_USERID = "ERR11609";
    UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String userId = ((Map<String, String>)input).get("userId");
        if(logger.isDebugEnabled()) logger.debug("userId = " + userId);
        String result = userQueryRepository.getUserByUserId(userId);
        if(result == null) {
            return NioUtils.toByteBuffer(getStatus(exchange, USER_NOT_FOUND_BY_USERID, userId));
        } else {
            return NioUtils.toByteBuffer(result);
        }
    }
}
