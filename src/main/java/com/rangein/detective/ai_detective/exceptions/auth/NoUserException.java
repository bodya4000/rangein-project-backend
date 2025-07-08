package com.rangein.detective.ai_detective.exceptions.auth;

public class NoUserException extends RuntimeException {
  public NoUserException() {
    super("No such user!");
  }
}
