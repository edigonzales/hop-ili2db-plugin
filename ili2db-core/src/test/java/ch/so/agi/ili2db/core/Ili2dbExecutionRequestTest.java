package ch.so.agi.ili2db.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Ili2dbExecutionRequestTest {

  @Test
  void shouldExposeConfiguredDefaults() {
    Ili2dbExecutionRequest request = new Ili2dbExecutionRequest();

    assertEquals("2056", request.getDefaultSrsCode());
    assertTrue(request.isStrokeArcs());
    assertTrue(request.isNameByTopic());
    assertFalse(request.isDisableValidation());
  }
}
