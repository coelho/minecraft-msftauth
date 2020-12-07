package coelho.msftauth.auth;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AuthWebView extends Application {

	public static final String URL_CLOSED = "closed://";

	public static final CountDownLatch INSTANCE_LATCH = new CountDownLatch(1);
	public static AuthWebView INSTANCE;
	public static AtomicBoolean STARTED = new AtomicBoolean(false);

	public static AuthWebView of() {
		if (STARTED.compareAndSet(false, true)) {
			new Thread(() -> Application.launch(AuthWebView.class)).start();
		}
		try {
			INSTANCE_LATCH.await();
		} catch (InterruptedException exceptions) {
			Thread.currentThread().interrupt();
		}
		return INSTANCE;
	}

	public static AuthWebView open(String url) {
		AuthWebView webView = of();
		webView.load(url);
		return webView;
	}

	private LinkedBlockingQueue<String> urls = new LinkedBlockingQueue<>();
	private Stage stage;
	private WebView web;

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		this.web = new WebView();
		this.web.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (Worker.State.SUCCEEDED.equals(newValue)) {
				this.urls.add(this.web.getEngine().getLocation());
			}
		});

		VBox vbox = new VBox(this.web);
		Scene scene = new Scene(vbox, 960, 600);
		this.stage.setTitle("Microsoft Login");
		this.stage.setScene(scene);
		this.stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> {
			this.urls.add(URL_CLOSED);
			this.stage.hide();
			event.consume();
		});

		INSTANCE = this;
		INSTANCE_LATCH.countDown();
	}

	public void load(String url) {
		this.urls.clear();
		Platform.runLater(() -> {
			this.urls.clear();
			this.web.getEngine().load(url);
			this.stage.show();
		});
	}

	public String waitForURL(String urlPrefix) throws InterruptedException {
		String url;
		while (true) {
			url = this.urls.take();
			if (url.equals(URL_CLOSED)) {
				return null; // cancelled
			}
			if (url.startsWith(urlPrefix)) {
				Platform.runLater(this.stage::close);
				return url;
			}
		}
	}

}
