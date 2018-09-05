
package com.networknt.portal.usermanagement.model.auth.service;



import com.networknt.portal.usermanagement.model.common.exception.NoSuchSessionException;
import com.networknt.portal.usermanagement.model.common.model.session.Session;
import com.networknt.portal.usermanagement.model.common.model.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * {@link SessionService} takes care of issuing new sessions and checking existing ones.
 */
public interface SessionService {

  /**
   * Creates a {@link Session} for the given {@link User}.
   *
   * @param sessionId a {@link Session}'s ID
   * @param userId a {@link User}'s ID
   * @param token user session token
   * @return a newly created session
   */
  Session createSession(long sessionId, long userId, String token);

  /**
   * Creates a {@link Session} for the given {@link User}, which is valid for {@code minutes}.
   *
   * @param sessionId a {@link Session}'s ID
   * @param userId a {@link User}'s ID
   * @param token user session token
   * @param minutes minutes to expire from now
   * @return a newly created session
   */
  Session createSession(long sessionId, long userId, String token, int minutes);

  /**
   * Gets the {@link Session} for the given ID.
   * @param id session ID
   * @return a {@link Session} if it exists.
   */
  Optional<Session> findSession(Long id);

  /**
   * Gets the {@link Session} for the given ID.
   * @param id session ID
   * @return a {@link Session} if it exists.
   * @throws NoSuchSessionException when there is no such session for the given ID
   */
  Session getSession(Long id) throws NoSuchSessionException;

  /**
   * Logs the user out by invalidating all of its {@link Session}s.
   *
   * @param userId User ID
   */
  void logoutUser(Long userId);

  /**
   * Updates the {@link Session} for the given ID.
   * @param id session ID
   * @param value string value of the session
   * @param lastUsedAt session last use timestamp
   * @throws NoSuchSessionException when there is no such session for the given ID
   */
  void useSession(Long id, String value, LocalDateTime lastUsedAt)
      throws NoSuchSessionException;

}
