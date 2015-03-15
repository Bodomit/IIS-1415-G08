import java.awt.image.BufferedImage;
import java.awt.image.Raster;


public class FeatureExtractor {
	
	public FeatureExtractor()
	{
	}
	
	public TrainingVector extract(TrainingImage image)
	{
		// Get the empty vector.
		double[] vector = new double[2];
		
		// Get the area.
		vector[0] = getArea(image.getImage());
		vector[1] = getPerimeter(image.getImage());
		
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
	
	private double [] position(BufferedImage image)
	{
		//calculate Centroid at M01
		double i = Math.round((moment(image, 0, 1))/ moment(image, 0, 0));
		//calculate Centroid at M10
		double j = Math.round((moment(image, 1, 0))/ moment(image, 0, 0));
		
		double [] Cij = {i, j};
		
		return Cij;
		
	}
	
	private double getPerimeter(BufferedImage image)
	{
		float [] mask3X3 = {1,1,1,
							1,1,1,
							1,1,1};		
		
		return getArea(image) - getArea(ImageOp.convolver(image,mask3X3));
	}
	
	private double compactness(BufferedImage image)
	{
		return Math.pow(getPerimeter(image), 2) / getArea(image);
	}
	

}
