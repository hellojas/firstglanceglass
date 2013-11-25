package hack.virginia.glassteam;

import java.io.File;

public class ImageFileReadyEvent {
	private final File imageFile;

	public ImageFileReadyEvent(File imageFile) {
		this.imageFile = imageFile;
	}

	public File getImageFile() {
		return imageFile;
	}
}
