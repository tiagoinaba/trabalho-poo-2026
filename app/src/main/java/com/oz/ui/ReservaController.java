package com.oz.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.oz.App;
import com.oz.domain.AreaComum;
import com.oz.domain.Reserva;
import com.oz.domain.exception.RegraNegocioException;
import com.oz.service.AreaComumService;
import com.oz.service.ReservaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class ReservaController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReservaService reservaService;
    private final AreaComumService areaComumService;

    @FXML
    private ListView<AreaComum> areasListView;

    @FXML
    private TableView<Reserva> reservasTableView;

    @FXML
    private TableColumn<Reserva, String> colMorador;

    @FXML
    private TableColumn<Reserva, String> colApto;

    @FXML
    private TableColumn<Reserva, String> colData;

    @FXML
    private ComboBox<AreaComum> areaComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField apartmentField;

    @FXML
    private DatePicker datePicker;

    public ReservaController(ReservaService reservaService, AreaComumService areaComumService) {
        this.reservaService = reservaService;
        this.areaComumService = areaComumService;
    }

    @FXML
    public void initialize() {
        colMorador.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomeMorador()));
        colApto.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAp()));
        colData.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getData().format(DATE_FORMAT)));

        carregarAreas();
        datePicker.setValue(LocalDate.now());

        areasListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        carregarReservas(newValue.getId());
                    } else {
                        reservasTableView.setItems(FXCollections.observableArrayList());
                    }
                });
    }

    private void carregarAreas() {
        var areas = areaComumService.listarTodas();
        areasListView.setItems(FXCollections.observableArrayList(areas));
        areaComboBox.setItems(FXCollections.observableArrayList(areas));
    }

    private void carregarReservas(Long areaId) {
        var reservas = reservaService.listarReservasPorArea(areaId);
        reservasTableView.setItems(FXCollections.observableArrayList(reservas));
    }

    @FXML
    public void handleAgendar() {
        AreaComum area = areaComboBox.getValue();
        String nome = nameField.getText();
        String ap = apartmentField.getText();
        LocalDate data = datePicker.getValue();

        if (area == null) {
            new Alert(AlertType.WARNING, "Selecione uma área.").showAndWait();
            return;
        }
        if (nome == null || nome.trim().isEmpty()) {
            new Alert(AlertType.WARNING, "Informe o nome do morador.").showAndWait();
            return;
        }
        if (ap == null || ap.trim().isEmpty()) {
            new Alert(AlertType.WARNING, "Informe o apartamento.").showAndWait();
            return;
        }
        if (data == null) {
            new Alert(AlertType.WARNING, "Selecione uma data.").showAndWait();
            return;
        }

        try {
            Reserva reserva = new Reserva(area, nome.trim(), ap.trim(), data);
            reservaService.agendar(reserva);
            new Alert(AlertType.INFORMATION, "Reserva agendada com sucesso!").showAndWait();
            handleLimpar();

            AreaComum selected = areasListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                carregarReservas(selected.getId());
            }
            if (areaComboBox.getValue() != null) {
                carregarReservas(area.getId());
            }
        } catch (RegraNegocioException e) {
            new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleCancelarReserva() {
        Reserva selected = reservasTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(AlertType.WARNING, "Selecione uma reserva para cancelar.").showAndWait();
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION,
            "Deseja cancelar a reserva de " + selected.getNomeMorador() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                reservaService.cancelarReserva(selected.getId());
                new Alert(AlertType.INFORMATION, "Reserva cancelada.").showAndWait();
                
                AreaComum areaSelecionada = areasListView.getSelectionModel().getSelectedItem();
                if (areaSelecionada != null) {
                    carregarReservas(areaSelecionada.getId());
                }
            }
        });
    }

    @FXML
    public void handleLimpar() {
        nameField.clear();
        apartmentField.clear();
        datePicker.setValue(LocalDate.now());
        areaComboBox.getSelectionModel().clearSelection();
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
}
