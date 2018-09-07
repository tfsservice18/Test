
package net.lightapi.portal.user.common.model.user;


import net.lightapi.portal.user.common.domain.ValueObject;

/**
 * Represents Users' password.
 */

public class Password implements ValueObject<Password> {

  private String passwordHash;
  private String passwordSalt;



  /**
   * Create a new password.
   *
   * @param passwordHash password hash
   * @param passwordSalt password salt
   */
  public Password(String passwordHash, String passwordSalt) {
    this.passwordHash = passwordHash;
    this.passwordSalt = passwordSalt;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getPasswordSalt() {
    return passwordSalt;
  }
  @Override
  public boolean sameValueAs(Password other) {
    return equals(other);
  }

}
