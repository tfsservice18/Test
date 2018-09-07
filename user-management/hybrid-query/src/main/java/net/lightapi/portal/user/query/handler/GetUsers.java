
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

@ServiceHandler(id="lightapi.net/user/getUsers/0.1.0")
public class GetUsers implements Handler {
    static final Logger logger = LoggerFactory.getLogger(GetUsers.class);
    UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        int offset = ((Map<String, Integer>)input).get("offset");
        int limit = ((Map<String, Integer>)input).get("limit");
        if(limit == 0) limit = 10;
        if(logger.isDebugEnabled()) logger.debug("offset = " + offset + " limit = " + limit);
        return NioUtils.toByteBuffer(userQueryRepository.getUser(offset, limit));
    }
}
