package com.networknt.portal.usermanagement.model.jdbc.user;


import com.networknt.portal.usermanagement.model.common.domain.contact.AddressData;
import com.networknt.portal.usermanagement.model.common.domain.contact.AddressType;
import com.networknt.portal.usermanagement.model.common.domain.contact.Country;
import com.networknt.portal.usermanagement.model.common.domain.contact.State;
import com.networknt.portal.usermanagement.model.common.exception.NoSuchUserException;
import com.networknt.portal.usermanagement.model.common.model.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * UserRepository implement class
 */
public class UserRepositoryImpl implements UserRepository {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {this.dataSource = dataSource;}

   public void delete(Long userId) throws NoSuchUserException {
       Objects.requireNonNull(userId);
       try (final Connection connection = dataSource.getConnection()){
           String psDelete = "UPDATE USER_DETAIL SET deleted = 'Y' WHERE user_id = ?";
           PreparedStatement psEntity = connection.prepareStatement(psDelete);
           psEntity.setLong(1, userId);
           int count = psEntity.executeUpdate();
           if (count != 1) {
               logger.error("Failed to update USER_DETAIL: {}", userId);
           }

       } catch (SQLException e) {
           logger.error("SqlException:", e);
       }

   }


    public Optional<User> findById(Long userId) {
        Objects.requireNonNull(userId);
        User user = null;
        String psSelect = "SELECT user_id, email, timezone, screenName, firstName, lastName, gender, birthday, passwordHash, passwordSalt  FROM USER_DETAIL WHERE deleted = 'N' AND user_id = ?";
        String psSelect_address = "SELECT  address_type, country, province_state, city, zipcode, addressline1, addressline2  FROM ADDRESS WHERE  user_id = ?";
        String psSelect_token = "SELECT  id, token_value, token_type, valid, payload, expiresAt, usedAt  FROM CONFIRMATION_TOKEN WHERE  user_id = ? and expiresAt > ?";
        try (final Connection connection = dataSource.getConnection()){
            PreparedStatement stmt = connection.prepareStatement(psSelect);
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs == null || rs.getFetchSize() > 1) {
                logger.error("incorrect fetch result {}", userId);
            } else {
                while (rs.next()) {
                    user = new User(userId, rs.getString("screenName"), rs.getString("email") );
                    user.getContactData().setFirstName(rs.getString("firstName"));
                    user.getContactData().setLastName(rs.getString("lastName"));
                    user.setPassword(new Password(rs.getString("passwordHash"), rs.getString("passwordHash")));

                }
                if (user!=null) {
                    stmt =  connection.prepareStatement(psSelect_address);
                    stmt.setLong(1, userId);
                    ResultSet rs2 = stmt.executeQuery();
                    if (rs2!=null) {
                        while (rs2.next()) {
                            AddressData address = new AddressData();
                            address.setAddressType(AddressType.valueOf(rs2.getString("address_type")));
                            address.setState(rs2.getString("province_state")==null?null:State.valueOf(rs2.getString("province_state")));
                            address.setCountry(rs2.getString("country")==null?null: Country.valueOf(rs2.getString("country")));
                            address.setCity(rs2.getString("city"));
                            address.setZipCode(rs2.getString("zipcode"));
                            address.setAddressLine1(rs2.getString("addressline1"));
                            address.setAddressLine2(rs2.getString("addressline2"));
                            user.getContactData().addAddresses(address);
                        }
                    }

                    stmt =  connection.prepareStatement(psSelect_token);
                    stmt.setLong(1, userId);
                    stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    rs2 = stmt.executeQuery();
                    if (rs2!=null) {
                        while (rs2.next()) {
                            Duration  duration = Duration.between(rs2.getTimestamp("expiresAt").toLocalDateTime(),LocalDateTime.now());
                            ConfirmationToken token = new ConfirmationToken(user, rs2.getString("token_value"), ConfirmationTokenType.valueOf(rs2.getString("token_type")),
                                    (int) duration.toMinutes());
                            token.setId(rs2.getLong("id"));
                            token.setValid("Y".equalsIgnoreCase(rs2.getString("valid"))?true:false);
                            user.addConfirmationToken(token);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SqlException:", e);
        }

        if (user == null) {
            return Optional.empty();
        }
       return Optional.of(user);
    }


   public Optional<User> findByEmail(String email) {
       Objects.requireNonNull(email);
       User user = null;
       String psSelect = "SELECT user_id, email, timezone, screenName, firstName, lastName, gender, birthday, passwordHash, passwordSalt  FROM USER_DETAIL WHERE deleted = 'N' AND email = ?";
       String psSelect_address = "SELECT  address_type, country, province_state, city, zipcode, addressline1, addressline2  FROM ADDRESS WHERE  user_id = ?";
       String psSelect_token = "SELECT  id, token_value, token_type, valid, payload, expiresAt, usedAt  FROM CONFIRMATION_TOKEN WHERE  user_id = ? and expiresAt > ?";
       try (final Connection connection = dataSource.getConnection()){
           PreparedStatement stmt = connection.prepareStatement(psSelect);
           stmt.setString(1, email);
           ResultSet rs = stmt.executeQuery();
           if (rs == null || rs.getFetchSize() > 1) {
               logger.error("incorrect fetch result {}", email);
           } else {
               while (rs.next()) {
                   user = new User(rs.getLong("user_id"), rs.getString("screenName"), rs.getString("email") );
                   user.getContactData().setFirstName(rs.getString("firstName"));
                   user.getContactData().setLastName(rs.getString("lastName"));
                   user.setPassword(new Password(rs.getString("passwordHash"), rs.getString("passwordHash")));

               }
               if (user!=null) {
                   stmt =  connection.prepareStatement(psSelect_address);
                   stmt.setLong(1, user.getId());
                   ResultSet rs2 = stmt.executeQuery();
                   if (rs2!=null) {
                       while (rs2.next()) {
                           AddressData address = new AddressData();
                           address.setAddressType(AddressType.valueOf(rs2.getString("address_type")));
                           address.setState(rs2.getString("province_state")==null?null:State.valueOf(rs2.getString("province_state")));
                           address.setCountry(rs2.getString("country")==null?null: Country.valueOf(rs2.getString("country")));
                           address.setCity(rs2.getString("city"));
                           address.setZipCode(rs2.getString("zipcode"));
                           address.setAddressLine1(rs2.getString("addressline1"));
                           address.setAddressLine2(rs2.getString("addressline2"));
                           user.getContactData().addAddresses(address);
                       }
                   }

                   stmt =  connection.prepareStatement(psSelect_token);
                   stmt.setLong(1, user.getId());
                   stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                   rs2 = stmt.executeQuery();
                   if (rs2!=null) {
                       while (rs2.next()) {
                           Duration  duration = Duration.between(rs2.getTimestamp("expiresAt").toLocalDateTime(),LocalDateTime.now());
                           ConfirmationToken token = new ConfirmationToken(user, rs2.getString("token_value"), ConfirmationTokenType.valueOf(rs2.getString("token_type")),
                                   (int) duration.toMinutes());
                           token.setId(rs2.getLong("id"));
                           token.setValid("Y".equalsIgnoreCase(rs2.getString("valid"))?true:false);
                           user.addConfirmationToken(token);
                       }
                   }
               }
           }
       } catch (SQLException e) {
           logger.error("SqlException:", e);
       }

       if (user == null) {
           return Optional.empty();
       }
       return Optional.of(user);
   }

    @Override
   public  Optional<User> findByScreenName(String screenName) {
       Objects.requireNonNull(screenName);
       User user = null;
       String psSelect = "SELECT user_id, email, timezone, screenName, firstName, lastName, gender, birthday, passwordHash, passwordSalt  FROM USER_DETAIL WHERE deleted = 'N' AND screenName = ?";
       String psSelect_address = "SELECT  address_type, country, province_state, city, zipcode, addressline1, addressline2  FROM ADDRESS WHERE  user_id = ?";
        String psSelect_token = "SELECT  id, token_value, token_type, valid, payload, expiresAt, usedAt  FROM CONFIRMATION_TOKEN WHERE  user_id = ? and expiresAt > ?";
       try (final Connection connection = dataSource.getConnection()){
           PreparedStatement stmt = connection.prepareStatement(psSelect);
           stmt.setString(1, screenName);
           ResultSet rs = stmt.executeQuery();
           if (rs == null || rs.getFetchSize() > 1) {
               logger.error("incorrect fetch result {}", screenName);
           } else {
               while (rs.next()) {
                   user = new User(rs.getLong("user_id"), rs.getString("screenName"), rs.getString("email") );
                   user.getContactData().setFirstName(rs.getString("firstName"));
                   user.getContactData().setLastName(rs.getString("lastName"));
                   user.setPassword(new Password(rs.getString("passwordHash"), rs.getString("passwordHash")));

               }
               if (user!=null) {
                   stmt =  connection.prepareStatement(psSelect_address);
                   stmt.setLong(1, user.getId());
                   ResultSet rs2 = stmt.executeQuery();
                   if (rs2!=null) {
                       while (rs2.next()) {
                           AddressData address = new AddressData();
                           address.setAddressType(AddressType.valueOf(rs2.getString("address_type")));
                           address.setState(rs2.getString("province_state")==null?null:State.valueOf(rs2.getString("province_state")));
                           address.setCountry(rs2.getString("country")==null?null: Country.valueOf(rs2.getString("country")));
                           address.setCity(rs2.getString("city"));
                           address.setZipCode(rs2.getString("zipcode"));
                           address.setAddressLine1(rs2.getString("addressline1"));
                           address.setAddressLine2(rs2.getString("addressline2"));
                           user.getContactData().addAddresses(address);
                       }
                   }
                   stmt =  connection.prepareStatement(psSelect_token);
                   stmt.setLong(1, user.getId());
                   stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                   rs2 = stmt.executeQuery();
                   if (rs2!=null) {
                       while (rs2.next()) {
                           Duration  duration = Duration.between(rs2.getTimestamp("expiresAt").toLocalDateTime(),LocalDateTime.now());
                           ConfirmationToken token = new ConfirmationToken(user, rs2.getString("token_value"), ConfirmationTokenType.valueOf(rs2.getString("token_type")),
                                   (int) duration.toMinutes());
                           token.setId(rs2.getLong("id"));
                           token.setValid("Y".equalsIgnoreCase(rs2.getString("valid"))?true:false);
                           user.addConfirmationToken(token);
                       }
                   }

               }
           }
       } catch (SQLException e) {
           logger.error("SqlException:", e);
       }

       if (user == null) {
           return Optional.empty();
       }
       return Optional.of(user);
   }

   @Override
   public  User save(User user) {

       Objects.requireNonNull(user);

       String psInsert = "INSERT INTO USER_DETAIL (user_id, email, timezone, screenName, firstName, lastName, gender, birthday, passwordHash," +
               "  passwordSalt, locale, confirmed, locked, deleted, createBy,  createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?)";
       String psInsert_address = "INSERT INTO ADDRESS (user_id, address_type, country, province_state, city, zipcode, addressline1, addressline2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
       String psInsert_token = "INSERT INTO CONFIRMATION_TOKEN (id, user_id, token_value, token_type, payload, expiresAt, usedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

       try (final Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
           PreparedStatement stmt = connection.prepareStatement(psInsert);
           stmt.setLong(1, user.getId());
           stmt.setString(2, user.getEmail());
           stmt.setString(3, user.getTimezoneName());
           stmt.setString(4, user.getScreenName());
           stmt.setString(5, user.getContactData().getFirstName());
           stmt.setString(6, user.getContactData().getLastName());
           stmt.setString(7, user.getContactData().getGender().name());
           stmt.setDate(8, user.getContactData().getBirthday()==null?null:Date.valueOf(user.getContactData().getBirthday()));
           stmt.setString(9, user.getPassword() == null ? null : user.getPassword().getPasswordHash());
           stmt.setString(10, user.getPassword() == null ? null : user.getPassword().getPasswordSalt());
           stmt.setString(11, user.getLocale().getDisplayName());
           stmt.setString(12, user.isConfirmed() ? "Y" : "N");
           stmt.setString(13, user.isLocked() ? "Y" : "N");
           stmt.setString(14, user.isDeleted() ? "Y" : "N");
           stmt.setLong(15, user.getAuditData()==null?0:user.getAuditData().getCreatedBy().getId());
           stmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
           int count = stmt.executeUpdate();
           if (user.getContactData().getAddresses()!=null && user.getContactData().getAddresses().size()>0) {
               try (PreparedStatement psAddress = connection.prepareStatement(psInsert_address)) {
                   for(AddressData address : user.getContactData().getAddresses()) {
                       psAddress.setLong(1,  user.getId());
                       psAddress.setString(2, address.getAddressType().name());
                       psAddress.setString(3, address.getCountry()==null? null:address.getCountry().name() );
                       psAddress.setString(4, address.getState()==null? null: address.getState().name());
                       psAddress.setString(5, address.getCity());
                       psAddress.setString(6,  address.getZipCode());
                       psAddress.setString(7,  address.getAddressLine1());
                       psAddress.setString(8,  address.getAddressLine2());
                       psAddress.addBatch();
                   }
                   psAddress.executeBatch();
               }
           }
           if (user.getConfirmationTokens()!=null && user.getConfirmationTokens().size() >0) {
               try (PreparedStatement psToken = connection.prepareStatement(psInsert_token)) {
                   for(ConfirmationToken token : user.getConfirmationTokens()) {
                       psToken.setLong(1,  token.getId());
                       psToken.setLong(2,  user.getId());
                       psToken.setString(3, token.getValue());
                       psToken.setString(4, token.getType().name());
                       psToken.setString(5, token.getPayload() == null?null:token.getPayload().toString());
                       psToken.setTimestamp(6, token.getExpiresAt()== null?null:Timestamp.valueOf(token.getExpiresAt()));
                       psToken.setTimestamp(7, token.getUsedAt()== null?null:Timestamp.valueOf(token.getUsedAt()));

                       psToken.addBatch();
                   }
                   psToken.executeBatch();
               }
           }
           connection.commit();

           if (count != 1) {
               logger.error("Failed to insert USER_DETAIL: {}", user.getId());
           } else {
               return user;
           }
       } catch (SQLException e) {
           logger.error("SqlException:", e);
       }

       return null;
   }

    @Override
    public  User update(User user) {
        Objects.requireNonNull(user);
        try (final Connection connection = dataSource.getConnection()){
            String psUpdate = "UPDATE USER_DETAIL SET screenName = ?, email = ?,  firstName = ?, lastName= ?, birthday = ?, gender=?, passwordHash =?, passwordSalt = ?, locked = ?, confirmed = ? WHERE user_id = ?";
            PreparedStatement psEntity = connection.prepareStatement(psUpdate);
            psEntity.setString(1, user.getScreenName());
            psEntity.setString(2, user.getEmail());
            psEntity.setString(3, user.getContactData().getFirstName());
            psEntity.setString(4, user.getContactData().getLastName());
            psEntity.setDate(5, user.getContactData().getBirthday()==null?null:Date.valueOf(user.getContactData().getBirthday()));
            psEntity.setString(6, user.getContactData().getGender().name());
            psEntity.setString(7, user.getPassword() == null ? null : user.getPassword().getPasswordHash());
            psEntity.setString(8, user.getPassword() == null ? null : user.getPassword().getPasswordSalt());
            psEntity.setString(9, user.isConfirmed() ? "Y" : "N");
            psEntity.setString(10, user.isLocked() ? "Y" : "N");
            psEntity.setLong(11, user.getId());
            int count = psEntity.executeUpdate();
            if (count != 1) {
                logger.error("Failed to update USER_DETAIL: {}", user);
            } else {
                return user;
            }

        } catch (SQLException e) {
            logger.error("SqlException:", e);
        }
        return null;
    }

    public void activeUser(Long userId) throws NoSuchUserException {
        Objects.requireNonNull(userId);
        try (final Connection connection = dataSource.getConnection()){
            String psDelete = "UPDATE USER_DETAIL SET locked = 'N', confirmed = 'Y' WHERE user_id = ?";
            PreparedStatement psEntity = connection.prepareStatement(psDelete);
            psEntity.setLong(1, userId);
            int count = psEntity.executeUpdate();
            if (count != 1) {
                logger.error("Failed to update USER_DETAIL: {}", userId);
            }

        } catch (SQLException e) {
            logger.error("SqlException:", e);
        }

    }


}



