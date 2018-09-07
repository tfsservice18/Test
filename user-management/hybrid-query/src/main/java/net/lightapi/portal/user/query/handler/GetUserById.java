
package net.lightapi.portal.user.query.handler;

import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

@ServiceHandler(id="lightapi.net/user/getUserById/0.1.0")
public class GetUserById implements Handler {
    static final Logger logger = LoggerFactory.getLogger(GetUserById.class);
    static final String USER_NOT_FOUND_BY_ID = "ERR11608";
    UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String id = ((Map<String, String>)input).get("id");
        if(logger.isDebugEnabled()) logger.debug("id = " + id);
        String result = userQueryRepository.getUserByEntityId(id);
        if(result == null) {
            return NioUtils.toByteBuffer(getStatus(exchange, USER_NOT_FOUND_BY_ID, id));
        } else {
            return NioUtils.toByteBuffer(result);
        }
    }
}
