package ch.so.agi.ili2db.hop.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.so.agi.ili2db.core.Ili2dbExecutionRequest;
import ch.so.agi.ili2db.core.Ili2dbExecutionResult;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.apache.hop.core.IRowSet;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.IRowHandler;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.junit.jupiter.api.Test;

class Ili2dbGpkgTargetModeTest {

  @Test
  void shouldUseFieldBasedGpkgTargetPerRow() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgTargetMode("FIELD");
    meta.setGpkgFileField("gpkg");

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new ValueMetaString("gpkg"));

    TestRowHandler rowHandler =
        new TestRowHandler(
            new Object[] {"target/first.gpkg"},
            new Object[] {"target/second.gpkg"});
    List<Ili2dbExecutionRequest> requests = new ArrayList<>();

    Ili2db transform = createTransform(meta, inputRowMeta, rowHandler);
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor =
        request -> {
          requests.add(request);
          return Ili2dbExecutionResult.ok("ok");
        };

    assertTrue(transform.processRow());
    assertTrue(transform.processRow());
    assertFalse(transform.processRow());

    assertEquals(2, requests.size());
    assertEquals(new File("target/first.gpkg").getAbsolutePath(), requests.get(0).getGpkgFile());
    assertEquals(new File("target/second.gpkg").getAbsolutePath(), requests.get(1).getGpkgFile());
    assertEquals(
        new File("target/first.gpkg").getAbsolutePath(),
        getOutputValue(rowHandler, 0, "ili2db_target_file"));
    assertEquals(
        new File("target/second.gpkg").getAbsolutePath(),
        getOutputValue(rowHandler, 1, "ili2db_target_file"));
  }

  @Test
  void shouldKeepStaticGpkgTargetBehavior() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgFilePath("target/static.gpkg");

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new ValueMetaString("dummy"));

    TestRowHandler rowHandler = new TestRowHandler(new Object[] {"value"});
    List<Ili2dbExecutionRequest> requests = new ArrayList<>();

    Ili2db transform = createTransform(meta, inputRowMeta, rowHandler);
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor =
        request -> {
          requests.add(request);
          return Ili2dbExecutionResult.ok("ok");
        };

    assertTrue(transform.processRow());
    assertFalse(transform.processRow());

    assertEquals(1, requests.size());
    assertEquals(new File("target/static.gpkg").getAbsolutePath(), requests.get(0).getGpkgFile());
    assertEquals(
        new File("target/static.gpkg").getAbsolutePath(),
        getOutputValue(rowHandler, 0, "ili2db_target_file"));
  }

  @Test
  void shouldRequireInputMetadataForGpkgFieldMode() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgTargetMode("FIELD");
    meta.setGpkgFileField("gpkg");

    Ili2db transform = createTransform(meta, null, new TestRowHandler());

    HopTransformException exception =
        assertThrows(HopTransformException.class, () -> initializeTransform(transform));

    assertEquals(
        BaseMessages.getString(Ili2dbMeta.class, "Ili2db.Transform.NoInputForGpkgField"),
        exception.getMessage().trim());
  }

  @Test
  void shouldRequireExistingGpkgField() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgTargetMode("FIELD");
    meta.setGpkgFileField("gpkg");

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new ValueMetaString("other"));

    Ili2db transform = createTransform(meta, inputRowMeta, new TestRowHandler());

    HopTransformException exception =
        assertThrows(HopTransformException.class, () -> initializeTransform(transform));

    assertEquals(
        BaseMessages.getString(Ili2dbMeta.class, "Ili2db.Transform.GpkgFieldNotFound", "gpkg"),
        exception.getMessage().trim());
  }

  @Test
  void shouldFailWhenGpkgFieldCannotBeRead() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgTargetMode("FIELD");
    meta.setGpkgFileField("gpkg");

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new FailingValueMetaString("gpkg"));

    Ili2db transform = createTransform(meta, inputRowMeta, new TestRowHandler(new Object[] {"ignored"}));
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor = request -> Ili2dbExecutionResult.ok("ok");

    HopTransformException exception =
        assertThrows(HopTransformException.class, transform::processRow);

    assertTrue(
        exception
            .getMessage()
            .contains(
                BaseMessages.getString(
                    Ili2dbMeta.class, "Ili2db.Transform.GpkgFieldReadError", "gpkg")));
  }

  @Test
  void shouldContinueOnExecutorErrorWhenFailOnErrorIsDisabled() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgTargetMode("FIELD");
    meta.setGpkgFileField("gpkg");
    meta.setFailOnError(false);

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new ValueMetaString("gpkg"));

    TestRowHandler rowHandler = new TestRowHandler(new Object[] {"target/error.gpkg"});
    Ili2db transform = createTransform(meta, inputRowMeta, rowHandler);
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor =
        request -> Ili2dbExecutionResult.error("import failed", new RuntimeException("boom"));

    assertTrue(transform.processRow());
    assertFalse(transform.processRow());

    assertEquals(Boolean.FALSE, getOutputValue(rowHandler, 0, "ili2db_success"));
    assertEquals("import failed", getOutputValue(rowHandler, 0, "ili2db_message"));
  }

  @Test
  void shouldPassThroughStaticIlidataImportSource() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setImportFilePath("ilidata:ch.so.agi.mopublic");

    List<Ili2dbExecutionRequest> requests = new ArrayList<>();
    Ili2db transform = createTransform(meta, null, new TestRowHandler());
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor =
        request -> {
          requests.add(request);
          return Ili2dbExecutionResult.ok("ok");
        };

    assertTrue(transform.processRow());
    assertFalse(transform.processRow());

    assertEquals(1, requests.size());
    assertEquals("ilidata:ch.so.agi.mopublic", requests.get(0).getImportFile());
  }

  @Test
  void shouldPassThroughFieldBasedIlidataImportSource() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setImportSourceMode("FIELD");
    meta.setImportFileField("import_source");

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new ValueMetaString("import_source"));

    TestRowHandler rowHandler = new TestRowHandler(new Object[] {"ilidata:ch.so.agi.mopublic"});
    List<Ili2dbExecutionRequest> requests = new ArrayList<>();

    Ili2db transform = createTransform(meta, inputRowMeta, rowHandler);
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor =
        request -> {
          requests.add(request);
          return Ili2dbExecutionResult.ok("ok");
        };

    assertTrue(transform.processRow());
    assertFalse(transform.processRow());

    assertEquals(1, requests.size());
    assertEquals("ilidata:ch.so.agi.mopublic", requests.get(0).getImportFile());
  }

  @Test
  void shouldStopOnExecutorErrorWhenFailOnErrorIsEnabled() throws Exception {
    Ili2dbMeta meta = createDefaultMeta();
    meta.setGpkgTargetMode("FIELD");
    meta.setGpkgFileField("gpkg");
    meta.setFailOnError(true);

    RowMeta inputRowMeta = new RowMeta();
    inputRowMeta.addValueMeta(new ValueMetaString("gpkg"));

    TestRowHandler rowHandler = new TestRowHandler(new Object[] {"target/error.gpkg"});
    Ili2db transform = createTransform(meta, inputRowMeta, rowHandler);
    initializeTransform(transform);
    ((Ili2dbData) transform.getData()).executor =
        request -> Ili2dbExecutionResult.error("import failed", new RuntimeException("boom"));

    HopTransformException exception =
        assertThrows(HopTransformException.class, transform::processRow);

    assertTrue(exception.getMessage().contains("import failed"));
  }

  private static Ili2dbMeta createDefaultMeta() {
    Ili2dbMeta meta = new Ili2dbMeta();
    meta.setDefault();
    meta.setFlavor("ILI2GPKG");
    meta.setFunction("IMPORT");
    meta.setImportSourceMode("STATIC_PATH");
    meta.setImportFilePath("data/input.xtf");
    meta.setGpkgFilePath("target/default.gpkg");
    return meta;
  }

  private static Ili2db createTransform(
      Ili2dbMeta meta, IRowMeta inputRowMeta, IRowHandler rowHandler) {
    TransformMeta transformMeta = new TransformMeta("ili2db", meta);
    PipelineMeta pipelineMeta = new PipelineMeta();
    pipelineMeta.addTransform(transformMeta);

    Ili2db transform =
        new QuietIli2db(transformMeta, meta, new Ili2dbData(), 0, pipelineMeta, null);
    transform.setInputRowMeta(inputRowMeta);
    transform.setRowHandler(rowHandler);
    return transform;
  }

  private static void initializeTransform(Ili2db transform) throws Exception {
    Method method = Ili2db.class.getDeclaredMethod("initializeData");
    method.setAccessible(true);
    try {
      method.invoke(transform);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Exception exception) {
        throw exception;
      }
      throw e;
    }
  }

  private static Object getOutputValue(TestRowHandler rowHandler, int rowIndex, String fieldName) {
    IRowMeta rowMeta = rowHandler.outputRowMetas.get(rowIndex);
    int fieldIndex = rowMeta.indexOfValue(fieldName);
    return rowHandler.outputRows.get(rowIndex)[fieldIndex];
  }

  private static final class TestRowHandler implements IRowHandler {
    private final Deque<Object[]> inputRows = new ArrayDeque<>();
    private final List<Object[]> outputRows = new ArrayList<>();
    private final List<IRowMeta> outputRowMetas = new ArrayList<>();

    private TestRowHandler(Object[]... rows) {
      for (Object[] row : rows) {
        inputRows.addLast(row);
      }
    }

    @Override
    public Object[] getRow() throws HopException {
      return inputRows.pollFirst();
    }

    @Override
    public void putRow(IRowMeta rowMeta, Object[] row) throws HopTransformException {
      outputRowMetas.add(rowMeta);
      outputRows.add(row.clone());
    }

    @Override
    public void putError(
        IRowMeta rowMeta,
        Object[] row,
        long nrErrors,
        String errorDescriptions,
        String fieldNames,
        String errorCodes)
        throws HopTransformException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void putRowTo(IRowMeta rowMeta, Object[] row, IRowSet rowSet)
        throws HopTransformException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object[] getRowFrom(IRowSet rowSet) throws HopTransformException {
      throw new UnsupportedOperationException();
    }
  }

  private static final class FailingValueMetaString extends ValueMetaString {
    private FailingValueMetaString(String name) {
      super(name);
    }

    @Override
    public String getString(Object object) throws HopValueException {
      throw new HopValueException("boom");
    }
  }

  private static final class QuietIli2db extends Ili2db {
    private QuietIli2db(
        TransformMeta transformMeta,
        Ili2dbMeta meta,
        Ili2dbData data,
        int copyNr,
        PipelineMeta pipelineMeta,
        org.apache.hop.pipeline.Pipeline pipeline) {
      super(transformMeta, meta, data, copyNr, pipelineMeta, pipeline);
    }

    @Override
    public boolean isBasic() {
      return false;
    }
  }
}
