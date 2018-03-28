
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.eventuate.jdbc.IdGeneratorImpl;
import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.domain.UserDto;
import com.networknt.portal.usermanagement.model.common.model.user.ConfirmationToken;
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
import java.util.Optional;

@ServiceHandler(id="lightapi.net/user/createUser/0.1.0")
public class GetNewUser implements Handler {
    static final Logger logger = LoggerFactory.getLogger(GetNewUser.class);
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {


        ObjectMapper mapper = new ObjectMapper();

        ResponseResult response = new ResponseResult();
        String result = "[Ok!]";
        try {
            String json = mapper.writeValueAsString(input);

            UserDto userDto = mapper.readValue(json, UserDto.class);
            IdGenerator idGenerator = new IdGeneratorImpl();
            User user = service.fromUserDto(userDto, idGenerator.genId().asString());
            System.out.println("user:" + user.getScreenName());
            service.signup(user, userDto.getPassword(), false);

            response.setCompleted(true);
            response.setMessage("created User:"  + user.getScreenName());
            result = Config.getInstance().getMapper().writeValueAsString(response);

        } catch (Exception e) {
            result = e.getMessage();
        }

        return NioUtils.toByteBuffer(result);
    }
}
