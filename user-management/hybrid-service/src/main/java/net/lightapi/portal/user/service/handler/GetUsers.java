
package net.lightapi.portal.user.service.handler;

import com.networknt.config.Config;
import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.domain.UserDto;
import com.networknt.portal.usermanagement.model.common.model.user.User;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import io.undertow.server.HttpServerExchange;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@ServiceHandler(id="lightapi.net/user/getUsers/0.1.0")
public class GetUsers implements Handler {
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String result = "[]";
        List<User> users = service.getUser();
        List<UserDto> userList = users.stream().map(e-> service.toUserDto(e)).collect(Collectors.toList());
        try {
            result = Config.getInstance().getMapper().writeValueAsString(userList);
        } catch (Exception e) {
            result = e.getMessage();
            //TODO handler Exception, add log info?
        }
        return NioUtils.toByteBuffer(result);
    }
}
