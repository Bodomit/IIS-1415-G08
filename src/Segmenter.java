import java.awt.image.BufferedImage;


public class Segmenter 
{
	private final short[] MANUAL_THRESHOLDING_LUT;
	public Segmenter()
	{
		MANUAL_THRESHOLDING_LUT = thresholdLut(50);
	}
	
	public BufferedImage segment(BufferedImage image)
	{
		return image;
	}
	
	private BufferedImage segment_brightness_manual(BufferedImage image)
	{
		return ImageOp.pixelop(image, MANUAL_THRESHOLDING_LUT);
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
			if(i <= t)
				lut[i] = 255;
			else
				lut[i]= 0;
		}
		return lut;
    }
	
}
