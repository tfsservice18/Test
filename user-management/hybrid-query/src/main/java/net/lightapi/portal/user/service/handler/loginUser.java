
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.domain.UserDto;
import com.networknt.portal.usermanagement.model.common.exception.InvalidTokenException;
import com.networknt.portal.usermanagement.model.common.exception.NoSuchUserException;
import com.networknt.portal.usermanagement.model.common.model.user.User;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

@ServiceHandler(id="lightapi.net/user/loginUser/0.1.0")
public class loginUser implements Handler {
    static final Logger logger = LoggerFactory.getLogger(loginUser.class);
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        ObjectMapper mapper = new ObjectMapper();

        User userResult;
        String result = "[]";
        ResponseResult response = new ResponseResult();
        try {

            String nameOrEmail = ((Map<String, String>)input).get("nameOrEmail");
            String password = ((Map<String, String>)input).get("password");
            String token = ((Map<String, String>)input).get("token");

            if (token != null ) {
                userResult= service.confirmPasswordReset(nameOrEmail, token);
            }
            userResult =  service.login(nameOrEmail, password);


            if (userResult == null) {
                response.setCompleted(false);
                response.setMessage("Login failed, please re-try or contact to admin;");
            } else {
                response.setCompleted(true);
                response.setMessage("Login successfully: \n" + mapper.writeValueAsString(service.toUserDto(userResult)));
            }
            result = Config.getInstance().getMapper().writeValueAsString(response);
        } catch (Exception e) {
            logger.error("login error:" + e);
        }

        return NioUtils.toByteBuffer(result);
    }
}
