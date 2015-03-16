import java.awt.image.BufferedImage;
import java.awt.image.Raster;


public class Segmenter 
{
	private final short[] MANUAL_THRESHOLDING_LUT;
	public Segmenter()
	{
		MANUAL_THRESHOLDING_LUT = thresholdLut(115);
	}
	
	public Segmenter(int threshold)
	{
		MANUAL_THRESHOLDING_LUT = thresholdLut(threshold);
	}
	
	public BufferedImage segment(BufferedImage image)
	{
		return segment_brightness_manual(image);
	}
	
	private BufferedImage segment_brightness_manual(BufferedImage image)
	{
		return ImageOp.pixelop(image, MANUAL_THRESHOLDING_LUT);
	}
	
	private BufferedImage segmentBrightnessAutomatic(BufferedImage image)
	{
		return image;
	}
	
	private BufferedImage segment_edge(BufferedImage image)
	{
		return image;
	}
	
	// Create the lookup table.
	private short[] thresholdLut(int t)
    {
    	short[] lut = new short[256];
		for(int i = 0; i < (short)lut.length; i++)
		{
			if(i >= t)
				lut[i] = 255;
			else
				lut[i]= 0;
		}
		return lut;
    }
	
	// calculate the mean grey level of the image 
	private int mean(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		Raster rast = image.getRaster();
		int sum = 0;
		
		for(int i =0; i<height; i++)
		{
			for(int j = 0; j<width; j++)
			{
				sum += rast.getSample(j, i, 0);
			}
		}
		
		return sum/(width*height);
	}
	
	// calculate the standard deviation 
	public int standardDeviation(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		Raster rast = image.getRaster();
		
		int sum =0;
		
		for(int i = 0; i<height; i++)
		{
			for(int j =0; j<width; j++)
			{
				sum += Math.pow(rast.getSample(j, i, 0) - mean(image), 2);
			}
		}
		
		return (int)Math.sqrt(sum/(width*(height-1)));
	}
	
}
