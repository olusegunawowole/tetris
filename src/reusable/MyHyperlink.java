package reusable;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.scene.control.Hyperlink;

public class MyHyperlink extends Hyperlink {

	public MyHyperlink(String name, String linkUrl) {
		super(name);
		setStyle("-fx-border-width: 0px;");
		setOnAction(evt -> {
			try {
				URL url = new URL(linkUrl);
				openLink(url);
				setVisited(true);
			} catch (MalformedURLException err) {
				err.printStackTrace();
			}
		});
	}
	
	private boolean openLink(URL url) {
		Desktop desktop = Desktop.getDesktop();
		if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(url.toURI());
				return true;
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
		
		
		
		
		
	

}
