
package net.lightapi.portal.user.command.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.config.Config;
import com.networknt.eventuate.common.AggregateRepository;
import com.networknt.eventuate.common.EventuateAggregateStore;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.HashUtil;
import com.networknt.utility.NioUtils;
import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserRepository;
import net.lightapi.portal.user.UserService;
import net.lightapi.portal.user.UserServiceImpl;
import net.lightapi.portal.user.domain.UserAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ServiceHandler(id="lightapi.net/user/createUser/0.1.0")
public class CreateUser implements Handler {
    static final Logger logger = LoggerFactory.getLogger(CreateUser.class);
    static final String CREATE_USER_EXECUTION_EXCEPTION = "ERR11603";
    static final String CREATE_USER_EXCEPTION = "ERR11604";
    static final String USER_EMAIL_IS_USED = "ERR11611";
    static final String USER_ID_IS_USED = "ERR11612";
    static final String PASSWORD_NOT_MATCH = "ERR11613";
    static final String ERROR_HASHING_PASSWORD = "ERR11614";

    private EventuateAggregateStore eventStore  = (EventuateAggregateStore) SingletonServiceFactory.getBean(EventuateAggregateStore.class);
    private AggregateRepository repository = new AggregateRepository(UserAggregate.class, eventStore);
    private UserService service = new UserServiceImpl(repository);
    private UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        Map<String, Object> inputMap = (Map<String, Object>)input;
        if(logger.isDebugEnabled()) logger.debug("input = " + inputMap);

        // ensure that email is not used.
        String email = (String)inputMap.get("email");
        String user = userQueryRepository.getUserByEmail(email);
        if(user != null) {
            // this email address is used already.
            return NioUtils.toByteBuffer(getStatus(exchange, USER_EMAIL_IS_USED, email));
        }
        // ensure that userId is not used.
        String userId = (String)inputMap.get("userId");
        user = userQueryRepository.getUserByUserId(userId);
        if(user != null) {
            // this userId is used already.
            return NioUtils.toByteBuffer(getStatus(exchange, USER_ID_IS_USED, userId));
        }
        // check password and passwordConfirm match and create hash/salt it.
        String password = (String)inputMap.get("password");
        String passwordConfirm = (String)inputMap.get("passwordConfirm");
        if(password.equals(passwordConfirm)) {
            try {
                String hashedPass = HashUtil.generateStrongPasswordHash(password);
                inputMap.put("password", hashedPass);
                inputMap.remove("passwordConfirm");
                inputMap.put("activated", false);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                logger.error("NoSuchAlgorithmException", e);
                return NioUtils.toByteBuffer(getStatus(exchange, ERROR_HASHING_PASSWORD, e.getMessage()));
            }

        } else {
            return NioUtils.toByteBuffer(getStatus(exchange, PASSWORD_NOT_MATCH, userId));
        }
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
