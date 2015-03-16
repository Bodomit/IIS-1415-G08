import java.awt.image.BufferedImage;


public class Tester {

	private PreProcessor pre;
	public static void main(String[] args) {
		new Tester();
	}

	public Tester()
	{
		try
		{
			JVision jV = new JVision();
			pre = new PreProcessor();

			// Get an image.
			String path = "C:\\Users\\Bodomite\\Documents\\GitHub\\IIS-1415-G08\\Datasets\\crop\\testing\\glaucoma15_crop.jpg";

			BufferedImage image = ImageOp.readInImage(path);
			jV.imdisp(image, "Orginal", 50, 20);
			Histogram hist1 = new Histogram(image);
			GraphPlot gp = new GraphPlot(hist1);
			jV.imdisp(gp, "Hist Original", 50, 420);

			image = pre.process(image);
			
			jV.imdisp(image, "Processed", 400, 20);
			Histogram hist2 = new Histogram(image);
			GraphPlot gp2 = new GraphPlot(hist2);
			jV.imdisp(gp2, "Hist Processed", 400, 420);
			
		}
		catch(Exception e)
		{
			System.err.println("YOLO!");
		}
	}

}
