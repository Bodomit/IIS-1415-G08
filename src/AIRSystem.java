import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * The Automated Image Recognition System.
 * @author 40057686
 *
 */
public class AIRSystem {
	
	public AIRSystem()
	{
	}
	
	public void train(File directory)
	{
		// Load the images.
		ArrayList<TrainingImage> images = getTrainingImages(directory);
		
	}
	
	public void train(String directoryName)
	{
		File directory = new File(directoryName.replace("\"", ""));
		train(directory);
	}
	
	public boolean classify(BufferedImage image)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	public Map<String, Boolean> classifyAllInFolder(File directory)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	public Map<String, Boolean> classifyAllInFolder(String directoryName)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	/**
	 * Takes a file directory and reads all loads all the training images.
	 * @param directory The path to the directory containing the training images.
	 * @return A list of images with whether they have glaucoma or not.
	 */
	private ArrayList<TrainingImage> getTrainingImages(File directory)
	{
		ArrayList<TrainingImage> images = new ArrayList<TrainingImage>();
		
		for(final File image : directory.listFiles())
		{
			try
			{
				boolean isGlaucoma = image.getName().contains("glaucoma") ? true : false;
				BufferedImage img = ImageIO.read(image);
				images.add(new TrainingImage(img, isGlaucoma));
			}
			catch(Exception ex)
			{
				System.err.println(ex.getMessage());
			}
		}
		
		return images;
	}

}
