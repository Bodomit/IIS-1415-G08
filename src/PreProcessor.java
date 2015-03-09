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
		//processedImage = enhanceBrightness(processedImage);
		processedImage = enhanceContrast(processedImage);
		
		// Reduce noise.
		//processedImage = reduceNoise(processedImage);
		
		// Return the processed image.
		return processedImage;
	}

	private BufferedImage enhanceBrightness(BufferedImage image)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	private BufferedImage enhanceContrast(BufferedImage image)
	{
		// Just random values for parameters atm.
		return enhanceContrast_LinearStretch(image, 1f, 0f);
	}
	
	private BufferedImage reduceNoise(BufferedImage image)
	{
		throw new UnsupportedOperationException("Not yet implemented."); 
	}

	private BufferedImage enhanceContrast_LinearStretch(BufferedImage image, float m, float c)
	{	
		short[] LUT = new short[256];

		for(int i = 0; i < LUT.length; i++)
			LUT[i] = (short) Math.min(Math.max((m * i + c), 0), 255);
		
		return ImageOp.pixelop(image, LUT);
	}
}
