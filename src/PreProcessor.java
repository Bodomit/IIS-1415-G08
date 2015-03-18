import java.awt.image.BufferedImage;

import com.sun.corba.se.spi.extension.ZeroPortPolicy;


public class PreProcessor 
{
	private final int LINEAR_STRECTH_MINIMUM_PEAK_WIDTH;
	private final int LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT;
	private final int BRIGHTNESS_MODIFIER;
	private final double GAMMA;
	
	public PreProcessor()
	{
		LINEAR_STRECTH_MINIMUM_PEAK_WIDTH = 3;
		LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT = 5;
	    BRIGHTNESS_MODIFIER = 30;
	    GAMMA = 1.5;
	}
	
	public PreProcessor(int linearStrechMinimumPeakWidth, int linearStrechMinimumPeakHeight, int brightnessModifier, double gamma)
	{
		LINEAR_STRECTH_MINIMUM_PEAK_WIDTH = linearStrechMinimumPeakWidth;
		LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT =linearStrechMinimumPeakHeight;
		BRIGHTNESS_MODIFIER = brightnessModifier;
		GAMMA = gamma;
	}

	public BufferedImage process(BufferedImage image)
	{
		// Store a reference to the processed image.
		BufferedImage processedImage = image;
		
		// Enhance brightness and contrast.
		//processedImage = enhanceBrightness(processedImage);
		processedImage = enhanceContrast(processedImage);
		
		// Reduce noise by Convolver or Median.
		processedImage = reduceNoisebyConvolver(processedImage);
		//processedImage = reduceNoiseByMedian(processedImage);
		
		// Return the processed image.
		return processedImage;
	}
	
	
	// create the look up table
	public short[] brightnessLut(int c)
	{
		short[] data = new short[256];
		
		for (int i = 0; i<data.length; i++)
		{
			data[i] = (short)Math.max(Math.min(i + c,255),0);
		}
		
		return data;
	}

	private BufferedImage enhanceBrightness(BufferedImage image)
	{
		return ImageOp.pixelop(image,brightnessLut(BRIGHTNESS_MODIFIER));
	}

	private BufferedImage enhanceContrast(BufferedImage image)
	{
		// Perform linear stretching.
		
		return enhanceContrast_LinearStretch_Automated(image);
		//return enhanceContrast_PowerLaw(image);
		// return enhanceContrast_HistogramEqualization(image)
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
	
    
    private BufferedImage reduceNoiseByMedian(BufferedImage image)
    {
    	//return noise reduce image.
    	return ImageOp.median(image, 2);
    }

    private BufferedImage enhanceContrast_LinearStretch_Automated(BufferedImage image)
    {
    	try
    	{
    		// Get the start and end grey levels.
    		int[] startEndLevels = getStartAndEndGrayLevels(image);
    		
    		// Translate the start and end levels into an unclipped transform function.
    		double m = 255f/ (startEndLevels[1] - startEndLevels[0]);
    		double c = -m * startEndLevels[0];
    		
    		// Perform the linear stretch.
    		image = enhanceContrast_LinearStretch(image, m, c);
    	}
    	catch(HistogramException ex)
    	{
    		System.err.println(ex.getMessage());
    	}
		return image;
    }
    
	private BufferedImage enhanceContrast_LinearStretch(BufferedImage image, double m, double c)
	{	
		// Create the lookup table.
		short[] LUT = new short[256];
		for(int i = 0; i < LUT.length; i++)
			LUT[i] = (short) Math.min(Math.max((m * i + c), 0), 255);
		
		// Return the processed image.
		return ImageOp.pixelop(image, LUT);
	}
	
	private int[] getStartAndEndGrayLevels(BufferedImage image) throws HistogramException
	{
		// Get the histogram of the image.
		Histogram hist = new Histogram(image);
		
		// Iterate over the histogram, recording the first peak that meets the minimum width.
		int startGreyLevel = 0;
		int endGreyLevel = hist.getMaxValue() - 1;
		
		int nonZeroLevelsInBuffer = 0;
		
		// Get the start level.
		for(int i = 0; i < hist.getMaxValue(); i++)
		{	
			// Check if gray level i has any samples, and add to nonZeroLevelsInBuffer.
			nonZeroLevelsInBuffer += hist.getFrequency(i) > LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT ? 1 : 0;
			
			if(i < LINEAR_STRECTH_MINIMUM_PEAK_WIDTH)
				continue;
			
			// Remove the first grey level from the buffer.
			nonZeroLevelsInBuffer -= hist.getFrequency(i - LINEAR_STRECTH_MINIMUM_PEAK_WIDTH) > LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT ? 1 : 0;
			
			// Check if the minimum width of a peak has been reached.
			if(nonZeroLevelsInBuffer >= LINEAR_STRECTH_MINIMUM_PEAK_WIDTH)
			{
				startGreyLevel = i - LINEAR_STRECTH_MINIMUM_PEAK_WIDTH;
				break;
			}
		}
		
		// Get the end level.
		nonZeroLevelsInBuffer = 0;
		for(int i = hist.getMaxValue(); i >= 0; i--)
		{	
			// Check if gray level i has any samples, and add to nonZeroLevelsInBuffer.
			nonZeroLevelsInBuffer += hist.getFrequency(i) > LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT ? 1 : 0;
			
			if(i >= 256 - LINEAR_STRECTH_MINIMUM_PEAK_WIDTH)
				continue;
			
			// Remove the first grey level from the buffer.
			nonZeroLevelsInBuffer -= hist.getFrequency(i + LINEAR_STRECTH_MINIMUM_PEAK_WIDTH) > LINEAR_STRETCH_MINIMUM_PEAK_HEIGHT ? 1 : 0;
			
			// Check if the minimum width of a peak has been reached.
			if(nonZeroLevelsInBuffer >= LINEAR_STRECTH_MINIMUM_PEAK_WIDTH)
			{
				endGreyLevel = i;
				break;
			}
		}
		
		// Return the results.
		int[] results = new int[2];
		results[0] = startGreyLevel;
		results[1] = endGreyLevel;
		
		return results;
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
		return ImageOp.pixelop(image,powerLawLut(GAMMA));
	}
    
    // perform histogram equalisation
    private short[] histogramEqualisationLut (Histogram hist) throws HistogramException
	{
		short [] outputData = new short [256];

		for (int i = 0; i<outputData.length; i++)
		{
			outputData[i] = (short)Math.max(0,(256*hist.getCumulativeFrequency(i)/(hist.getNumSamples())-1));
		}

		return outputData;
	}
    
    // enhance contrast via histogram equalisation
    private BufferedImage enhanceContrast_HistogramEqualization(BufferedImage image) throws HistogramException
    {
    	Histogram histogram = new Histogram(image);
    	return ImageOp.pixelop(image, histogramEqualisationLut(histogram));
    }
}
