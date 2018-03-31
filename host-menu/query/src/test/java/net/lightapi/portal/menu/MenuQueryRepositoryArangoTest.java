package net.lightapi.portal.menu;

import com.arangodb.ArangoDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MenuQueryRepositoryArangoTest {
    static ArangoDB arangoDB;
    static MenuRepository menuQueryRepository = SingletonServiceFactory.getBean(MenuRepository.class);
    ObjectMapper mapper = Config.getInstance().getMapper();
    @BeforeClass
    public static void setUp() {
        arangoDB = (ArangoDB)menuQueryRepository.getDataSource();
        // add testing data
    }

    @AfterClass
    public static void tearDown() {
        // remove testing data
        menuQueryRepository.removeMenu("e1");
        menuQueryRepository.removeMenu("e2");
        menuQueryRepository.removeMenuItem("e11");
        menuQueryRepository.removeMenuItem("e12");
        menuQueryRepository.removeMenuItem("e13");
        menuQueryRepository.removeMenuItem("e1");
        menuQueryRepository.removeMenuItem("e2");
        menuQueryRepository.removeMenuItem("e3");
    }

    @Test
    public void testHost() {

        String s11 = "{\"menuItemId\":\"11\",\"label\":\"Host Admin\",\"route\":\"/admin/hostAdmin\",\"roles\":[\"admin\",\"owner\"]}";
        String s12 = "{\"menuItemId\":\"12\",\"label\":\"Rule Admin\",\"route\":\"/admin/ruleAdmin\",\"roles\":[\"ruleAdmin\",\"admin\",\"owner\"]}";
        String s13 = "{\"menuItemId\":\"13\",\"label\":\"Form Admin\",\"route\":\"/admin/formAdmin\",\"roles\":[\"formAdmin\",\"admin\",\"owner\"]}";
        String s1 = "{\"menuItemId\":\"1\",\"label\":\"admin\",\"route\":\"/admin\",\"roles\":[\"admin\",\"owner\"],\"contains\":[\"11\",\"12\",\"13\"]}";
        String s2 = "{\"menuItemId\":\"2\",\"label\":\"login\",\"route\":\"/login\",\"roles\":[\"user\"]}";
        String s3 = "{\"menuItemId\":\"3\",\"label\":\"logout\",\"route\":\"/logout\",\"roles\":[\"user\"]}";

        menuQueryRepository.createMenuItem("e11", s11);
        menuQueryRepository.createMenuItem("e12", s12);
        menuQueryRepository.createMenuItem("e13", s13);
        menuQueryRepository.createMenuItem("e1", s1);
        menuQueryRepository.createMenuItem("e2", s2);
        menuQueryRepository.createMenuItem("e3", s3);

        String site2 = "{\"host\":\"example.org\",\"description\":\"example site\",\"contains\":[\"1\",\"2\",\"3\"]}";
        menuQueryRepository.createMenu("e2", site2);

        String result = menuQueryRepository.getMenuByHost("example.org");
        System.out.println("result = " + result);
        Assert.assertNotNull(result);

        menuQueryRepository.removeMenu("e2");
        site2 = "{\"host\":\"example.org\",\"description\":\"example site\",\"contains\":[\"1\",\"2\",\"3\"]}";
        menuQueryRepository.createMenu("e2", site2);
        String site2_new = "{\"host\":\"example.org\",\"description\":\"example site111\",\"contains\":[\"1\",\"5\",\"3\"]}";
        menuQueryRepository.updateMenu("e2", site2_new);
        System.out.println(menuQueryRepository.getMenu());
    }

}
