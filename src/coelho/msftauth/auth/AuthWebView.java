package coelho.msftauth.auth;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class AuthWebView extends Application {

	public static final CountDownLatch instanceLatch = new CountDownLatch(1);
	public static AuthWebView instance;

	public static AuthWebView of() {
		try {
			instanceLatch.await();
		} catch (InterruptedException exceptions) {
			Thread.currentThread().interrupt();
		}
		return instance;
	}

	public static AuthWebView open(String url) {
		new Thread(() -> Application.launch(AuthWebView.class, url)).start();
		return of();
	}

	private LinkedBlockingQueue<String> urls = new LinkedBlockingQueue<>();
	private Stage stage;

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;
		String url = this.getParameters().getRaw().get(0);

		WebView web = new WebView();
		web.getEngine().load(url);
		web.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (Worker.State.SUCCEEDED.equals(newValue)) {
				this.urls.add(web.getEngine().getLocation());
			}
		});

		VBox vbox = new VBox(web);
		Scene scene = new Scene(vbox, 960, 600);

		primaryStage.setTitle("Microsoft Login");
		primaryStage.setScene(scene);
		primaryStage.show();

		instance = this;
		instanceLatch.countDown();
	}

	public String waitForURL(String urlPrefix) throws InterruptedException {
		String url;
		while (this.stage.isShowing()) {
			url = this.urls.take();
			if (url.startsWith(urlPrefix)) {
				Platform.runLater(this.stage::close);
				return url;
			}
		}
		return null;
	}

}
