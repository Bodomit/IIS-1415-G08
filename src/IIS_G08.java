import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class IIS_G08 {

	/**
	 *  The name of the file containing the application properties.
	 */
	private final static String PROPERTY_FILENAME = "config.properties";
	
	public static void main(String[] args) 
	{
		// Get the application properties.
		Properties props = getProperties();
		
		// Get an instance of the Automated Image Recognition System
		AIRSystem airs = new AIRSystem();
		
		// Train the system using the training image set.
		airs.train(props.getProperty("trainingPath"));
		
		// Classify all the test images.
		airs.classifyAllInFolder(props.getProperty("testingPath"));
	}
	
	/**
	 * Gets the application properties such as paths to training / testing folders.
	 * @return The properties set in the config.properties file.
	 */
	public static Properties getProperties()
	{
		Properties props = new Properties();
		InputStream inputStream =  IIS_G08.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);
		
		// Load the properties.
		try
		{
			props.load(inputStream);
		}
		catch(IOException ex)
		{
			System.err.println(ex.getMessage());
		}
		
		return props;
	}

}
