package com.oz.ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.oz.App;
import com.oz.domain.AreaComum;
import com.oz.domain.RegraWrapper;
import com.oz.domain.exception.RegraNegocioException;
import com.oz.service.AreaComumService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class AreaController {
    private static final DateTimeFormatter HORA_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final AreaComumService areaComumService;

    @FXML
    private TextField nameField;

    @FXML
    private ListView<AreaComum> areasListView;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<RegraWrapper> regrasTableView;

    @FXML
    private TableColumn<RegraWrapper, String> colDia;

    @FXML
    private TableColumn<RegraWrapper, Boolean> colPermitido;

    @FXML
    private TableColumn<RegraWrapper, String> colInicio;

    @FXML
    private TableColumn<RegraWrapper, String> colLimite;

    @FXML
    private Button btnSalvarRegras;

    private AreaComum selectedArea;

    public AreaController(AreaComumService areaComumService) {
        this.areaComumService = areaComumService;
    }

    @FXML
    public void initialize() {
        colDia.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDiaPortugues()));
        colPermitido.setCellValueFactory(cellData -> cellData.getValue().permitidoProperty());
        colInicio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHorarioInicioTexto()));
        colLimite.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHorarioLimiteTexto()));

        colDia.setCellFactory(col -> new TableCell<RegraWrapper, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });

        colPermitido.setCellFactory(col -> new TableCell<RegraWrapper, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    RegraWrapper wrapper = getTableRow().getItem();
                    if (wrapper != null) {
                        wrapper.permitidoProperty().set(checkBox.isSelected());
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(getTableRow().getItem().isPermitido());
                    setGraphic(checkBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });

        colInicio.setCellFactory(col -> new TimeEditingCell(true));
        colLimite.setCellFactory(col -> new TimeEditingCell(false));

        carregarAreasComuns();
        btnSave.setText("Salvar nova área");
        areasListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    selectedArea = newValue;
                    if (newValue != null) {
                        nameField.setText(newValue.getName());
                        btnSave.setText("Atualizar área selecionada");
                        carregarRegras(newValue.getId());
                    } else {
                        regrasTableView.setItems(FXCollections.observableArrayList());
                    }
                });
    }

    private void carregarAreasComuns() {
        areasListView.setItems(FXCollections.observableArrayList(areaComumService.listarTodas()));
    }

    private void carregarRegras(Long areaId) {
        try {
            var regras = areaComumService.buscarRegrasDaArea(areaId);
            var wrappers = regras.stream().map(RegraWrapper::new).toList();
            regrasTableView.setItems(FXCollections.observableArrayList(wrappers));
        } catch (RegraNegocioException e) {
            new Alert(AlertType.ERROR, "Erro ao carregar regras: " + e.getMessage()).showAndWait();
        }
    }

    private void resetFields() {
        btnSave.setText("Salvar nova área");
        nameField.setText("");
        selectedArea = null;
        areasListView.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleCancel() {
        resetFields();
    }

    @FXML
    public void handleSave() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            new Alert(AlertType.WARNING, "O nome da área não pode estar vazio.").showAndWait();
            return;
        }
        if (selectedArea != null) {
            selectedArea.setName(name);
            areaComumService.atualizarArea(selectedArea);
        } else {
            areaComumService.cadastrarArea(name);
        }
        resetFields();
        carregarAreasComuns();
    }

    @FXML
    public void handleDelete() {
        if (selectedArea == null) {
            new Alert(AlertType.WARNING, "Selecione uma área para excluir.").showAndWait();
            return;
        }

        try {
            areaComumService.deletarArea(selectedArea);
            resetFields();
            carregarAreasComuns();
        } catch (RegraNegocioException e) {
            new Alert(AlertType.ERROR, "Erro ao excluir área: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleSalvarRegras() {
        if (selectedArea == null) {
            new Alert(AlertType.WARNING, "Selecione uma área primeiro.").showAndWait();
            return;
        }

        try {
            for (var wrapper : regrasTableView.getItems()) {
                areaComumService.atualizarRegra(
                        wrapper.getAreaId(),
                        wrapper.getDia(),
                        wrapper.isPermitido(),
                        wrapper.getHorarioInicio(),
                        wrapper.getHorarioLimite()
                );
            }
            new Alert(AlertType.INFORMATION, "Regras salvas com sucesso!").showAndWait();
        } catch (RegraNegocioException e) {
            new Alert(AlertType.ERROR, "Erro ao salvar regras: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/views/index.fxml"));
            loader.setControllerFactory(App.getContext());
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao retornar ao menu", e);
        }
    }

    private class TimeEditingCell extends TableCell<RegraWrapper, String> {
        private final TextField textField = new TextField();
        private final HBox container = new HBox(textField);
        private final boolean isInicio;

        TimeEditingCell(boolean isInicio) {
            this.isInicio = isInicio;
            HBox.setHgrow(textField, Priority.ALWAYS);
            textField.setOnAction(e -> commitEdit(textField.getText()));
            textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (!isFocused) {
                    commitEdit(textField.getText());
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow().getItem() == null) {
                setGraphic(null);
            } else {
                RegraWrapper wrapper = getTableRow().getItem();
                textField.setText(isInicio ? wrapper.getHorarioInicioTexto() : wrapper.getHorarioLimiteTexto());
                setGraphic(container);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
            textField.selectAll();
            textField.requestFocus();
        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);
            RegraWrapper wrapper = getTableRow().getItem();
            if (wrapper != null) {
                LocalTime time = parseTime(newValue);
                if (isInicio) {
                    wrapper.horarioInicioProperty().set(time);
                } else {
                    wrapper.horarioLimiteProperty().set(time);
                }
            }
        }

        private LocalTime parseTime(String text) {
            if (text == null || text.isBlank() || text.equals("--:--")) {
                return null;
            }
            try {
                return LocalTime.parse(text.trim(), HORA_FORMAT);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }
}
