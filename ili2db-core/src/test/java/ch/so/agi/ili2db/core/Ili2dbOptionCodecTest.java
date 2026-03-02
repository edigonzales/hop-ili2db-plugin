package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class Ili2dbOptionCodecTest {

  @Test
  void shouldRoundTripOptionEntries() {
    List<Ili2dbOptionEntry> entries =
        List.of(
            new Ili2dbOptionEntry("proxy", true, "host.local"),
            new Ili2dbOptionEntry("proxyPort", true, "8080"),
            new Ili2dbOptionEntry("createGeomIdx", false, ""));

    String encoded = Ili2dbOptionCodec.encode(entries);
    List<Ili2dbOptionEntry> decoded = Ili2dbOptionCodec.decode(encoded);

    assertEquals(entries, decoded);
  }
}
