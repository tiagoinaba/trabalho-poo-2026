package com.oz.ui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.oz.App;

public class HomeController {
    private Stage stage;

	@FXML
    private javafx.scene.control.Button btnAreaComum;

    public void setStage(Stage stage) {
		this.stage = stage;
	}

    @FXML
    private void navigateToAreaComum() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/views/areaComum.fxml"));
            loader.setControllerFactory(App.getContext());
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar área comum", e);
        }
    }
}
