import java.awt.image.BufferedImage;


public class PreProcessor 
{
	public PreProcessor()
	{
	}

	public BufferedImage process(BufferedImage image)
	{
		// Store a reference to the processed image.
		BufferedImage processedImage = image;
		
		// Enhance brightness and contrast.
		processedImage = enhanceBrightness(processedImage);
		processedImage = enhanceContrast(processedImage);
		
		// Reduce noise by Covolver or Median.
		processedImage = reduceNoisebyConvolver(processedImage);
		
		
		
		// Return the processed image.
		return processedImage;
	}

	private BufferedImage enhanceBrightness(BufferedImage image)
	{
		return image;
	}

	private BufferedImage enhanceContrast(BufferedImage image)
	{
		// Just random values for parameters atm.
		return enhanceContrast_LinearStretch(image, 1f, 0f);
	}
	
	private BufferedImage reduceNoisebyConvolver(BufferedImage image)
	{
		//declare mask.
		final float[] LOWPASS5X5 = {1/9.f,1/9.f,1/9.f,
									1/9.f,1/9.f,1/9.f,
									1/9.f,1/9.f,1/9.f,
									1/9.f,1/9.f,1/9.f,
									1/9.f,1/9.f,1/9.f};
    	
    	//return noise reduce image.
    	return ImageOp.convolver(image, LOWPASS5X5);
	}
	
    
    private BufferedImage reduceNoiseReductionbyMedian(BufferedImage image)
    {
    	//return noise reduce image.
    	return ImageOp.median(image, 2);
    }

	private BufferedImage enhanceContrast_LinearStretch(BufferedImage image, float m, float c)
	{	
		// Create the lookup table.
		short[] LUT = new short[256];
		for(int i = 0; i < LUT.length; i++)
			LUT[i] = (short) Math.min(Math.max((m * i + c), 0), 255);
		
		// Return the processed image.
		return ImageOp.pixelop(image, LUT);
	}
	
	// Create the lookup table.
	private short[] powerLawLut(double gamma)
    {
    	short[] lut = new short[256];
		for(int i = 0; i < lut.length; i++)
		{
			lut[i] = (short) (Math.pow(i,gamma)/ Math.pow(255,gamma-1));
		}
		return lut;
    }
    
	// Return the processed image.
    private BufferedImage enhanceContrast_PowerLaw(BufferedImage image)
	{
		return ImageOp.pixelop(image,powerLawLut(0.8));
	}
}
