import java.util.ArrayList;


public class ClassifierLinearDiscriminantFunction implements IClassifier{

	private double[][] trainingVectors;

	public ClassifierLinearDiscriminantFunction() {

		
	}
	
	@Override
	public void train(ArrayList<TrainingVector> vectors, boolean isVerbose) {
		
		// Store the vectors.
		trainingVectors = new double[vectors.size()][vectors.get(0).getVector().length];
				
		for(int i = 0; i < vectors.size(); i++)
		{
			trainingVectors[i] = vectors.get(i).getVector();
		}
		
		
	}
	
	

	@Override
	public boolean predict(double[] vector) {
		
		// Store the mean vectors and absolute distances.
		double[][] meanVecs = new double[trainingVectors.length][trainingVectors[0].length];
		
		//find the mean
		double [] meanFeaure = new double[trainingVectors.length];
		
		for(int i = 0; i < meanVecs.length; i ++)
		{
			int count= 0;
			for(int j = 0; j < meanVecs.length; j++)
				{
					meanFeaure[i] += meanVecs[i][j];
					count++;
				}
			meanFeaure[i] = meanFeaure[i]/count;
		}
		
		
		double meanVector = 0;
		for(int i =0; i < vector.length; i++)
			meanVector += vector[i];
		
		meanVector = meanVector / vector.length;
		
		//find the gradients of line				
		double m = 0;
		m = (meanVector - meanFeaure[0])/(meanVector-meanFeaure[1]);
		
		//find the gradients of perpendicular bisector
		double mpb = 0;
		mpb = -1/m;
		
		
		
		//find the coordinate of the midpoints
		double mp1 = (meanVector + meanFeaure[0])/2;
		double mp2 = (meanVector + meanFeaure[1])/2;
		
		
		//find c
		double c = mp2 - (mpb*mp1);

		if(mp2 + (mpb*mp1) - c > 0)
			return true;
		else
			return false;
	}

}
