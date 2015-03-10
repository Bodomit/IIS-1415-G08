import java.awt.image.BufferedImage;


public class TrainingImage {
	private final BufferedImage image;
	private final boolean isPositive;
	private final String fileName;
	
	public TrainingImage(BufferedImage image, boolean isPositive, String fileName) {
		this.image = image;
		this.isPositive = isPositive;
		this.fileName = fileName;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isPositive() {
		return isPositive;
	}
	
	public String getFileName() {
		return fileName;
	}
}
