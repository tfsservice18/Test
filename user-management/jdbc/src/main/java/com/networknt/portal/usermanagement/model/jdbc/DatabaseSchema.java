package com.networknt.portal.usermanagement.model.jdbc;

import com.networknt.utility.StringUtils;

public class DatabaseSchema {
  public static final String DEFAULT_SCHEMA = "user";
  public static final String EMPTY_SCHEMA = "none";

  private final String userDatabaseSchema;

  public DatabaseSchema() {
    userDatabaseSchema = DEFAULT_SCHEMA;
  }

  public DatabaseSchema(String userDatabaseSchema) {
    this.userDatabaseSchema = StringUtils.isNullOrEmpty(userDatabaseSchema) ? DEFAULT_SCHEMA : userDatabaseSchema;
  }

  public String getEventuateDatabaseSchema() {
    return userDatabaseSchema;
  }

  public boolean isEmpty() {
    return EMPTY_SCHEMA.equals(userDatabaseSchema);
  }

  public boolean isDefault() {
    return DEFAULT_SCHEMA.equals(userDatabaseSchema);
  }

  public String qualifyTable(String table) {
    if (isEmpty()) return table;

    String schema = isDefault() ? DEFAULT_SCHEMA : userDatabaseSchema;

    return String.format("%s.%s", schema, table);
  }
}
