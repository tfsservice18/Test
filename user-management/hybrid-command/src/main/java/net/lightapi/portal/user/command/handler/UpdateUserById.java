
package net.lightapi.portal.user.command.handler;

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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ServiceHandler(id="lightapi.net/user/updateUser/0.1.0")
public class UpdateUserById implements Handler {
    static final Logger logger = LoggerFactory.getLogger(DeleteUserById.class);
    private EventuateAggregateStore eventStore  = (EventuateAggregateStore) SingletonServiceFactory.getBean(EventuateAggregateStore.class);
    private AggregateRepository repository = new AggregateRepository(UserAggregate.class, eventStore);
    private UserService service = new UserServiceImpl(repository);
    static final String UPDATE_USER_EXECUTION_EXCEPTION = "ERR11605";
    static final String UPDATE_USER_EXCEPTION = "ERR11606";

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        if(logger.isDebugEnabled()) logger.debug("input = " + input);
        String id = ((Map<String, String>)input).get("id");
        try {
            CompletableFuture<String> result =  service.update(id, Config.getInstance().getMapper().writeValueAsString(input)).thenApply((e) -> {
                return e.getAggregate().getUser();
            });
            return NioUtils.toByteBuffer(result.get());
        } catch (Exception e) {
            logger.error("Exception", e);
            if(e instanceof ExecutionException) {
                return NioUtils.toByteBuffer(getStatus(exchange, UPDATE_USER_EXECUTION_EXCEPTION, e.getCause().getClass(), id));
            } else {
                // unknown exception handler them separately when known.
                return NioUtils.toByteBuffer(getStatus(exchange, UPDATE_USER_EXCEPTION, e.getMessage(), id));
            }
        }
    }
}
