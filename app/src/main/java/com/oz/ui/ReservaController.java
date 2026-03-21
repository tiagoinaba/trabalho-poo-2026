package com.oz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class ReservaController {

    @FXML private ComboBox<String> areaComboBox;
    @FXML private TextField nameField;
    @FXML private TextField apartmentField;
    @FXML private DatePicker datePicker;

    @FXML
    public void initialize() {
        areaComboBox.getItems().addAll("Salão de Festas", "Churrasqueira", "Área de Lazer");
        
        datePicker.setValue(LocalDate.now());

        System.out.println("Reservation UI Initialized!");
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String apt = apartmentField.getText();
        String area = areaComboBox.getValue();
        LocalDate date = datePicker.getValue();
        
        System.out.println("Tentando salvar reserva:");
        System.out.println("Morador: " + name + " (Apto: " + apt + ")");
        System.out.println("Local: " + area + " em " + date);
    }

    @FXML
    private void handleCancel() {
        System.out.println("Operação cancelada pela zeladora.");
    }
}
