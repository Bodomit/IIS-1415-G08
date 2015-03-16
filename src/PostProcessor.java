import java.awt.image.BufferedImage;


public class PostProcessor 
{
	public PostProcessor()
	{
	}
	
	public BufferedImage process(BufferedImage image)
	{
		image = close(image, 3);
		return open(image, 3);
	}
    
    private BufferedImage erosion(BufferedImage image)
    {
    	return image;
    }
    
    private BufferedImage dilation(BufferedImage image)
    {
    	return image;
    }
    
    private BufferedImage open(BufferedImage image, int m)
    {
    	// m is mask size.
    	return ImageOp.open(image, 3);
    }
    
    private BufferedImage close(BufferedImage image, int m)
    {
    	//m is mask size.
    	return ImageOp.close(image, m);
    }
}
