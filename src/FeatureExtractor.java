import java.awt.image.BufferedImage;
import java.awt.image.Raster;


public class FeatureExtractor {
	
	public FeatureExtractor()
	{
	}
	
	public TrainingVector extract(TrainingImage image)
	{
		// Get the empty vector.
		double[] vector = new double[1];
		
		// Get the area.
		vector[0] = getArea(image.getImage());
		
		return new TrainingVector(image.isPositive(), vector);
	}
	
	private double getArea(BufferedImage image)
	{
		return Math.round(moment(image, 0, 0));
	}
	
	private double moment(BufferedImage image, int k, int l)
	{
		// Get the raster of the image.
		Raster r = image.getRaster();
		
		// Get the moment.
		double m = 0f;
		for(int i = 0; i < r.getHeight(); i++)
			for(int j = 0; j < r.getWidth(); j++)
				m += Math.pow(i, k) * Math.pow(j, l) * r.getSample(j, i, 0) / 255;
		
		return m;
	}

}
