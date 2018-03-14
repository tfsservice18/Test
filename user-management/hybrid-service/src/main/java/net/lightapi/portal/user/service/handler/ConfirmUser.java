
package net.lightapi.portal.user.service.handler;

import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.exception.InvalidTokenException;
import com.networknt.portal.usermanagement.model.common.exception.NoSuchUserException;
import com.networknt.portal.usermanagement.model.common.model.user.User;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Map;

@ServiceHandler(id="lightapi.net/user/confirmUser/0.1.0")
public class ConfirmUser implements Handler {
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(Object input)  {
        String tokenId = ((Map<String, String>)input).get("tokenId");
        String result = null;
        try {
            User user = service.confirmEmail(tokenId);
            if (user == null) {
                result = "no user active by the token";
            } else {
                result = "user active: " + user.getScreenName();
            }
        } catch(NoSuchUserException e) {
            result = "no Such User";
        } catch(InvalidTokenException e) {
                result = "Invalid Token";
        }

        return NioUtils.toByteBuffer(result);
    }
}
