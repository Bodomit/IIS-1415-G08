import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Classifier 
{
	public Classifier()
	{
	}
	
	public void train(ArrayList<TrainingVector> vectors)
	{
		for(TrainingVector tVec : vectors)
		{
			System.out.println(tVec.isPositive() + " Area: " + tVec.getVector()[0] );
		}
	}
}
