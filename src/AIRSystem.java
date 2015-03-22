import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
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
		seg = new Segmenter(1);
		post = new PostProcessor();
		feature = new FeatureExtractor();
		classifier = new ClassifierNearestNeighbour(3);
	}

	public AIRSystem(double alpha)
	{
		pre = new PreProcessor();
		seg = new Segmenter(alpha);
		post = new PostProcessor();
		feature = new FeatureExtractor();
		classifier = new ClassifierNearestNeighbour(3);
	}

	/**
	 * Trains the system using images in the given directory.
	 * @param directory The directory containing the training images.
	 * @throws HistogramException 
	 */
	public void train(File directory, boolean isVerbose)
	{
		// Load the images.
		ArrayList<TrainingImage> images = getTrainingImages(directory);
		ArrayList<TrainingVector> vectors = new ArrayList<TrainingVector>();

		// Process each image.
		for(TrainingImage image: images)
		{
			try
			{
				// Get the actual image for processing.
				BufferedImage processedImage = image.getImage();

				// Process the image.
				processedImage = pre.process(processedImage);
				processedImage = seg.segment(processedImage);
				//processedImage = post.process(processedImage);

				// Extract the features.
				TrainingVector tVec = feature.extract(new TrainingImage(processedImage, image.isPositive()));
				vectors.add(tVec);
			}
			catch(Exception e)
			{
				System.err.println(e.getMessage());
			}
		}

		if(isVerbose)
		{
			System.out.println("The Training vectors: ");
			for(int i = 0; i < vectors.size(); i++)
			{
				System.out.printf("%-9s: ", vectors.get(i).isPositive() ? "Glaucoma" : "Healthy");
				for(int j = 0; j < vectors.get(0).getVector().length; j++)
					System.out.printf("%9.2f, ", vectors.get(i).getVector()[j]);

				System.out.println();
			}
			System.out.println();
		}

		// Train the classifier with the vectors.
		classifier.train(vectors, isVerbose);
	}

	/**
	 * Trains the system using images in the given directory.
	 * @param directoryName The directory containing the training images.
	 * @throws HistogramException 
	 */
	public void train(String directoryName, boolean isVerbose)
	{
		File directory = new File(directoryName.replace("\"", ""));
		train(directory, isVerbose);
	}

	public boolean classify(BufferedImage image) throws HistogramException
	{
		// Process the image.
		image = pre.process(image);
		image = seg.segment(image);
		//image = post.process(image);

		// Get the feature vector.
		double[] featureVector = feature.extract(image);

		// Classify the image.
		return classifier.predict(featureVector);
	}

	public void classifyAllInFolder(File directory, boolean isVerbose)
	{
		if(isVerbose)
			System.out.println("Classifying: ");

		// Store the total number of images classified, and how many were classified accurately. 
		int truePositives = 0;
		int trueNegatives = 0;
		int falsePositives = 0;
		int falseNegatives = 0;

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
				String message = isClassifiedGlaucoma ? "Positive" : "Negative";
				if((isClassifiedGlaucoma && isGlaucoma) || (!isClassifiedGlaucoma && !isGlaucoma))
				{
					message += " - Correctly Classified.";

					if(isGlaucoma)
						truePositives++;
					else
						trueNegatives++;

				}
				else
				{
					message += " - Incorrectly Classified.";

					if(isClassifiedGlaucoma)
						falsePositives++;
					else
						falseNegatives++;
				}

				// Output the result.
				if(isVerbose)
					System.out.println(imagePath.getName() + ": " + message);
			}

		} catch (FileNotFoundException | HistogramException e) 
		{
			System.err.println(e.getMessage());
		}

		// Print out the accuracy
		if(isVerbose)
		{
			System.out.printf("\n\nNumber of images: %d", truePositives + trueNegatives + falsePositives + falseNegatives);
			System.out.printf("\nNumber correctly classified: %d", truePositives + trueNegatives);
			System.out.printf("\nAccuracy: %.2f%%", (double)(truePositives + trueNegatives) * 100f / (truePositives + trueNegatives + falsePositives + falseNegatives));
			System.out.printf("\nTrue Negative rate: %.2f", (double)trueNegatives / (falsePositives + trueNegatives));
			System.out.printf("\nTrue Positive rate: %.2f", (double)truePositives / (truePositives + falseNegatives));
			System.out.printf("\nTrue Accuracy: %.2f%%", 50 * (((double)trueNegatives / (falsePositives + trueNegatives)) + ((double)truePositives / (truePositives + falseNegatives))));
		}
		else
		{
			//System.out.printf("%6.2f%%, %6.2f%%, %6.2f%%", (double)trueNegatives * 100 / (falsePositives + trueNegatives), (double)truePositives * 100 / (truePositives + falseNegatives), (double)(truePositives + trueNegatives) * 100f / (truePositives + trueNegatives + falsePositives + falseNegatives));
			System.out.printf("%.0f %.0f", (double)(truePositives + trueNegatives) * 100f / (truePositives + trueNegatives + falsePositives + falseNegatives), 50 * (((double)trueNegatives / (falsePositives + trueNegatives)) + ((double)truePositives / (truePositives + falseNegatives))));
		}


	}

	public void classifyAllInFolder(String directoryName, boolean isVerbose)
	{
		File directory = new File(directoryName.replace("\"", ""));
		classifyAllInFolder(directory, isVerbose);
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

			// Get all the image files.
			File[] imagePaths = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jpg");
				}
			});

			for(final File image : imagePaths)
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
