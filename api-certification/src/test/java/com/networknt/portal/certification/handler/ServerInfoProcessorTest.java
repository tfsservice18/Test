package com.networknt.portal.certification.handler;

import com.networknt.config.Config;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ServerInfoProcessorTest {
    /**
     * This test needs petstore from light-example-4j/rest/swagger/petstore to be up and running.
     * @throws Exception
     */
    //@Test
    public void testProcess() throws Exception {
        Map<String, Object> map = ServerInfoRetriever.retrieve("https://localhost:8443", "/v2/server/info", null);
        List list = ServerInfoProcessor.process(map);
        Assert.assertTrue(list.size() > 0);
        System.out.println("issue list = " + Config.getInstance().getMapper().writeValueAsString(list));
    }
}
