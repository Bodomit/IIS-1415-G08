import java.awt.image.BufferedImage;


public class TrainingImage {
	private final BufferedImage image;
	private final boolean isPositive;
	
	public TrainingImage(BufferedImage image, boolean isPositive) {
		this.image = image;
		this.isPositive = isPositive;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isPositive() {
		return isPositive;
	}
}
