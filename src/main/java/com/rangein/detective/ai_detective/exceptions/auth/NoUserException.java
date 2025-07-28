package com.rangein.detective.ai_detective.exceptions.auth;

import com.rangein.detective.ai_detective.exceptions.BaseException;

public class NoUserException extends BaseException {

  public NoUserException() {
    super(404, "No user found");
  }
}
