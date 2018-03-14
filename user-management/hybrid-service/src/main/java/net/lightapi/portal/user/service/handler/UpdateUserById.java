
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
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
import java.util.Map;
import java.util.Optional;

@ServiceHandler(id="lightapi.net/user/updateUser/0.1.0")
public class UpdateUserById implements Handler {

    static final Logger logger = LoggerFactory.getLogger(GetNewUser.class);
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);

    @Override
    public ByteBuffer handle(Object input)  {
        ObjectMapper mapper = new ObjectMapper();
        String id = ((Map<String, String>)input).get("id");
        String result;
        try {
            String json = mapper.writeValueAsString(input);
            System.out.println("hybrid input:" + json);
            UserDto user = mapper.readValue(json, UserDto.class);
            User userResult ;
            if (user.isEmailChange()) {
                userResult= service.changeEmail(id,  user.getContactData().getEmail());
            } else if (user.isPasswordReset()) {
                userResult= service.changePassword(id, user.getPassword());
            } else if (user.isScreenNameChange()) {
                userResult= service.changeScreenName(id, user.getScreenName());
            } else {
                userResult =  service.fromUserDto(user);
                userResult.setId(id);
                userResult = service.update(userResult);
            }
            if (userResult == null) {
                result = "no user changed;";
            } else {
                result =  Config.getInstance().getMapper().writeValueAsString(service.toUserDto(userResult));
            }

        } catch (Exception e) {
            result = e.getMessage();
            //TODO handler Exception, add log info?
        }

        return NioUtils.toByteBuffer(result);
    }
}
