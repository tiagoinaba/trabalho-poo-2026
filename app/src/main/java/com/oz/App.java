package com.oz;

import java.sql.Connection;
import javax.sql.DataSource;
import com.oz.config.Config;
import com.oz.db.DatabaseFactory;
import com.oz.db.MigrationRunner;
import com.oz.db.repository.AreaComumRepository;
import com.oz.db.repository.RegraRepository;
import com.oz.db.repository.ReservaRepository;
import com.oz.db.repository.sqlite.AreaComumRepositoryImpl;
import com.oz.db.repository.sqlite.RegraRepositoryImpl;
import com.oz.db.repository.sqlite.ReservaRepositoryImpl;
import com.oz.service.AreaComumService;
import com.oz.service.ReservaService;
import com.oz.service.impl.AreaComumServiceImpl;
import com.oz.service.impl.ReservaServiceImpl;
import com.oz.ui.AreaController;
import com.oz.ui.HomeController;
import com.oz.ui.ReservaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class App extends Application {
	private static App instance;
	private AreaComumService areaComumService;
	private ReservaService reservaService;
	private Connection connection;
	private Callback<Class<?>, Object> controllerFactory;

	private HomeController homeController;
	private AreaController areaController;
	private ReservaController reservaController;

	@Override
	public void init() throws Exception {
		instance = this;
		Config.load();
		DataSource ds = DatabaseFactory.getDataSource();
		MigrationRunner.initDatabase(ds);

		this.connection = ds.getConnection();
		AreaComumRepository areaComumRepo = new AreaComumRepositoryImpl(connection);
		RegraRepository regraRepository = new RegraRepositoryImpl(connection);
		ReservaRepository reservaRepository = new ReservaRepositoryImpl(connection);

		this.areaComumService = new AreaComumServiceImpl(areaComumRepo, regraRepository);
		this.reservaService = new ReservaServiceImpl(reservaRepository, regraRepository);

		this.areaController = new AreaController(areaComumService);
		this.reservaController = new ReservaController(reservaService, areaComumService);

		this.controllerFactory = clazz -> {
			if (clazz == AreaController.class) {
				return areaController;
			}
			if (clazz == HomeController.class) {
				return homeController;
			}
			if (clazz == ReservaController.class) {
				return reservaController;
			}
			try {
				return clazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static Callback<Class<?>, Object> getContext() {
		return instance.controllerFactory;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		homeController = new HomeController();
		homeController.setStage(primaryStage);

		FXMLLoader loader = new FXMLLoader(App.class.getResource("/views/index.fxml"));
		loader.setControllerFactory(controllerFactory);

		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("OZ");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		if (connection != null) {
			connection.close();
		}
		super.stop();
	}
}
