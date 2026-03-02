package ch.so.agi.ili2db.core;

@FunctionalInterface
public interface Ili2dbExternalLogSink {
  void log(Ili2dbExternalLogLevel level, String message, Throwable throwable);
}
