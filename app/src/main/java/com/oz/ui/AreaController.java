package com.oz.ui;

import com.oz.service.AreaComumService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AreaController {
	private final AreaComumService areaComumService;

	@FXML private TextField nameField;

    public AreaController(AreaComumService areaComumService) {
		this.areaComumService = areaComumService;
	}

	private void resetFields() {
		nameField.setText("");
	}

	@FXML
	public void handleCancel() {
		resetFields();
	}

	@FXML
	public void handleSave() {
		String name = nameField.getText();
		areaComumService.cadastrarArea(name);
		resetFields();
	}
}
