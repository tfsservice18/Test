package net.lightapi.portal.user;

public interface UserRepository {

    void setDataSource(Object dataSource);

    Object getDataSource();

    String getUser(int offset, int limit);

    String getUserByEmail(String email);

    String getUserByUserId(String userId);

    String getUserByEntityId(String entityId);

    void createUser(String entityId, String data);

    void updateUser(String entityId, String data);

    void removeUser(String entityId);

}
