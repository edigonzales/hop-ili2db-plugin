package ch.so.agi.ili2db.core;

public class Ili2dbExecutionResult {

  private final boolean success;
  private final String message;
  private final Throwable error;

  private Ili2dbExecutionResult(boolean success, String message, Throwable error) {
    this.success = success;
    this.message = message;
    this.error = error;
  }

  public static Ili2dbExecutionResult ok(String message) {
    return new Ili2dbExecutionResult(true, message, null);
  }

  public static Ili2dbExecutionResult error(String message, Throwable error) {
    return new Ili2dbExecutionResult(false, message, error);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  public Throwable getError() {
    return error;
  }
}
