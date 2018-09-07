package net.lightapi.portal.user.common.domain;



import net.lightapi.portal.user.common.domain.contact.ContactData;

import java.util.Locale;
import java.util.TimeZone;


public class UserDto {

  private String screenName;

  private ContactData contactData = new ContactData();

  private String timezone = TimeZone.getDefault().getDisplayName();
  private String locale = Locale.CANADA.getDisplayName();

  private String password;
  private String host;


  private String id;
  private boolean emailChange;
  private boolean passwordReset;
  private boolean screenNameChange;

  public UserDto(){}

  public UserDto(String email, String screenName) {
    this.contactData.setEmail(email);
    this.screenName = screenName;
  }


  public String getScreenName() {
    return screenName;
  }

  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  public ContactData getContactData() {
    return contactData;
  }

  public void setContactData(ContactData contactData) {
    this.contactData = contactData;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public boolean isEmailChange() {
    return emailChange;
  }

  public void setEmailChange(boolean emailChange) {
    this.emailChange = emailChange;
  }

  public boolean isPasswordReset() {
    return passwordReset;
  }

  public void setPasswordReset(boolean passwordReset) {
    this.passwordReset = passwordReset;
  }

  public boolean isScreenNameChange() {
    return screenNameChange;
  }

  public void setScreenNameChange(boolean screenNameChange) {
    this.screenNameChange = screenNameChange;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
