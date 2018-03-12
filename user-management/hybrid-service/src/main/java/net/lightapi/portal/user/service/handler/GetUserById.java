
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.nio.ByteBuffer;
import java.util.Optional;

@ServiceHandler(id="lightapi.net/user/getUserById/0.1.0")
public class GetUserById implements Handler {
    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(Object input)  {
        JsonNode inputPara = Config.getInstance().getMapper().valueToTree(input);
        String id = inputPara.findPath("id").asText();
        System.out.println(" user id:" + id);

        Optional<User> user = service.findUser(id);
        String result = null;
        try {
            if (user.isPresent()) {
                result = Config.getInstance().getMapper().writeValueAsString(service.toUserDto(user.get()));
            } else {
                result = "No user find for the Id:" + id;
            }
        } catch (Exception e) {
            result = e.getMessage();
            //TODO handler Exception, add log info?
        }
        System.out.println("result:" + result);
        return NioUtils.toByteBuffer(result);
    }
}
