import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Classifier 
{
	public Classifier()
	{
	}
	
	public void train(TrainingImage image)
	{
		
	}
	
	public void trainAll(ArrayList<TrainingImage> images)
	{
		for(TrainingImage image: images)
			train(image);
	}
}
