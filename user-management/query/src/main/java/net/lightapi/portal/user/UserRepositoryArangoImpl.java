package net.lightapi.portal.user;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.*;
import com.arangodb.model.HashIndexOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.arangodb.velocypack.module.jdk8.VPackJdk8Module;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
import net.lightapi.portal.config.ArangoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserRepositoryArangoImpl implements UserRepository {
    static final String CONFIG_NAME = "arango";

    public static final String DBNAME = "user";
    public static final String USER = "user";
    public static final String ENTITYID = "entityId";
    public static final String EMAIL = "email";
    public static final String USERID = "userId";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ArangoConfig config = (ArangoConfig)Config.getInstance().getJsonObjectConfig(CONFIG_NAME, ArangoConfig.class);
    private ArangoDB arangoDB;
    private ArangoDatabase db;
    private ObjectMapper mapper = Config.getInstance().getMapper();

    public UserRepositoryArangoImpl() {
        arangoDB = new ArangoDB.Builder()
                .host(config.getHost(), config.getPort())
                .user(config.getUser())
                .password(config.getPassword())
                .registerModule(new VPackJdk8Module())
                .build();
        // if first time connect to the arango, create menu database.
        if(!arangoDB.getDatabases().contains(DBNAME)) {
            arangoDB.createDatabase(DBNAME);
            db = arangoDB.db(DBNAME);
            CollectionEntity userCollection = db.createCollection(USER);
            // add unique index on email
            final Collection<String> fields = new ArrayList<>();
            fields.add(EMAIL);
            HashIndexOptions options = new HashIndexOptions();
            options.unique(true);
            db.collection(USER).createHashIndex(fields, options);
            // add unique index on userId
            fields.remove(EMAIL);
            fields.add(USERID);
            db.collection(USER).createHashIndex(fields, options);
        }
        if(db == null) db = arangoDB.db(DBNAME);
    }

    @Override
    public void setDataSource(Object object) {
        this.arangoDB = (ArangoDB)object;
    }

    @Override
    public Object getDataSource() {
        return arangoDB;
    }

    @Override
    public String getUser(int offset, int limit) {
        String result = null;
        List<Map<String, Object>> list = new ArrayList<>();
        final String query = "FOR u IN user LIMIT @offset, @limit RETURN u";
        final Map<String, Object> bindVars = new MapBuilder().put("offset", offset).put("limit", limit).get();
        final ArangoCursor<Map> cursor = db.query(query, bindVars, null, Map.class);
        for (; cursor.hasNext();) {
            final Map map = cursor.next();
            list.add(map);
        }
        try {
            result = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException:", e);
        }
        return result;
    }

    @Override
    public String getUserByEmail(String email) {
        String result = null;
        final String query = "FOR u IN user FILTER u.email == @email RETURN u";
        final Map<String, Object> bindVars = new MapBuilder().put("email", email).get();
        final ArangoCursor<Map> cursor = db.query(query, bindVars, null, Map.class);
        if(cursor.hasNext()) {
            final Map map = cursor.next();
            try {
                result = mapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                logger.error("JsonProcessingException:", e);
            }
        }
        return result;
    }

    @Override
    public String getUserByUserId(String userId) {
        String result = null;
        final String query = "FOR u IN user FILTER u.userId == @userId RETURN u";
        final Map<String, Object> bindVars = new MapBuilder().put("userId", userId).get();
        final ArangoCursor<Map> cursor = db.query(query, bindVars, null, Map.class);
        if(cursor.hasNext()) {
            final Map map = cursor.next();
            try {
                result = mapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                logger.error("JsonProcessingException:", e);
            }
        }
        return result;
    }

    @Override
    public String getUserByEntityId(String entityId) {
        String result = null;
        try {
            BaseDocument myDocument = db.collection(USER).getDocument(entityId,
                    BaseDocument.class);
            if (myDocument!=null) {
                result =  mapper.writeValueAsString(myDocument.getProperties());
            }
        } catch (ArangoDBException e) {
            logger.error("ArangoDBException:", e);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException:", e);
        }
        return result;
    }

    @Override
    public void createUser(String entityId, String data) {
        try {
            Map<String, Object> map = mapper.readValue(data, new TypeReference<Map<String, Object>>() {});
            final BaseDocument bd = new BaseDocument();
            bd.setKey(entityId);
            bd.setProperties(map);
            bd.addAttribute(ENTITYID, entityId);
            final DocumentCreateEntity<BaseDocument> doc = db.collection(USER).insertDocument(bd);
        } catch(IOException e) {
            logger.error("IOException:", e);
        } catch(ArangoDBException e) {
            logger.error("ArangoDBException:", e);
            // there is a chance that the same userId or email will be saved multiple time due to the query
            // side update latency or the query side service id down. If this happens, then an email will be
            // sent out the user to let him/her know that the registration is failed. As ArangoDBException is
            // RuntimeException, we have to swagger it here. Otherwise, the message in events db won't be
            // consumed and it will keep popping up.
            // TODO send email here.
            logger.info("An email is sending to the user...");
        }
    }

    @Override
    public void updateUser(String entityId, String data) {
        try {
            Map<String, Object> map = mapper.readValue(data, new TypeReference<Map<String, Object>>() {});
            final BaseDocument bd = new BaseDocument();
            //  bd.setKey(entityId);
            bd.setProperties(map);
            bd.addAttribute(ENTITYID, entityId);
            db.collection(USER).updateDocument(entityId, bd);
        } catch (ArangoDBException e) {
            logger.error("ArangoDBException:", e);
        } catch(IOException e) {
            logger.error("IOException:", e);
        }
    }

    @Override
    public void removeUser(String entityId) {
        // entityId should be uniquely indexed.
        if (getUserByEntityId(entityId) != null) {
            try {
                db.collection(USER).deleteDocument(entityId);
            } catch (ArangoDBException e) {
                logger.error("ArangoDBException:", e);
            }
        }
    }

}
