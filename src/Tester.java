import java.awt.image.BufferedImage;


public class Tester {

	private PreProcessor pre;
	private Segmenter seg;
	private PostProcessor post;
	private FeatureExtractor fE;
	
	public static void main(String[] args) {
		new Tester();
	}

	public Tester()
	{
		try
		{
			JVision jV = new JVision();
			jV.setSize(1200, 750);
			pre = new PreProcessor();
			seg = new Segmenter(1.25);
			post = new PostProcessor();
			fE = new FeatureExtractor();

			// Get an image.
			String path = "C:\\Users\\40057686\\Desktop\\IIS-1415-G08.git\\trunk\\Datasets\\crop\\training\\glaucoma6_crop.jpg";

			BufferedImage image = ImageOp.readInImage(path);
			jV.imdisp(image, "Orginal", 50, 20);
			Histogram hist1 = new Histogram(image);
			GraphPlot gp = new GraphPlot(hist1);
			jV.imdisp(gp, "Hist Original", 50, 370);

			image = pre.process(image);
			
			jV.imdisp(image, "Processed", 400, 20);
			Histogram hist2 = new Histogram(image);
			GraphPlot gp2 = new GraphPlot(hist2);
			jV.imdisp(gp2, "Hist Processed", 400, 370);
			
			image = seg.segment(image);
			jV.imdisp(image, "Segmented", 740, 20);
			System.out.println(fE.getArea(image));
			
			image = post.process(image);
			System.out.println(fE.getArea(image));
			jV.imdisp(image, "Post-Processed", 740, 370);	
		}
		catch(Exception e)
		{
			System.err.println("YOLO!");
		}
	}

}
