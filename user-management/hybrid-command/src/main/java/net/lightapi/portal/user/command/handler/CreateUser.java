
package net.lightapi.portal.user.command.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.config.Config;
import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EventuateAggregateStore;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserService;
import net.lightapi.portal.user.UserServiceImpl;
import net.lightapi.portal.user.domain.UserAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ServiceHandler(id="lightapi.net/user/createUser/0.1.0")
public class CreateUser implements Handler {
    static final Logger logger = LoggerFactory.getLogger(CreateUser.class);
    static final String CREATE_USER_EXECUTION_EXCEPTION = "ERR11603";
    static final String CREATE_USER_EXCEPTION = "ERR11604";
    private EventuateAggregateStore eventStore  = (EventuateAggregateStore) SingletonServiceFactory.getBean(EventuateAggregateStore.class);
    private AggregateRepository repository = new AggregateRepository(UserAggregate.class, eventStore);
    private UserService service = new UserServiceImpl(repository);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        if(logger.isDebugEnabled()) logger.debug("input = " + input);

        try {
            CompletableFuture<String> result =  service.create(Config.getInstance().getMapper().writeValueAsString(input)).thenApply((e) -> {
                return e.getAggregate().getUser();
            });
            return NioUtils.toByteBuffer(result.get());
        } catch (Exception e) {
            String s = "";
            try {
                s = Config.getInstance().getMapper().writeValueAsString(input);
            } catch (JsonProcessingException jpe) {
                logger.error("JsonProcessingException", jpe);
            }
            logger.error("Exception with input " + s, e);
            if(e instanceof ExecutionException) {
                return NioUtils.toByteBuffer(getStatus(exchange, CREATE_USER_EXECUTION_EXCEPTION, e.getCause().getClass(), s));
            } else {
                // unknown exception handler them separately when known.
                return NioUtils.toByteBuffer(getStatus(exchange, CREATE_USER_EXCEPTION, e.getMessage(), s));
            }
        }
    }
}
