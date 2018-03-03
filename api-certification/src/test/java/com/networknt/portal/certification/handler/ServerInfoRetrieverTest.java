package com.networknt.portal.certification.handler;

import com.networknt.config.Config;
import org.junit.Test;

import java.util.Map;

public class ServerInfoRetrieverTest {
    /**
     * This test needs petstore from light-example-4j/rest/swagger/petstore to be up and running.
     * @throws Exception
     */
    //@Test
    public void testRetrieveServerInfo() throws Exception {
        Map<String, Object> map = ServerInfoRetriever.retrieve("https://localhost:8443", "/v2/server/info", null);
        // make sure the the map is server info object instead of status object that indicates an error.
        System.out.println("map = " + Config.getInstance().getMapper().writeValueAsString(map));
    }
}
