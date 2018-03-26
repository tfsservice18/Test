
package net.lightapi.portal.user.service.handler;

import com.networknt.config.Config;
import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.model.user.User;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import io.undertow.server.HttpServerExchange;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

@ServiceHandler(id="lightapi.net/user/getUserByName/0.1.0")
public class GetUserByName implements Handler {
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String name = ((Map<String, String>)input).get("name");
        ResponseResult response = new ResponseResult();
        Optional<User> user = service.findUser(name);;
        String result = null;
        try {
            if (user.isPresent()) {
                result = Config.getInstance().getMapper().writeValueAsString(service.toUserDto(user.get()));
            } else {
                response.setMessage("No user find for the name:" + name);
                result = Config.getInstance().getMapper().writeValueAsString(response);
            }
        } catch (Exception e) {
            result = e.getMessage();
            //TODO handler Exception, add log info?
        }
        return NioUtils.toByteBuffer(result);
    }
}
