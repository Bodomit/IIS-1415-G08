import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.io.FileNotFoundException;
import javax.imageio.ImageIO;

/**
 * The Automated Image Recognition System.
 * @author 40057686
 *
 */
public class AIRSystem {
	
	// Initialise the different components.
	private PreProcessor pre;
	private PostProcessor post;
	private Segmenter seg;
	private FeatureExtractor feature;
	private IClassifier classifier;
	
	public AIRSystem()
	{
		pre = new PreProcessor();
		post = new PostProcessor();
		seg = new Segmenter();
		feature = new FeatureExtractor();
		classifier = new Classifier();
	}
	
	/**
	 * Trains the system using images in the given directory.
	 * @param directory The directory containing the training images.
	 */
	public void train(File directory)
	{
		// Load the images.
		ArrayList<TrainingImage> images = getTrainingImages(directory);
		ArrayList<TrainingVector> vectors = new ArrayList<TrainingVector>();
		
		// Process each image.
		for(TrainingImage image: images)
		{
			// Get the actual image for processing.
			BufferedImage processedImage = image.getImage();
			
			// Process the image.
			processedImage = pre.process(processedImage);
			processedImage = seg.segment(processedImage);
			processedImage = post.process(processedImage);
			
			// Extract the features.
			TrainingVector tVec = feature.extract(new TrainingImage(processedImage, image.isPositive()));
			vectors.add(tVec);
		}
		
		// Train the classifier with the vectors.
		classifier.train(vectors);
	}
	
	/**
	 * Trains the system using images in the given directory.
	 * @param directoryName The directory containing the training images.
	 */
	public void train(String directoryName)
	{
		File directory = new File(directoryName.replace("\"", ""));
		train(directory);
	}
	
	public boolean classify(BufferedImage image)
	{
		// Process the image.
		image = pre.process(image);
		image = seg.segment(image);
		image = post.process(image);
		
		// Get the feature vector.
		double[] featureVector = feature.extract(image);
		
		// Classify the image.
		return classifier.predict(featureVector);
	}
	
	public void classifyAllInFolder(File directory)
	{
		System.out.println("Classifying: ");
		
		// Store the total number of images classified, and how many were classified accurately. 
		int numImages = 0;
		int numCorrectlyClassified = 0;
		
		try 
		{
			// Make sure the directory exists.
			if(!directory.isDirectory())
					throw new FileNotFoundException("The directory \"" + directory.getAbsolutePath() + "\" does not exist.");
			
			// Classify each image in the directory.
			for(File imagePath : directory.listFiles())
			{
				// Get the image.
				BufferedImage image = ImageOp.readInImage(imagePath.getAbsolutePath());
				boolean isClassifiedGlaucoma = classify(image);
				boolean isGlaucoma = imagePath.getName().contains("glaucoma");
				
				// Store and print the results.
				numImages++;
				String message = isClassifiedGlaucoma ? "Positive" : "Negative";
				if((isClassifiedGlaucoma && isGlaucoma) || (!isClassifiedGlaucoma && !isGlaucoma))
				{
					message += " - Correctly Classified.";
					numCorrectlyClassified++;
				}
				else
				{
					message += " - Incorrectly Classified.";
				}
				
				// Output the result.
				System.out.println(imagePath.getName() + ": " + message);
			}
			
		} catch (FileNotFoundException e) 
		{
			System.err.println(e.getMessage());
		}
		
		// Print out the accuracy.
		System.out.printf("\n\nNumber of images: %d", numImages);
		System.out.printf("\nNumber correctly classified: %d", numCorrectlyClassified);
		System.out.printf("\nAccuracy: %.2f%%", numCorrectlyClassified * 100f / numImages);
		
	}
	
	public void classifyAllInFolder(String directoryName)
	{
		File directory = new File(directoryName.replace("\"", ""));
		classifyAllInFolder(directory);
	}
	
	/**
	 * Takes a file directory and reads all loads all the training images.
	 * @param directory The path to the directory containing the training images.
	 * @return A list of images with whether they have glaucoma or not.
	 */
	private ArrayList<TrainingImage> getTrainingImages(File directory)
	{
		ArrayList<TrainingImage> images = new ArrayList<TrainingImage>();
	
		try
		{
			if(!directory.isDirectory())
				throw new FileNotFoundException("The directory \"" + directory.getAbsolutePath() + "\" does not exist.");
				
			for(final File image : directory.listFiles())
			{
				
				if(!image.isFile())
					throw new FileNotFoundException(image.getName() + " does not exist.");
				
				boolean isGlaucoma = image.getName().contains("glaucoma");
				BufferedImage img = ImageOp.readInImage(image.getAbsolutePath());
				images.add(new TrainingImage(img, isGlaucoma));
			}
		}
		catch(FileNotFoundException e)
		{
			System.err.println(e.getMessage());
		}
		
		return images;
	}

}
