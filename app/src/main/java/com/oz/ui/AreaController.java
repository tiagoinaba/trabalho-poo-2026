package com.oz.ui;

import com.oz.domain.AreaComum;
import com.oz.domain.exception.RegraNegocioException;
import com.oz.service.AreaComumService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AreaController {
	private final AreaComumService areaComumService;

	@FXML
	private TextField nameField;

	@FXML
	private ListView<AreaComum> areasListView;

	@FXML
	private Button btnSave;

	private AreaComum selectedArea;

	public AreaController(AreaComumService areaComumService) {
		this.areaComumService = areaComumService;
	}

	@FXML
	public void initialize() {
		carregarAreasComuns();
		btnSave.setText("Salvar nova área");
		areasListView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					selectedArea = newValue;
					if (newValue != null) {
						nameField.setText(newValue.getName());
						btnSave.setText("Atualizar área selecionada");
					}
				});
	}

	private void carregarAreasComuns() {
		areasListView.setItems(FXCollections.observableArrayList(areaComumService.listarTodas()));
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
}
