import java.awt.geom.Point2D;
import java.util.ArrayList;


public class ClassifierLinearDiscriminantFunction implements IClassifier{

	private double m_pb, c;

	public ClassifierLinearDiscriminantFunction() 
	{
	}
	
	public void train(ArrayList<TrainingVector> vectors, boolean isVerbose) 
	{
		// Store the two mean points of the two classes.
		Point2D.Double vG_mu = new Point2D.Double();
		Point2D.Double vH_mu = new Point2D.Double();
		
		int numVG = 0;
		int numVH = 0;
		
		// Iterate over the training vectors and get the mean.
		for(TrainingVector tVec : vectors)
			if(tVec.isPositive())
			{
				vG_mu.setLocation(vG_mu.x + tVec.getVector()[0], vG_mu.y + tVec.getVector()[1]);
				numVG++;
			}
			else
			{
				vH_mu.setLocation(vH_mu.x + tVec.getVector()[0], vH_mu.y + tVec.getVector()[1]);
				numVH++;
			}
		
		// Get the averages.
		vG_mu.setLocation(vG_mu.x / numVG, vG_mu.y / numVG);
		vH_mu.setLocation(vH_mu.x / numVH, vH_mu.y / numVH);
		
		// Get the gradient of the line connecting the two means.
		double m = (vG_mu.y - vH_mu.y) / (vG_mu.x - vH_mu.x);
		
		// Get the gradient perpendicular to this line.
		m_pb = -1/m;
		
		// Get the midpoint of this line.
		Point2D.Double midPoint = new Point2D.Double((vG_mu.x + vH_mu.x) / 2, (vG_mu.y + vH_mu.y) / 2);
		
		// Get c.
		c = midPoint.y - m_pb * midPoint.x;
	}	

	public boolean predict(double[] vector) 
	{
		return (vector[1] - m_pb * vector[0] - c) > 0;
	}
}
