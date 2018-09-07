package net.lightapi.portal.user.command.handler;

import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EventuateAggregateStore;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserService;
import net.lightapi.portal.user.UserServiceImpl;
import net.lightapi.portal.user.domain.UserAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ServiceHandler(id="lightapi.net/user/confirmUser/0.1.0")
public class ConfirmUser implements Handler {
    static final Logger logger = LoggerFactory.getLogger(ConfirmUser.class);

    private EventuateAggregateStore eventStore  = (EventuateAggregateStore) SingletonServiceFactory.getBean(EventuateAggregateStore.class);
    private AggregateRepository repository = new AggregateRepository(UserAggregate.class, eventStore);
    private UserService service = new UserServiceImpl(repository);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        if(logger.isDebugEnabled()) logger.debug("input = " + input);
        String tokenId = ((Map<String, String>)input).get("tokenId");
        String id = ((Map<String, String>)input).get("id");
        try {
            CompletableFuture<String> result =  service.confirm(id, tokenId).thenApply((e) -> {
                String s = e.getAggregate().getUser();
                return s;
            });
            return NioUtils.toByteBuffer(result.get());
        } catch (Exception e) {
            logger.error("Error converting map to json", e);
            // TODO return error code
            return NioUtils.toByteBuffer("error");
        }
    }
}
