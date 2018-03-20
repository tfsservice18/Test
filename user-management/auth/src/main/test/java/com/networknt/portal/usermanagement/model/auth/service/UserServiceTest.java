package com.networknt.portal.usermanagement.model.auth.service;



import com.networknt.portal.usermanagement.model.common.crypto.PasswordSecurity;
import com.networknt.portal.usermanagement.model.common.domain.contact.AddressData;
import com.networknt.portal.usermanagement.model.common.domain.contact.AddressType;
import com.networknt.portal.usermanagement.model.common.domain.contact.Country;
import com.networknt.portal.usermanagement.model.common.domain.contact.Gender;
import com.networknt.portal.usermanagement.model.common.model.user.User;
import com.networknt.portal.usermanagement.model.common.model.user.UserRepository;
import com.networknt.portal.usermanagement.model.common.utils.IdentityGenerator;
import com.networknt.service.SingletonServiceFactory;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserServiceTest {




    private UserRepository userRepository = (UserRepository)SingletonServiceFactory.getBean(UserRepository.class);
    private static PasswordSecurity passwordSecurity = (PasswordSecurity)SingletonServiceFactory.getBean(PasswordSecurity.class);

     private UserService  service = new UserServiceImpl(passwordSecurity, null, userRepository);


    private static User user;
    private static  Long  id;
    @BeforeClass
    public static void setUp() {
        id = IdentityGenerator.generate();
        user = new User(String.valueOf(id), "testUser", "aaa.bbb@gmail.com");
    //    Password password = passwordSecurity.ecrypt("password");
     //   user.setPassword(password);
        user.getContactData().setFirstName("Google");
        user.getContactData().setLastName("Com");
        user.getContactData().setGender(Gender.UNKNOWN);
        user.getContactData().setBirthday(LocalDate.of(1999,1,1));

        AddressData address1 = new AddressData();
        address1.setAddressType(AddressType.BILLING);
        address1.setCountry(Country.CA);
        address1.setCity("Toronto");
        address1.setZipCode("L3G G5T");
        address1.setAddressLine1("111 toronto st");
        user.getContactData().addAddresses(address1);

    }

    @Test
    public void testSignup()  throws Exception {


    //   System.out.println(service.isEmitEvent());

     //   service.setEmitEvent(false);
    //    service.signup(user, "password", false);

    }
}
