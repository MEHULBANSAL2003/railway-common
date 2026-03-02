package com.railway.common.enums;


public enum Role {
  USER,
  ADMIN,
  SUPER_ADMIN;

  public static boolean isValidRole(String role) {
    if (role == null) return false;

    try {
      Role.valueOf(role);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }
}
