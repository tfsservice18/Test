
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ByteBuffer handle(Object input)  {

        ObjectMapper mapper = new ObjectMapper();

        String result = "Ok!";
        try {
            String json = mapper.writeValueAsString(input);
            System.out.println("hybrid input:" + json);
            UserDto userDto = mapper.readValue(json, UserDto.class);
           //userDto.getContactData().getAddresses().forEach(e->System.out.println(e.getCountry().name()));
            User user = service.fromUserDto(userDto);
            service.signup(user, userDto.getPassword(), false);

            //TODO remove the following implemetation after confirm email implemented
            Optional<ConfirmationToken> token = user.getConfirmationTokens().stream().findFirst();
            if (token.isPresent()) {
                result = "http://localhost:8080/v1/user/token/" + token.get().getId();
            }

        } catch (Exception e) {
            result = e.getMessage();
            //TODO handler Exception, add log info?
        }

        return NioUtils.toByteBuffer(result);
    }
}
