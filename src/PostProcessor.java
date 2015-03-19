import java.awt.image.BufferedImage;


public class PostProcessor 
{
	public PostProcessor()
	{
	}
	
	public BufferedImage process(BufferedImage image)
	{
		image = close(image);
		image = open(image);
		return image;
	}
    
    public static BufferedImage erode(BufferedImage image)
    {
    	float v = 1f/9f;
    	float[] mask = {v,v,v,v,v,v,v,v,v};
    	
    	BufferedImage erodedImage = ImageOp.convolver(image, mask);
    	
    	// Set every pixel in the erodedImage that is not totally white to 255.
    	for(int i = 0; i < erodedImage.getHeight(); i++)
    		for(int j = 0; j < erodedImage.getWidth(); j++)
    			if(erodedImage.getRaster().getSample(j, i, 0) < 255)
    				erodedImage.getRaster().setSample(j, i, 0, 0);
    	
    	return erodedImage;
    }
    
    public static BufferedImage dilate(BufferedImage image)
    {
    	float v = 1f/9f;
    	float[] mask = {v,v,v,v,v,v,v,v,v};
    	
    	BufferedImage dilatedImage = ImageOp.convolver(image, mask);
    	
    	// Set every pixel in the erodedImage that is not totally white to 255.
    	for(int i = 0; i < dilatedImage.getHeight(); i++)
    		for(int j = 0; j < dilatedImage.getWidth(); j++)
    			if(dilatedImage.getRaster().getSample(j, i, 0) >= 255f/9)
    				dilatedImage.getRaster().setSample(j, i, 0, 255);
    	
    	return dilatedImage;
    }
    
    private BufferedImage open(BufferedImage image)
    {
    	return dilate(erode(image));
    }
    
    private BufferedImage close(BufferedImage image)
    {
    	return erode(dilate(image));
    }
}
