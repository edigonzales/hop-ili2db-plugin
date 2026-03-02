package ch.so.agi.ili2db.hop.transform;

import ch.so.agi.ili2db.core.Ili2dbFlavor;
import ch.so.agi.ili2db.core.Ili2dbFunction;
import ch.so.agi.ili2db.core.Ili2dbOptionCatalog;
import ch.so.agi.ili2db.core.Ili2dbOptionCodec;
import ch.so.agi.ili2db.core.Ili2dbOptionDefinition;
import ch.so.agi.ili2db.core.Ili2dbOptionEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.ComboVar;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class Ili2dbDialog extends BaseTransformDialog {

  private static final Class<?> PKG = Ili2dbMeta.class;

  private final Ili2dbMeta input;

  private ComboVar wFlavor;
  private ComboVar wFunction;
  private MetaSelectionLine<DatabaseMeta> wConnection;
  private TextVar wGpkgFilePath;
  private Button wbGpkgFilePath;
  private TextVar wSchemaName;

  private ComboVar wImportSourceMode;
  private TextVar wImportFilePath;
  private Button wbImportFilePath;
  private ComboVar wImportFileField;

  private TextVar wModelName;
  private TextVar wModelDir;
  private TextVar wDefaultSrsCode;
  private Button wImplicitSchemaImport;
  private Button wStrokeArcs;
  private Button wNameByTopic;
  private Button wDisableValidation;
  private Button wFailOnError;

  private TableView wOptions;

  private ComboVar wDatasetMode;
  private TextVar wDatasetName;
  private ComboVar wDatasetField;

  private Text wOutputConnectionField;
  private Text wOutputTargetFileField;
  private Text wOutputDatabaseSchemaField;
  private Button wCreateLogFile;
  private TextVar wLogDirectory;
  private Button wbLogDirectory;
  private Text wOutputLogFilePathField;

  public Ili2dbDialog(
      Shell parent, IVariables variables, Ili2dbMeta transformMeta, PipelineMeta pipelineMeta) {
    super(parent, variables, transformMeta, pipelineMeta);
    this.input = transformMeta;
  }

  @Override
  public String open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
    shell.setMinimumSize(900, 680);
    shell.setSize(980, 760);
    PropsUi.setLook(shell);
    setShellImage(shell, input);

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = PropsUi.getFormMargin();
    formLayout.marginHeight = PropsUi.getFormMargin();
    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "Ili2dbDialog.Shell.Title"));
    normalizeStoredWindowSize();

    int margin = PropsUi.getMargin();

    wlTransformName = new Label(shell, SWT.RIGHT);
    wlTransformName.setText(BaseMessages.getString(PKG, "System.TransformName.Label"));
    wlTransformName.setToolTipText(BaseMessages.getString(PKG, "System.TransformName.Tooltip"));
    PropsUi.setLook(wlTransformName);
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment(0, 0);
    fdlTransformName.right = new FormAttachment(props.getMiddlePct(), -margin);
    fdlTransformName.top = new FormAttachment(0, margin);
    wlTransformName.setLayoutData(fdlTransformName);

    wTransformName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wTransformName.setText(transformName);
    PropsUi.setLook(wTransformName);
    wTransformName.addModifyListener(e -> input.setChanged());
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment(props.getMiddlePct(), 0);
    fdTransformName.right = new FormAttachment(100, 0);
    fdTransformName.top = new FormAttachment(0, margin);
    wTransformName.setLayoutData(fdTransformName);

    Button wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    Button wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

    setButtonPositions(new Button[] {wOk, wCancel}, margin, null);

    CTabFolder tabFolder = new CTabFolder(shell, SWT.BORDER);
    PropsUi.setLook(tabFolder);
    tabFolder.setSimple(false);
    tabFolder.setUnselectedCloseVisible(false);

    FormData fdTabs = new FormData();
    fdTabs.left = new FormAttachment(0, 0);
    fdTabs.top = new FormAttachment(wTransformName, margin * 2);
    fdTabs.right = new FormAttachment(100, 0);
    fdTabs.bottom = new FormAttachment(wOk, -margin * 2);
    tabFolder.setLayoutData(fdTabs);

    createMainTab(tabFolder);
    createOptionsTab(tabFolder);
    createDatasetTab(tabFolder);
    createOutputTab(tabFolder);
    tabFolder.setSelection(0);

    wOk.addListener(SWT.Selection, e -> ok());
    wCancel.addListener(SWT.Selection, e -> cancel());

    getData();
    enableDisableControls();
    input.setChanged(changed);

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return transformName;
  }

  private void createMainTab(CTabFolder tabFolder) {
    CTabItem mainTab = new CTabItem(tabFolder, SWT.NONE);
    mainTab.setText(BaseMessages.getString(PKG, "Ili2dbDialog.Tab.Main"));

    Composite mainComposite = new Composite(tabFolder, SWT.NONE);
    PropsUi.setLook(mainComposite);
    FormLayout layout = new FormLayout();
    layout.marginWidth = PropsUi.getFormMargin();
    layout.marginHeight = PropsUi.getFormMargin();
    mainComposite.setLayout(layout);

    Control lastControl = null;

    wFlavor = new ComboVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wFlavor.add("ILI2GPKG");
    wFlavor.add("ILI2PG");
    wFlavor.addModifyListener(
        e -> {
          input.setChanged();
          enableDisableControls();
          refreshOptionsTable();
        });
    placeControl(
        mainComposite, BaseMessages.getString(PKG, "Ili2dbDialog.Flavor.Label"), wFlavor, lastControl);
    lastControl = wFlavor;

    wFunction = new ComboVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wFunction.add("SCHEMA_IMPORT");
    wFunction.add("IMPORT");
    wFunction.add("REPLACE");
    wFunction.add("UPDATE");
    wFunction.add("VALIDATE");
    wFunction.addModifyListener(
        e -> {
          input.setChanged();
          enableDisableControls();
          refreshOptionsTable();
        });
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.Function.Label"),
        wFunction,
        lastControl);
    lastControl = wFunction;

    wConnection =
        addConnectionLine(
            mainComposite,
            lastControl,
            input.getConnectionName(),
            e -> {
              input.setChanged();
            });
    lastControl = wConnection;

    wGpkgFilePath = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wGpkgFilePath.addModifyListener(e -> input.setChanged());
    wbGpkgFilePath = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
    placeControlWithBrowse(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.GpkgTarget.Label"),
        wGpkgFilePath,
        lastControl,
        wbGpkgFilePath,
        e -> browseSaveFile(wGpkgFilePath, new String[] {"*.gpkg", "*.*"}));
    lastControl = wGpkgFilePath;

    wSchemaName = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wSchemaName.addModifyListener(e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.SchemaName.Label"),
        wSchemaName,
        lastControl);
    lastControl = wSchemaName;

    wImportSourceMode = new ComboVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wImportSourceMode.add("STATIC_PATH");
    wImportSourceMode.add("FIELD");
    wImportSourceMode.addModifyListener(
        e -> {
          input.setChanged();
          enableDisableControls();
        });
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.ImportSourceMode.Label"),
        wImportSourceMode,
        lastControl);
    lastControl = wImportSourceMode;

    wImportFilePath = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wImportFilePath.addModifyListener(e -> input.setChanged());
    wbImportFilePath = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
    placeControlWithBrowse(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.ImportFilePath.Label"),
        wImportFilePath,
        lastControl,
        wbImportFilePath,
        e -> browseFile(wImportFilePath, new String[] {"*.xtf", "*.xml", "*.itf", "*.*"}));
    lastControl = wImportFilePath;

    wImportFileField = new ComboVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wImportFileField.addModifyListener(e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.ImportFileField.Label"),
        wImportFileField,
        lastControl);
    BaseTransformDialog.getFieldsFromPrevious(variables, wImportFileField, pipelineMeta, transformMeta);
    lastControl = wImportFileField;

    wModelName = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wModelName.addModifyListener(e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.ModelName.Label"),
        wModelName,
        lastControl);
    lastControl = wModelName;

    wModelDir = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wModelDir.addModifyListener(e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.ModelDir.Label"),
        wModelDir,
        lastControl);
    lastControl = wModelDir;

    wDefaultSrsCode = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wDefaultSrsCode.addModifyListener(e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.DefaultSrsCode.Label"),
        wDefaultSrsCode,
        lastControl);
    lastControl = wDefaultSrsCode;

    wImplicitSchemaImport = new Button(mainComposite, SWT.CHECK);
    wImplicitSchemaImport.addListener(SWT.Selection, e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.ImplicitSchemaImport.Label"),
        wImplicitSchemaImport,
        lastControl);
    lastControl = wImplicitSchemaImport;

    wStrokeArcs = new Button(mainComposite, SWT.CHECK);
    wStrokeArcs.addListener(SWT.Selection, e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.StrokeArcs.Label"),
        wStrokeArcs,
        lastControl);
    lastControl = wStrokeArcs;

    wNameByTopic = new Button(mainComposite, SWT.CHECK);
    wNameByTopic.addListener(SWT.Selection, e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.NameByTopic.Label"),
        wNameByTopic,
        lastControl);
    lastControl = wNameByTopic;

    wDisableValidation = new Button(mainComposite, SWT.CHECK);
    wDisableValidation.addListener(SWT.Selection, e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.DisableValidation.Label"),
        wDisableValidation,
        lastControl);
    lastControl = wDisableValidation;

    wFailOnError = new Button(mainComposite, SWT.CHECK);
    wFailOnError.addListener(SWT.Selection, e -> input.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.FailOnError.Label"),
        wFailOnError,
        lastControl);

    mainTab.setControl(mainComposite);
  }

  private void createOptionsTab(CTabFolder tabFolder) {
    CTabItem optionsTab = new CTabItem(tabFolder, SWT.NONE);
    optionsTab.setText(BaseMessages.getString(PKG, "Ili2dbDialog.Tab.Options"));

    Composite optionsComposite = new Composite(tabFolder, SWT.NONE);
    PropsUi.setLook(optionsComposite);
    FormLayout layout = new FormLayout();
    layout.marginWidth = PropsUi.getFormMargin();
    layout.marginHeight = PropsUi.getFormMargin();
    optionsComposite.setLayout(layout);

    ColumnInfo[] columns = new ColumnInfo[5];
    columns[0] =
        new ColumnInfo(BaseMessages.getString(PKG, "Ili2dbDialog.Options.Column.Key"), ColumnInfo.COLUMN_TYPE_TEXT);
    columns[0].setReadOnly(true);

    columns[1] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "Ili2dbDialog.Options.Column.Active"),
            ColumnInfo.COLUMN_TYPE_CCOMBO,
            new String[] {"Y", "N"});

    columns[2] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "Ili2dbDialog.Options.Column.Value"),
            ColumnInfo.COLUMN_TYPE_TEXT);

    columns[3] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "Ili2dbDialog.Options.Column.Type"),
            ColumnInfo.COLUMN_TYPE_TEXT);
    columns[3].setReadOnly(true);

    columns[4] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "Ili2dbDialog.Options.Column.Applicable"),
            ColumnInfo.COLUMN_TYPE_TEXT);
    columns[4].setReadOnly(true);

    wOptions =
        new TableView(
            variables,
            optionsComposite,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE,
            columns,
            12,
            e -> input.setChanged(),
            props);

    FormData fdOptions = new FormData();
    fdOptions.left = new FormAttachment(0, 0);
    fdOptions.top = new FormAttachment(0, 0);
    fdOptions.right = new FormAttachment(100, 0);
    fdOptions.bottom = new FormAttachment(100, 0);
    wOptions.setLayoutData(fdOptions);

    optionsTab.setControl(optionsComposite);
  }

  private void createDatasetTab(CTabFolder tabFolder) {
    CTabItem datasetTab = new CTabItem(tabFolder, SWT.NONE);
    datasetTab.setText(BaseMessages.getString(PKG, "Ili2dbDialog.Tab.Dataset"));

    Composite datasetComposite = new Composite(tabFolder, SWT.NONE);
    PropsUi.setLook(datasetComposite);
    FormLayout layout = new FormLayout();
    layout.marginWidth = PropsUi.getFormMargin();
    layout.marginHeight = PropsUi.getFormMargin();
    datasetComposite.setLayout(layout);

    Control lastControl = null;

    wDatasetMode = new ComboVar(variables, datasetComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wDatasetMode.add("STATIC");
    wDatasetMode.add("FIELD");
    wDatasetMode.addModifyListener(
        e -> {
          input.setChanged();
          enableDisableControls();
        });
    placeControl(
        datasetComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.DatasetMode.Label"),
        wDatasetMode,
        lastControl);
    lastControl = wDatasetMode;

    wDatasetName = new TextVar(variables, datasetComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wDatasetName.addModifyListener(e -> input.setChanged());
    placeControl(
        datasetComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.DatasetName.Label"),
        wDatasetName,
        lastControl);
    lastControl = wDatasetName;

    wDatasetField = new ComboVar(variables, datasetComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wDatasetField.addModifyListener(e -> input.setChanged());
    placeControl(
        datasetComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.DatasetField.Label"),
        wDatasetField,
        lastControl);
    BaseTransformDialog.getFieldsFromPrevious(variables, wDatasetField, pipelineMeta, transformMeta);

    datasetTab.setControl(datasetComposite);
  }

  private void createOutputTab(CTabFolder tabFolder) {
    CTabItem outputTab = new CTabItem(tabFolder, SWT.NONE);
    outputTab.setText(BaseMessages.getString(PKG, "Ili2dbDialog.Tab.Output"));

    Composite outputComposite = new Composite(tabFolder, SWT.NONE);
    PropsUi.setLook(outputComposite);
    FormLayout layout = new FormLayout();
    layout.marginWidth = PropsUi.getFormMargin();
    layout.marginHeight = PropsUi.getFormMargin();
    outputComposite.setLayout(layout);

    Control lastControl = null;

    wOutputConnectionField = new Text(outputComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wOutputConnectionField.addModifyListener(e -> input.setChanged());
    placeControl(
        outputComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.OutputConnectionField.Label"),
        wOutputConnectionField,
        lastControl);
    lastControl = wOutputConnectionField;

    wOutputTargetFileField = new Text(outputComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wOutputTargetFileField.addModifyListener(e -> input.setChanged());
    placeControl(
        outputComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.OutputTargetFileField.Label"),
        wOutputTargetFileField,
        lastControl);
    lastControl = wOutputTargetFileField;

    wOutputDatabaseSchemaField = new Text(outputComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wOutputDatabaseSchemaField.addModifyListener(e -> input.setChanged());
    placeControl(
        outputComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.OutputDatabaseSchemaField.Label"),
        wOutputDatabaseSchemaField,
        lastControl);
    lastControl = wOutputDatabaseSchemaField;

    wCreateLogFile = new Button(outputComposite, SWT.CHECK);
    wCreateLogFile.addListener(
        SWT.Selection,
        e -> {
          input.setChanged();
          enableDisableControls();
        });
    placeControl(
        outputComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.CreateLogFile.Label"),
        wCreateLogFile,
        lastControl);
    lastControl = wCreateLogFile;

    wLogDirectory = new TextVar(variables, outputComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wLogDirectory.addModifyListener(e -> input.setChanged());
    wbLogDirectory = new Button(outputComposite, SWT.PUSH | SWT.CENTER);
    placeControlWithBrowse(
        outputComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.LogDirectory.Label"),
        wLogDirectory,
        lastControl,
        wbLogDirectory,
        e -> browseDirectory(wLogDirectory));
    lastControl = wLogDirectory;

    wOutputLogFilePathField = new Text(outputComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wOutputLogFilePathField.addModifyListener(e -> input.setChanged());
    placeControl(
        outputComposite,
        BaseMessages.getString(PKG, "Ili2dbDialog.OutputLogFilePathField.Label"),
        wOutputLogFilePathField,
        lastControl);

    outputTab.setControl(outputComposite);
  }

  private void placeControl(Composite parent, String labelText, Control control, Control under) {
    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    Label label = new Label(parent, SWT.RIGHT);
    label.setText(labelText);
    PropsUi.setLook(label);
    FormData fdLabel = new FormData();
    fdLabel.left = new FormAttachment(0, 0);
    fdLabel.right = new FormAttachment(middle, -margin);
    fdLabel.top = under == null ? new FormAttachment(0, 0) : new FormAttachment(under, margin);
    label.setLayoutData(fdLabel);

    PropsUi.setLook(control);
    FormData fdControl = new FormData();
    fdControl.left = new FormAttachment(middle, 0);
    fdControl.right = new FormAttachment(100, 0);
    fdControl.top = under == null ? new FormAttachment(0, 0) : new FormAttachment(under, margin);
    control.setLayoutData(fdControl);
  }

  private void placeControlWithBrowse(
      Composite parent,
      String labelText,
      Control control,
      Control under,
      Button browseButton,
      org.eclipse.swt.widgets.Listener browseListener) {
    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    Label label = new Label(parent, SWT.RIGHT);
    label.setText(labelText);
    PropsUi.setLook(label);
    FormData fdLabel = new FormData();
    fdLabel.left = new FormAttachment(0, 0);
    fdLabel.right = new FormAttachment(middle, -margin);
    fdLabel.top = under == null ? new FormAttachment(0, 0) : new FormAttachment(under, margin);
    label.setLayoutData(fdLabel);

    browseButton.setText(BaseMessages.getString(PKG, "System.Button.Browse"));
    PropsUi.setLook(browseButton);
    FormData fdBrowse = new FormData();
    fdBrowse.right = new FormAttachment(100, 0);
    fdBrowse.top = under == null ? new FormAttachment(0, 0) : new FormAttachment(under, 0);
    browseButton.setLayoutData(fdBrowse);
    browseButton.addListener(SWT.Selection, browseListener);

    PropsUi.setLook(control);
    FormData fdControl = new FormData();
    fdControl.left = new FormAttachment(middle, 0);
    fdControl.right = new FormAttachment(browseButton, -margin);
    fdControl.top = under == null ? new FormAttachment(0, 0) : new FormAttachment(under, margin);
    control.setLayoutData(fdControl);
  }

  private void browseFile(TextVar textVar, String[] filterExtensions) {
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterExtensions(filterExtensions);
    String currentPath = textVar.getText();
    if (currentPath != null && !currentPath.isEmpty()) {
      dialog.setFileName(currentPath);
    }
    String path = dialog.open();
    if (path != null) {
      textVar.setText(path);
      input.setChanged();
    }
  }

  private void browseSaveFile(TextVar textVar, String[] filterExtensions) {
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterExtensions(filterExtensions);
    dialog.setOverwrite(true);
    String currentPath = textVar.getText();
    if (currentPath != null && !currentPath.isEmpty()) {
      dialog.setFileName(currentPath);
    }
    String path = dialog.open();
    if (path != null) {
      textVar.setText(path);
      input.setChanged();
    }
  }

  private void browseDirectory(TextVar textVar) {
    DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
    String currentPath = textVar.getText();
    if (currentPath != null && !currentPath.isEmpty()) {
      dialog.setFilterPath(currentPath);
    }
    String path = dialog.open();
    if (path != null) {
      textVar.setText(path);
      input.setChanged();
    }
  }

  private void normalizeStoredWindowSize() {
    WindowProperty windowProperty = props.getScreen(shell.getText());
    if (windowProperty == null) {
      return;
    }

    Rectangle clientArea = shell.getDisplay().getPrimaryMonitor().getClientArea();
    int maxHeight = (int) (clientArea.height * 0.9);
    int maxWidth = (int) (clientArea.width * 0.95);
    boolean changed = false;

    if (windowProperty.getHeight() > maxHeight) {
      windowProperty.setHeight(760);
      changed = true;
    }
    if (windowProperty.getWidth() > maxWidth) {
      windowProperty.setWidth(980);
      changed = true;
    }
    if (windowProperty.isMaximized()) {
      windowProperty.setMaximized(false);
      changed = true;
    }

    if (changed) {
      props.setScreen(windowProperty);
    }
  }

  private void enableDisableControls() {
    boolean isPg = "ILI2PG".equalsIgnoreCase(wFlavor.getText());
    wConnection.setEnabled(isPg);
    wSchemaName.setEnabled(isPg);

    wGpkgFilePath.setEnabled(!isPg);
    wbGpkgFilePath.setEnabled(!isPg);

    boolean isSchemaImport = "SCHEMA_IMPORT".equalsIgnoreCase(wFunction.getText());
    boolean importFromField = "FIELD".equalsIgnoreCase(wImportSourceMode.getText());

    wImportFilePath.setEnabled(!isSchemaImport && !importFromField);
    wbImportFilePath.setEnabled(!isSchemaImport && !importFromField);
    wImportFileField.setEnabled(!isSchemaImport && importFromField);

    boolean datasetFromField = "FIELD".equalsIgnoreCase(wDatasetMode.getText());
    wDatasetName.setEnabled(!datasetFromField);
    wDatasetField.setEnabled(datasetFromField);

    boolean createLogFile = wCreateLogFile != null && wCreateLogFile.getSelection();
    if (wLogDirectory != null) {
      wLogDirectory.setEnabled(createLogFile);
    }
    if (wbLogDirectory != null) {
      wbLogDirectory.setEnabled(createLogFile);
    }
    if (wOutputLogFilePathField != null) {
      wOutputLogFilePathField.setEnabled(createLogFile);
    }
  }

  private void getData() {
    wTransformName.setText(transformName == null ? "" : transformName);

    wFlavor.setText(input.getFlavor() == null ? "ILI2GPKG" : input.getFlavor());
    wFunction.setText(input.getFunction() == null ? "IMPORT" : input.getFunction());

    wConnection.setText(input.getConnectionName() == null ? "" : input.getConnectionName());
    wGpkgFilePath.setText(input.getGpkgFilePath() == null ? "" : input.getGpkgFilePath());
    wSchemaName.setText(input.getSchemaName() == null ? "" : input.getSchemaName());

    wImportSourceMode.setText(
        input.getImportSourceMode() == null ? "STATIC_PATH" : input.getImportSourceMode());
    wImportFilePath.setText(input.getImportFilePath() == null ? "" : input.getImportFilePath());
    wImportFileField.setText(input.getImportFileField() == null ? "" : input.getImportFileField());

    wModelName.setText(input.getModelName() == null ? "" : input.getModelName());
    wModelDir.setText(input.getModelDir() == null ? "" : input.getModelDir());
    wDefaultSrsCode.setText(input.getDefaultSrsCode() == null ? "2056" : input.getDefaultSrsCode());

    wImplicitSchemaImport.setSelection(input.isImplicitSchemaImport());
    wStrokeArcs.setSelection(input.isStrokeArcs());
    wNameByTopic.setSelection(input.isNameByTopic());
    wDisableValidation.setSelection(input.isDisableValidation());
    wFailOnError.setSelection(input.isFailOnError());

    wDatasetMode.setText(input.getDatasetMode() == null ? "STATIC" : input.getDatasetMode());
    wDatasetName.setText(input.getDatasetName() == null ? "" : input.getDatasetName());
    wDatasetField.setText(input.getDatasetField() == null ? "" : input.getDatasetField());

    wOutputConnectionField.setText(
        input.getOutputConnectionField() == null ? "ili2db_connection" : input.getOutputConnectionField());
    wOutputTargetFileField.setText(
        input.getOutputTargetFileField() == null ? "ili2db_target_file" : input.getOutputTargetFileField());
    wOutputDatabaseSchemaField.setText(
        input.getOutputDatabaseSchemaField() == null
            ? "ili2db_database_schema"
            : input.getOutputDatabaseSchemaField());
    wCreateLogFile.setSelection(input.isCreateLogFile());
    wLogDirectory.setText(input.getLogDirectory() == null ? "" : input.getLogDirectory());
    wOutputLogFilePathField.setText(
        input.getOutputLogFilePathField() == null ? "log_file_path" : input.getOutputLogFilePathField());

    populateOptionsTable(Ili2dbOptionCodec.decode(input.getSerializedOptions()));

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void refreshOptionsTable() {
    List<Ili2dbOptionEntry> current = readOptionsFromTable();
    populateOptionsTable(current);
  }

  private void populateOptionsTable(List<Ili2dbOptionEntry> selectedValues) {
    Map<String, Ili2dbOptionEntry> existing = new LinkedHashMap<>();
    for (Ili2dbOptionEntry entry : selectedValues) {
      if (entry != null && entry.getKey() != null) {
        existing.put(entry.getKey().toLowerCase(Locale.ROOT), entry);
      }
    }

    wOptions.removeAll();

    Ili2dbFlavor flavor = Ili2dbFlavor.fromValue(wFlavor.getText());
    Ili2dbFunction function = Ili2dbFunction.fromValue(wFunction.getText());

    for (Ili2dbOptionDefinition definition : Ili2dbOptionCatalog.allDefinitions()) {
      Ili2dbOptionEntry existingEntry = existing.get(definition.getKey().toLowerCase(Locale.ROOT));
      boolean applies = definition.appliesTo(flavor, function);
      boolean enabled = existingEntry != null && existingEntry.isEnabled() && applies;
      String value = existingEntry == null || existingEntry.getValue() == null ? "" : existingEntry.getValue();

      TableItem item =
          wOptions.add(
              definition.getKey(),
              enabled ? "Y" : "N",
              value,
              definition.getType().name(),
              applies
                  ? BaseMessages.getString(PKG, "Ili2dbDialog.Options.Applicable.Yes")
                  : BaseMessages.getString(PKG, "Ili2dbDialog.Options.Applicable.No"));
      if (!applies) {
        item.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
      }
    }

    wOptions.removeEmptyRows();
    wOptions.optWidth(true);
  }

  private List<Ili2dbOptionEntry> readOptionsFromTable() {
    List<Ili2dbOptionEntry> entries = new java.util.ArrayList<>();
    for (TableItem item : wOptions.getNonEmptyItems()) {
      String key = item.getText(1);
      if (key == null || key.isBlank()) {
        continue;
      }
      String active = item.getText(2);
      String value = item.getText(3);
      boolean enabled = "Y".equalsIgnoreCase(active) || "TRUE".equalsIgnoreCase(active);

      if (!enabled && (value == null || value.isBlank())) {
        continue;
      }

      entries.add(new Ili2dbOptionEntry(key, enabled, value));
    }
    return entries;
  }

  private void cancel() {
    transformName = null;
    input.setChanged(changed);
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wTransformName.getText())) {
      return;
    }

    transformName = wTransformName.getText();

    input.setFlavor(wFlavor.getText());
    input.setFunction(wFunction.getText());

    input.setConnectionName(wConnection.getText());
    input.setGpkgFilePath(wGpkgFilePath.getText());
    input.setSchemaName(wSchemaName.getText());

    input.setImportSourceMode(wImportSourceMode.getText());
    input.setImportFilePath(wImportFilePath.getText());
    input.setImportFileField(wImportFileField.getText());

    input.setModelName(wModelName.getText());
    input.setModelDir(wModelDir.getText());
    input.setDefaultSrsCode(wDefaultSrsCode.getText());
    input.setImplicitSchemaImport(wImplicitSchemaImport.getSelection());
    input.setStrokeArcs(wStrokeArcs.getSelection());
    input.setNameByTopic(wNameByTopic.getSelection());
    input.setDisableValidation(wDisableValidation.getSelection());
    input.setFailOnError(wFailOnError.getSelection());

    input.setDatasetMode(wDatasetMode.getText());
    input.setDatasetName(wDatasetName.getText());
    input.setDatasetField(wDatasetField.getText());

    input.setOutputConnectionField(wOutputConnectionField.getText());
    input.setOutputTargetFileField(wOutputTargetFileField.getText());
    input.setOutputDatabaseSchemaField(wOutputDatabaseSchemaField.getText());
    input.setCreateLogFile(wCreateLogFile.getSelection());
    input.setLogDirectory(wLogDirectory.getText());
    input.setOutputLogFilePathField(wOutputLogFilePathField.getText());

    input.setSerializedOptions(Ili2dbOptionCodec.encode(readOptionsFromTable()));

    dispose();
  }
}
