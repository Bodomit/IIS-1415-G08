import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class ClassifierNearestNeighbour implements IClassifier {
	
	private double[][] trainingVectors;
	private boolean[] trainingVectorClasses;
	private double[] trainingset_feature_mean;
	private double[] trainingset_feature_range;
	private final int K;
	private int n,m;
	
	public ClassifierNearestNeighbour(int K)
	{
		this.K = K;
	}
	
	@Override
	public void train(ArrayList<TrainingVector> vectors, boolean isVerbose) 
	{
		// Store n and m.
		n = vectors.get(0).getVector().length;
		m = vectors.size();
		
		// Store the scaling vectors.
		setScalingVectors(vectors);
		
		// Store the vectors.
		trainingVectors = new double[vectors.size()][vectors.get(0).getVector().length];
		trainingVectorClasses = new boolean[vectors.size()];
		
		for(int i = 0; i < vectors.size(); i++)
		{
			// Scale the feature vector.
			trainingVectors[i] = scale(vectors.get(i).getVector());
			trainingVectorClasses[i] = vectors.get(i).isPositive();
		}
		
	}

	@Override
	public boolean predict(double[] vector)
	{
		// Scale the vector.
		vector = scale(vector);
		
		// Store the difference vectors and absolute distances.
		double[][] differenceVecs = new double[trainingVectors.length][trainingVectors[0].length];
		double[] distances = new double[trainingVectors.length];
		int[] sortedDistanceIndexs = new int[distances.length];
		
		// Calculate the differences.
		for(int i = 0; i < differenceVecs.length; i++)
			for(int j = 0; j < differenceVecs[0].length; j++)
				differenceVecs[i][j] = trainingVectors[i][j] - vector[j];
		
		// Calculate the distances.
		for(int i = 0; i < differenceVecs.length; i++)
		{
			double sum = 0;
			for(int j = 0; j < differenceVecs[0].length; j++)
				sum += Math.pow(differenceVecs[i][j], 2);
			
			distances[i] = Math.sqrt(sum);
		}
		
		// Populate the sorted distance index array.
		for(int i = 0; i < sortedDistanceIndexs.length; i++)
			sortedDistanceIndexs[i] = i;
		
		// Perform an insertion sort to get the indexes in the correct order.
		for(int i = 1; i < sortedDistanceIndexs.length; i++)
		{
			int j = i;
			while( j > 0 && distances[sortedDistanceIndexs[j-1]] > distances[sortedDistanceIndexs[j]])
			{
				int temp = sortedDistanceIndexs[j];
				sortedDistanceIndexs[j] = sortedDistanceIndexs[j-1];
				sortedDistanceIndexs[j-1] = temp;
				j--;
			}
		}
		
		// Look at the smallest k distances and determine which class the given vector is closet to.
		int sum = 0;
		for(int i = 0; i < K; i++)
			sum += trainingVectorClasses[sortedDistanceIndexs[i]] ? 1 : -1;
		
		return sum >= 0 ? true : false;
	}
	
	private void setScalingVectors(ArrayList<TrainingVector> vectors)
	{
		double[] mean = new double[n];
		double[] max = new double[n];
		double[] min = new double[n];
		double[] range = new double[n];
		
		// Initialise the arrays to set values.
		Arrays.fill(mean, 0);
		Arrays.fill(max, 0);
		Arrays.fill(min, Integer.MAX_VALUE);
		Arrays.fill(range, 0);
		
		// Get the mean and min-max values for each feature across all training examples.
		for(TrainingVector tVec: vectors)
		{
			for(int i = 0; i < n; i++)
			{
				mean[i] += tVec.getVector()[i] / m;
				
				if(tVec.getVector()[i] > max[i])
					max[i] = tVec.getVector()[i];
				
				if(tVec.getVector()[i] < min[i])
					min[i] = tVec.getVector()[i];
			}
		}
		
		// Set the range array.
		for(int i = 0; i < n; i++)
			range[i] = max[i] - min[i];
		
		// Save the vectors.
		trainingset_feature_mean = mean;
		trainingset_feature_range = range;
	}
	
	private ArrayList<TrainingVector> scale(ArrayList<TrainingVector> vectors)
	{
		// Store the scaled vectors.
		ArrayList<TrainingVector> scaledVectors = new ArrayList<TrainingVector>();
		
		// Scale all the vectors.
		for(TrainingVector tVec : vectors)
		{
			double[] scaledVector = scale(tVec.getVector());
			
			scaledVectors.add(new TrainingVector(tVec.isPositive(), scaledVector));
		}
		
		return scaledVectors;
	}
	
	private double[] scale(double[] vector)
	{
		double[] scaledVector = new double[n];
		
		for(int i = 0; i < n; i++)
			scaledVector[i] = (vector[i] - trainingset_feature_mean[i]) / (0.5 * trainingset_feature_range[i]);
		
		return scaledVector;
	}
}
