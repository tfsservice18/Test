
package com.networknt.portal.usermanagement.restquery.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.handler.LightHttpHandler;
import com.networknt.portal.usermanagement.model.auth.service.UserService;
import com.networknt.portal.usermanagement.model.auth.service.UserServiceImpl;
import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.exception.InvalidTokenException;
import com.networknt.portal.usermanagement.model.common.exception.NoSuchUserException;
import com.networknt.portal.usermanagement.model.common.model.user.User;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.portal.usermanagement.restquery.model.LoginForm;
import com.networknt.service.SingletonServiceFactory;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

public class UserLoginPutHandler implements LightHttpHandler {

    private UserRepository userRepository = (UserRepository) SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);
    private UserService service = new UserServiceImpl(passwordSecurity, null, userRepository);
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {


        ObjectMapper mapper = new ObjectMapper();

        Map s = (Map)exchange.getAttachment(BodyHandler.REQUEST_BODY);
        String json = mapper.writeValueAsString(s);
        LoginForm login = mapper.readValue(json, LoginForm.class);
        User userResult = null;
        try {

            if (login.getToken() != null ) {
                userResult= service.confirmPasswordReset(login.getNameOrEmail(), login.getToken());
            }
            userResult =  service.login(login.getNameOrEmail(), login.getPassword());
        } catch (NoSuchUserException e) {
            //TODO handler excption, add log info?
        } catch (InvalidTokenException e) {
            //TODO handler excption, add log info?
        }


        String result;
        if (userResult == null) {
            result = "Login failed, please re-try or contact to admin;";
        } else {
            result =  "Login successfully: \n" + service.toUserDto(userResult);
            //TODO get session???
        }

        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(result));
        //    exchange.endExchange();
    }
}
