package net.lightapi.portal.form;

import com.arangodb.ArangoDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import net.lightapi.portal.form.FormRepository;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FormQueryRepositoryArangoTest {
    static ArangoDB arangoDB;
    static FormRepository formQueryRepository = SingletonServiceFactory.getBean(FormRepository.class);
    ObjectMapper mapper = Config.getInstance().getMapper();
    @BeforeClass
    public static void setUp() {
        arangoDB = (ArangoDB)formQueryRepository.getDataSource();
        // add testing data
    }

    @AfterClass
    public static void tearDown() {
        // remove testing data
    }

    @Test
    public void testForm() throws Exception {
        // clean up all vertexes if exist.

        formQueryRepository.removeForm("{\"formId\":\"2233-55\"}");

       // String site1 = "{\"formId\":\"d-55\",\"description\":\"example site\"}";
         String site1 = "{\"id\":\"2233-55\",\"formId\":\"2233-55\",\"description\":\"example site\"}";

        formQueryRepository.createForm( site1);

        String result = formQueryRepository.getFormByEntityId("2233-55");
        System.out.println("result = " + result);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetForm() {
        String result = formQueryRepository.getForm();
        System.out.println("result = " + result);
        Assert.assertNotNull(result);
    }

    @Test
    public void testCreateAnotherForm() {
        formQueryRepository.removeForm("{\"formId\":\"2233-555\"}");
        String site2 = "{\"id\":\"2233-555\",\"formId\":\"2233-555\", \"version\":\"1.0\",\"description\":\"User Signup Form\",\"action\":{\"host\":\"lightportal.org\",\"service\":\"form\",\"action\":\"signupForm\",\"version\":\"0.1.0\",\"method\":\"POST\"},\"schema\":{\"type\":\"FormSchema\",\"title\":\"Form Schema\",\"properties\":[{\"name\":\"firstName\",\"readonly\":false,\"type\":\"text\",\"title\":\"firstName_field\",\"maxItems\":1},{\"name\":\"secondName\",\"readonly\":false,\"type\":\"text\",\"title\":\"secondName\",\"maxItems\":1}]},\"formFields\":[{\"key\":\"firstName\",\"multiple\":false,\"type\":\"text\",\"rows\":\"1\"}, {\"key\":\"secondName\",\"multiple\":false,\"type\":\"test\",\"rows\":\"1\"}]}";
        formQueryRepository.createForm(site2);
        String result = formQueryRepository.getFormByEntityId("2233-555");
        Assert.assertNotNull(result);
        String result2 = formQueryRepository.getFormByEntityId("2233-556");
        Assert.assertNull(result2);
    }

    @Test
    public void testUpdateAndGetMenu() {
        formQueryRepository.removeForm("{\"formId\":\"2233-555\"}");
        String site2 = "{\"id\":\"2233-555\",\"formId\":\"2233-555\",\"description\":\"example site\"}";
        formQueryRepository.createForm( site2);
        String site2_new = "{\"formId\":\"2233-555\",\"description\":\"real site\"}";
        formQueryRepository.updateForm( site2_new);
        System.out.println(formQueryRepository.getForm());
    }

    @Test
    public void testRemove() {
        formQueryRepository.removeForm("{\"formId\":\"2233-55\"}");
        formQueryRepository.removeForm("{\"formId\":\"2233-555\"}");
    }
}
