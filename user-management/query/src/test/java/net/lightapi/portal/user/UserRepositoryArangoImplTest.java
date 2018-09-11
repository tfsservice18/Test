package net.lightapi.portal.user;

import com.arangodb.ArangoDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepositoryArangoImplTest {
    static final Logger logger = LoggerFactory.getLogger(UserRepositoryArangoImplTest.class);
    static ArangoDB arangoDB;
    static UserRepository userQueryRepository = SingletonServiceFactory.getBean(UserRepository.class);
    ObjectMapper mapper = Config.getInstance().getMapper();
    @BeforeClass
    public static void setUp() {
        arangoDB = (ArangoDB)userQueryRepository.getDataSource();
        // add testing data

    }

    @AfterClass
    public static void tearDown() {
        // remove testing data
        userQueryRepository.removeUser("u1");
        userQueryRepository.removeUser("u2");
        userQueryRepository.removeUser("u3");
        userQueryRepository.removeUser("u4");
    }

    @Test
    public void testUser() {
        String u1 = "{\"host\":\"example.org\",\"userId\":\"stevehu\",\"email\":\"stevehu@example.org\",\"password\":\"123456\",\"passwordConfirm\":\"123456\",\"firstName\":\"Steve\",\"lastName\":\"Hu\"}";
        String u2 = "{\"host\":\"example.org\",\"userId\":\"gavinchen\",\"email\":\"gavinchen@example.org\",\"password\":\"123456\",\"passwordConfirm\":\"123456\",\"firstName\":\"Gavin\",\"lastName\":\"Chen\"}";
        String u3 = "{\"host\":\"example.org\",\"userId\":\"dandobrin\",\"email\":\"dandobrin@example.org\",\"password\":\"123456\",\"passwordConfirm\":\"123456\",\"firstName\":\"Dan\",\"lastName\":\"Dobrin\"}";
        String u4 = "{\"host\":\"example.org\",\"userId\":\"nicholasazar\",\"email\":\"nicholasazar@example.org\",\"password\":\"123456\",\"passwordConfirm\":\"123456\",\"firstName\":\"Nicholas\",\"lastName\":\"Azar\"}";

        // create two users
        userQueryRepository.createUser("u1", u1);
        userQueryRepository.createUser("u2", u2);
        userQueryRepository.createUser("u3", u3);
        userQueryRepository.createUser("u4", u4);

        String firstTwo = userQueryRepository.getUser(0, 2);
        logger.debug("firstTwo = " + firstTwo);
        String secondTwo = userQueryRepository.getUser(2, 2);
        logger.debug("secondTwo = " + secondTwo);
        String all = userQueryRepository.getUser(0, 10);
        logger.debug("all = " + all);

        u1 = userQueryRepository.getUserByEntityId("u1");
        logger.debug("u1 by entityId = " + u1);
        u2 = userQueryRepository.getUserByEntityId("u2");
        logger.debug("u2 by entityId = " + u2);

        u1 = userQueryRepository.getUserByEmail("stevehu@example.org");
        logger.debug("u1 by email = " + u1);
        u2 = userQueryRepository.getUserByUserId("gavinchen");
        logger.debug("u2 by userId= " + u2);

        // update one of the users
        //u1 = "";
        //userQueryRepository.updateUser("u1", u1);

        // get the updated user
        //u1 = userQueryRepository.getUserByEntityId("u1");
        //logger.debug("u1 = " + u1);

        // get all users


    }

    /**
     * create a permanent user for subsequent test cases. This user only be removed by the tearDown.
     * And this method should be commented out all the time as it will fail the second time your run it.
     */
    //@Test
    public void testPermanentUser() {
        String u0 = "{\"host\":\"example.org\",\"userId\":\"test\",\"email\":\"test@example.org\",\"password\":\"123456\",\"passwordConfirm\":\"123456\",\"firstName\":\"Steve\",\"lastName\":\"Hu\"}";
        userQueryRepository.createUser("u0", u0);
    }
}
