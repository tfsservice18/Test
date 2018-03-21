
package net.lightapi.portal.user.service.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.config.Config;
import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;

@ServiceHandler(id="lightapi.net/user/deleteUserById/0.1.0")
public class DeleteUserById implements Handler {

    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public ByteBuffer handle(Object input)  {
        ResponseResult response = new ResponseResult();
        JsonNode inputPara = Config.getInstance().getMapper().valueToTree(input);

        String id = inputPara.findPath("id").asText();

        System.out.println("delete user id:" + id);

        int rec  = service.delete(id);
        String result = "[]";
        if (rec > 0) {
            response.setCompleted(true);
            response.setMessage("Deleted user: " + id);
        } else {
            response.setCompleted(false);
            response.setMessage("No Such User:" + id);
        }

        try {
            result = Config.getInstance().getMapper().writeValueAsString(response);
        } catch (Exception e) {
            //TODO
        }

        return NioUtils.toByteBuffer(result);
    }
}
