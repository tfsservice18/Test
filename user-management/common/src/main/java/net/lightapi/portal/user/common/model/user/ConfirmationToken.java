/**
 * Copyright (c) 2017-present Laszlo Csontos All rights reserved.
 *
 * This file is part of springuni-particles.
 *
 * springuni-particles is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * springuni-particles is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with springuni-particles.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.lightapi.portal.user.common.model.user;


import net.lightapi.portal.user.common.domain.AuditData;
import net.lightapi.portal.user.common.domain.Entity;
import net.lightapi.portal.user.common.utils.IdGenerator;
import net.lightapi.portal.user.common.utils.IdGeneratorImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.time.Clock.systemUTC;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Confirmation token functions as a one-time password for being able to perform operations like
 * email change and password reset.
 */

public class ConfirmationToken<P> implements Entity<String, ConfirmationToken<P>> {

  public static final int DEFAULT_EXPIRATION_MINUTES = 10;

  private static IdGenerator idGenerator = new IdGeneratorImpl();
  private String id;

  private User owner;
  private ConfirmationTokenType type;

  private boolean valid = true;
  private LocalDateTime expiresAt;
  private LocalDateTime usedAt;

  private P payload;

  private AuditData<User> auditData;

  /**
   * Creates a new confirmation token with the given {@link User} and expiration period.
   *
   * @param owner a {@link User}
   * @param type confirmation token's type
   */
  public ConfirmationToken(User owner, ConfirmationTokenType type) {
    // FIXME: Use a bit more sophisticated random token value generaton later
    this(owner, type, DEFAULT_EXPIRATION_MINUTES);
  }

  /**
   * Creates a new confirmation token with the given {@link User} and expiration period.
   *
   * @param owner a {@link User}
   * @param type confirmation token's type
   * @param minutes expiration in minutes
   */
  public ConfirmationToken(User owner, ConfirmationTokenType type, int minutes) {
    // FIXME: Use a bit more sophisticated random token value generaton later
    this(owner, idGenerator.genId().asString(), type, minutes);
  }

  /**
   * Creates a new confirmation token with the given {@link User} and expiration period.
   *
   * @param owner a {@link User}
   * @param value tokens's value
   * @param type confirmation token's type
   */
  public ConfirmationToken(User owner, String value, ConfirmationTokenType type) {
    this(owner, value, type, DEFAULT_EXPIRATION_MINUTES, null);
  }

  /**
   * Creates a new confirmation token with the given {@link User} and expiration period.
   *
   * @param owner a {@link User}
   * @param id tokens's value
   * @param type confirmation token's type
   * @param minutes expiration in minutes
   */
  public ConfirmationToken(User owner, String id, ConfirmationTokenType type, int minutes) {
    this(owner, id, type, minutes, null);
  }

  /**
   * Creates a new confirmation token with the given {@link User} and expiration period.
   *
   * @param owner a {@link User}
   * @param id tokens's value
   * @param type confirmation token's type
   * @param minutes expiration in minutes
   * @param payload payload
   */
  public ConfirmationToken(
          User owner, String id, ConfirmationTokenType type, int minutes, P payload) {

    this.id = id;
    this.owner = owner;
    this.type = type;
    this.payload = payload;

    expiresAt = LocalDateTime.now(systemUTC()).plus(minutes, MINUTES);
  }

  @Override
  public String getId() {
    return id;
  }

  /**
   * Gets the token's payload if any.
   *
   * @return token's payload.
   */
  public Optional<P> getPayload() {
    return Optional.ofNullable(payload);
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean sameIdentityAs(ConfirmationToken<P> other) {
    return equals(other);
  }

  /**
   * Use the confirmation token.
   *
   * @return this confirmation token.
   */
  public ConfirmationToken use() {
    valid = false;
    usedAt = LocalDateTime.now(systemUTC());
    return this;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public ConfirmationTokenType getType() {
    return type;
  }

  public void setType(ConfirmationTokenType type) {
    this.type = type;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public LocalDateTime getUsedAt() {
    return usedAt;
  }
}
