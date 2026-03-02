package ch.so.agi.ili2db.hop.action;

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
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.ComboVar;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ActionIli2dbDialog extends ActionDialog {

  private static final Class<?> PKG = ActionIli2db.class;

  private final ActionIli2db action;
  private boolean backupChanged;

  private Text wName;

  private ComboVar wFlavor;
  private ComboVar wFunction;
  private MetaSelectionLine<DatabaseMeta> wConnection;
  private TextVar wGpkgFilePath;
  private Button wbGpkgFilePath;
  private TextVar wSchemaName;

  private TextVar wImportFilePath;
  private Button wbImportFilePath;

  private TextVar wModelName;
  private TextVar wModelDir;
  private TextVar wDefaultSrsCode;
  private Button wImplicitSchemaImport;
  private Button wStrokeArcs;
  private Button wNameByTopic;
  private Button wDisableValidation;

  private TableView wOptions;

  private TextVar wDatasetName;

  public ActionIli2dbDialog(
      Shell parent, ActionIli2db action, WorkflowMeta workflowMeta, IVariables variables) {
    super(parent, workflowMeta, variables);
    this.action = action;
  }

  @Override
  public IAction open() {
    shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.RESIZE);
    shell.setMinimumSize(900, 680);
    shell.setSize(980, 760);
    PropsUi.setLook(shell);
    WorkflowDialog.setShellImage(shell, action);

    backupChanged = action.hasChanged();

    FormLayout shellLayout = new FormLayout();
    shellLayout.marginWidth = PropsUi.getFormMargin();
    shellLayout.marginHeight = PropsUi.getFormMargin();
    shell.setLayout(shellLayout);
    shell.setText(BaseMessages.getString(PKG, "ActionIli2dbDialog.Title"));

    int margin = PropsUi.getMargin();
    int middle = props.getMiddlePct();

    Label wlName = new Label(shell, SWT.RIGHT);
    wlName.setText(BaseMessages.getString(PKG, "System.ActionName.Label"));
    wlName.setToolTipText(BaseMessages.getString(PKG, "System.ActionName.Tooltip"));
    PropsUi.setLook(wlName);
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment(0, 0);
    fdlName.top = new FormAttachment(0, margin);
    fdlName.right = new FormAttachment(middle, -margin);
    wlName.setLayoutData(fdlName);

    wName = new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT);
    PropsUi.setLook(wName);
    wName.addModifyListener(e -> action.setChanged());
    FormData fdName = new FormData();
    fdName.left = new FormAttachment(middle, 0);
    fdName.top = new FormAttachment(0, margin);
    fdName.right = new FormAttachment(100, 0);
    wName.setLayoutData(fdName);

    Button wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    Button wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

    BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk, wCancel}, margin, null);

    CTabFolder tabFolder = new CTabFolder(shell, SWT.BORDER);
    PropsUi.setLook(tabFolder);
    tabFolder.setSimple(false);
    tabFolder.setUnselectedCloseVisible(false);
    FormData fdTabs = new FormData();
    fdTabs.left = new FormAttachment(0, 0);
    fdTabs.top = new FormAttachment(wName, margin * 2);
    fdTabs.right = new FormAttachment(100, 0);
    fdTabs.bottom = new FormAttachment(wOk, -margin * 2);
    tabFolder.setLayoutData(fdTabs);

    createMainTab(tabFolder);
    createOptionsTab(tabFolder);
    createDatasetTab(tabFolder);

    wOk.addListener(SWT.Selection, e -> ok());
    wCancel.addListener(SWT.Selection, e -> cancel());

    getData();
    enableDisableControls();

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return action;
  }

  private void createMainTab(CTabFolder tabFolder) {
    CTabItem mainTab = new CTabItem(tabFolder, SWT.NONE);
    mainTab.setText(BaseMessages.getString(PKG, "ActionIli2dbDialog.Tab.Main"));

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
          action.setChanged();
          enableDisableControls();
          refreshOptionsTable();
        });
    placeControl(mainComposite, BaseMessages.getString(PKG, "ActionIli2dbDialog.Flavor.Label"), wFlavor, lastControl);
    lastControl = wFlavor;

    wFunction = new ComboVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wFunction.add("SCHEMA_IMPORT");
    wFunction.add("IMPORT");
    wFunction.add("REPLACE");
    wFunction.add("UPDATE");
    wFunction.add("VALIDATE");
    wFunction.addModifyListener(
        e -> {
          action.setChanged();
          enableDisableControls();
          refreshOptionsTable();
        });
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.Function.Label"),
        wFunction,
        lastControl);
    lastControl = wFunction;

    wConnection =
        addConnectionLine(
            mainComposite,
            lastControl,
            action.getConnectionName(),
            e -> action.setChanged());
    lastControl = wConnection;

    wGpkgFilePath = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wGpkgFilePath.addModifyListener(e -> action.setChanged());
    wbGpkgFilePath = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
    placeControlWithBrowse(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.GpkgTarget.Label"),
        wGpkgFilePath,
        lastControl,
        wbGpkgFilePath,
        e -> browseSaveFile(wGpkgFilePath, new String[] {"*.gpkg", "*.*"}));
    lastControl = wGpkgFilePath;

    wSchemaName = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wSchemaName.addModifyListener(e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.SchemaName.Label"),
        wSchemaName,
        lastControl);
    lastControl = wSchemaName;

    wImportFilePath = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wImportFilePath.addModifyListener(e -> action.setChanged());
    wbImportFilePath = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
    placeControlWithBrowse(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.ImportFilePath.Label"),
        wImportFilePath,
        lastControl,
        wbImportFilePath,
        e -> browseFile(wImportFilePath, new String[] {"*.xtf", "*.xml", "*.itf", "*.*"}));
    lastControl = wImportFilePath;

    wModelName = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wModelName.addModifyListener(e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.ModelName.Label"),
        wModelName,
        lastControl);
    lastControl = wModelName;

    wModelDir = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wModelDir.addModifyListener(e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.ModelDir.Label"),
        wModelDir,
        lastControl);
    lastControl = wModelDir;

    wDefaultSrsCode = new TextVar(variables, mainComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wDefaultSrsCode.addModifyListener(e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.DefaultSrsCode.Label"),
        wDefaultSrsCode,
        lastControl);
    lastControl = wDefaultSrsCode;

    wImplicitSchemaImport = new Button(mainComposite, SWT.CHECK);
    wImplicitSchemaImport.addListener(SWT.Selection, e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.ImplicitSchemaImport.Label"),
        wImplicitSchemaImport,
        lastControl);
    lastControl = wImplicitSchemaImport;

    wStrokeArcs = new Button(mainComposite, SWT.CHECK);
    wStrokeArcs.addListener(SWT.Selection, e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.StrokeArcs.Label"),
        wStrokeArcs,
        lastControl);
    lastControl = wStrokeArcs;

    wNameByTopic = new Button(mainComposite, SWT.CHECK);
    wNameByTopic.addListener(SWT.Selection, e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.NameByTopic.Label"),
        wNameByTopic,
        lastControl);
    lastControl = wNameByTopic;

    wDisableValidation = new Button(mainComposite, SWT.CHECK);
    wDisableValidation.addListener(SWT.Selection, e -> action.setChanged());
    placeControl(
        mainComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.DisableValidation.Label"),
        wDisableValidation,
        lastControl);

    mainTab.setControl(mainComposite);
  }

  private void createOptionsTab(CTabFolder tabFolder) {
    CTabItem optionsTab = new CTabItem(tabFolder, SWT.NONE);
    optionsTab.setText(BaseMessages.getString(PKG, "ActionIli2dbDialog.Tab.Options"));

    Composite optionsComposite = new Composite(tabFolder, SWT.NONE);
    PropsUi.setLook(optionsComposite);
    FormLayout layout = new FormLayout();
    layout.marginWidth = PropsUi.getFormMargin();
    layout.marginHeight = PropsUi.getFormMargin();
    optionsComposite.setLayout(layout);

    ColumnInfo[] columns = new ColumnInfo[5];
    columns[0] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Column.Key"),
            ColumnInfo.COLUMN_TYPE_TEXT);
    columns[0].setReadOnly(true);

    columns[1] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Column.Active"),
            ColumnInfo.COLUMN_TYPE_CCOMBO,
            new String[] {"Y", "N"});

    columns[2] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Column.Value"),
            ColumnInfo.COLUMN_TYPE_TEXT);

    columns[3] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Column.Type"),
            ColumnInfo.COLUMN_TYPE_TEXT);
    columns[3].setReadOnly(true);

    columns[4] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Column.Applicable"),
            ColumnInfo.COLUMN_TYPE_TEXT);
    columns[4].setReadOnly(true);

    wOptions =
        new TableView(
            variables,
            optionsComposite,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE,
            columns,
            12,
            e -> action.setChanged(),
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
    datasetTab.setText(BaseMessages.getString(PKG, "ActionIli2dbDialog.Tab.Dataset"));

    Composite datasetComposite = new Composite(tabFolder, SWT.NONE);
    PropsUi.setLook(datasetComposite);
    FormLayout layout = new FormLayout();
    layout.marginWidth = PropsUi.getFormMargin();
    layout.marginHeight = PropsUi.getFormMargin();
    datasetComposite.setLayout(layout);

    wDatasetName = new TextVar(variables, datasetComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wDatasetName.addModifyListener(e -> action.setChanged());
    placeControl(
        datasetComposite,
        BaseMessages.getString(PKG, "ActionIli2dbDialog.DatasetName.Label"),
        wDatasetName,
        null);

    datasetTab.setControl(datasetComposite);
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
      action.setChanged();
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
      action.setChanged();
    }
  }

  private void enableDisableControls() {
    boolean isPg = "ILI2PG".equalsIgnoreCase(wFlavor.getText());
    wConnection.setEnabled(isPg);
    wSchemaName.setEnabled(isPg);

    wGpkgFilePath.setEnabled(!isPg);
    wbGpkgFilePath.setEnabled(!isPg);
  }

  private void getData() {
    wName.setText(action.getName() == null ? "" : action.getName());

    wFlavor.setText(action.getFlavor() == null ? "ILI2GPKG" : action.getFlavor());
    wFunction.setText(action.getFunction() == null ? "IMPORT" : action.getFunction());

    wConnection.setText(action.getConnectionName() == null ? "" : action.getConnectionName());
    wGpkgFilePath.setText(action.getGpkgFilePath() == null ? "" : action.getGpkgFilePath());
    wSchemaName.setText(action.getSchemaName() == null ? "" : action.getSchemaName());

    wImportFilePath.setText(action.getImportFilePath() == null ? "" : action.getImportFilePath());

    wModelName.setText(action.getModelName() == null ? "" : action.getModelName());
    wModelDir.setText(action.getModelDir() == null ? "" : action.getModelDir());
    wDefaultSrsCode.setText(action.getDefaultSrsCode() == null ? "2056" : action.getDefaultSrsCode());
    wImplicitSchemaImport.setSelection(action.isImplicitSchemaImport());
    wStrokeArcs.setSelection(action.isStrokeArcs());
    wNameByTopic.setSelection(action.isNameByTopic());
    wDisableValidation.setSelection(action.isDisableValidation());

    wDatasetName.setText(action.getDatasetName() == null ? "" : action.getDatasetName());

    populateOptionsTable(Ili2dbOptionCodec.decode(action.getSerializedOptions()));

    wName.selectAll();
    wName.setFocus();
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
                  ? BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Applicable.Yes")
                  : BaseMessages.getString(PKG, "ActionIli2dbDialog.Options.Applicable.No"));
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

  private void ok() {
    if (Utils.isEmpty(wName.getText())) {
      return;
    }

    action.setName(wName.getText());

    action.setFlavor(wFlavor.getText());
    action.setFunction(wFunction.getText());

    action.setConnectionName(wConnection.getText());
    action.setGpkgFilePath(wGpkgFilePath.getText());
    action.setSchemaName(wSchemaName.getText());

    action.setImportFilePath(wImportFilePath.getText());

    action.setModelName(wModelName.getText());
    action.setModelDir(wModelDir.getText());
    action.setDefaultSrsCode(wDefaultSrsCode.getText());
    action.setImplicitSchemaImport(wImplicitSchemaImport.getSelection());
    action.setStrokeArcs(wStrokeArcs.getSelection());
    action.setNameByTopic(wNameByTopic.getSelection());
    action.setDisableValidation(wDisableValidation.getSelection());

    action.setDatasetName(wDatasetName.getText());

    action.setSerializedOptions(Ili2dbOptionCodec.encode(readOptionsFromTable()));

    dispose();
  }

  private void cancel() {
    action.setChanged(backupChanged);
    dispose();
  }
}
