package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ehi.ili2db.gui.Config;
import java.util.List;
import org.junit.jupiter.api.Test;

class Ili2dbOptionApplierTest {

  @Test
  void shouldAcceptValidBooleanValue() {
    Config config = new Config();

    assertDoesNotThrow(
        () ->
            Ili2dbOptionApplier.apply(
                config,
                Ili2dbFlavor.ILI2PG,
                Ili2dbFunction.IMPORT,
                List.of(new Ili2dbOptionEntry("disableRounding", true, "true"))));
  }

  @Test
  void shouldRejectInvalidBooleanValue() {
    Config config = new Config();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            Ili2dbOptionApplier.apply(
                config,
                Ili2dbFlavor.ILI2PG,
                Ili2dbFunction.IMPORT,
                List.of(new Ili2dbOptionEntry("disableRounding", true, "maybe"))));
  }

  @Test
  void shouldRejectInvalidIntegerValue() {
    Config config = new Config();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            Ili2dbOptionApplier.apply(
                config,
                Ili2dbFlavor.ILI2PG,
                Ili2dbFunction.IMPORT,
                List.of(new Ili2dbOptionEntry("proxyPort", true, "abc"))));
  }
}
