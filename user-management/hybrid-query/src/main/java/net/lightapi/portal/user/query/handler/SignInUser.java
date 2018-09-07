
package net.lightapi.portal.user.query.handler;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
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
            return NioUtils.toByteBuffer("");
        }
    }
}
