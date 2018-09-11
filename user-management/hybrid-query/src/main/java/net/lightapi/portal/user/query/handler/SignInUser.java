
package net.lightapi.portal.user.query.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.HashUtil;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.networknt.utility.StringUtils;
import io.undertow.server.HttpServerExchange;
import net.lightapi.portal.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This SignIn API will be called by the OAuth 2.0 provider to ensure the credential
 * is correct for the userId or email within a contact of host. Multiple hosts is supported
 * in the light-portal so that different site can utilize the light-portal API to manage their
 * users.
 *
 * @author Steve Hu
 */
@ServiceHandler(id="lightapi.net/user/signInUser/0.1.0")
public class SignInUser implements Handler {
    static final Logger logger = LoggerFactory.getLogger(SignInUser.class);
    static final String USER_NOT_FOUND_BY_USERIDEMAIL = "ERR11610";
    static final String ERROR_PARSING_USER = "ERR11615";
    static final String INVALID_SIGNIN_PASSWORD = "ERR11616";
    static final String USER_NOT_ACTIVATED = "ERR11617";

    UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        String userIdEmail = ((Map<String, String>)input).get("userIdEmail");
        String inputPassword = ((Map<String, String>)input).get("password");
        String user = null;
        if(StringUtils.isEmail(userIdEmail)) {
            // use email index to get the user object
            user = userQueryRepository.getUserByEmail(userIdEmail);
        } else {
            // use userId index to get the user object
            user = userQueryRepository.getUserByUserId(userIdEmail);
        }
        if(user == null) {
            return NioUtils.toByteBuffer(getStatus(exchange, USER_NOT_FOUND_BY_USERIDEMAIL, userIdEmail));
        } else {
            // check password match
            try {
                Map<String, Object> userMap = Config.getInstance().getMapper().readValue(user, new TypeReference<Map<String, Object>>(){});
                String hashedPassword = (String)userMap.get("password");
                boolean match = HashUtil.validatePassword(inputPassword.toCharArray(), hashedPassword);
                if(match) {
                    // check if the user is activated
                    Boolean activated = (Boolean)userMap.get("activated");
                    if(activated == null || activated == false) {
                        return NioUtils.toByteBuffer(getStatus(exchange, USER_NOT_ACTIVATED, userMap.get("userId")));
                    }
                    // constructed a new user object
                    Map<String, Object> result = new HashMap<>();
                    result.put("userId", userMap.get("userId"));
                    result.put("email", userMap.get("email"));
                    if(userMap.get("firstName") != null) result.put("firstName", userMap.get("firstName"));
                    if(userMap.get("lastName") != null) result.put("lastName", userMap.get("lastName"));
                    if(userMap.get("roles") != null) result.put("roles", userMap.get("roles"));
                    return NioUtils.toByteBuffer(Config.getInstance().getMapper().writeValueAsString(result));
                } else {
                    return NioUtils.toByteBuffer(getStatus(exchange, INVALID_SIGNIN_PASSWORD));
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                return NioUtils.toByteBuffer(getStatus(exchange, ERROR_PARSING_USER, e.getMessage()));
            }
        }
    }
}
