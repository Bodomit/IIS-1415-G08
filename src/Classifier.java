import java.util.ArrayList;
import java.util.Arrays;

/**
 * The classifier implements an SVM using a linear kernal function.
 * @author Bodomite
 *
 */
public class Classifier 
{
	private final double TOLERANCE = 0.001;
	private final int NUMBER_OF_PASSES = 20;
	private final int C;
	
	private int n, m;
	private double[] trainingset_feature_mean;
	private double[] trainingset_feature_range;
	
	public Classifier()
	{
		C = 1000;
	}
	
	public void train(ArrayList<TrainingVector> vectors)
	{
		// Get the number of features.
		n = vectors.get(0).getVector().length;
		m = vectors.size();
		
		// Set the scaling vectors.
		setScalingVectors(vectors);
		
		// Scale all the vectors.
		ArrayList<TrainingVector> scaledVectors = scale(vectors);
		
		// Transform the TrainingVector objects into matrices.
		double[][] X = new double[m][n];
		double[] Y = new double[m];
		
		for(int i = 0; i < m; i++)
		{
			Y[i] = scaledVectors.get(i).isPositive() ? 1 : 0;
			
			for(int j = 0; j < n; j++)
				X[i][j] = scaledVectors.get(i).getVector()[j];
		}
		
		double[][] K = calculateKernalMatrix_Linear(X);
		
		
		
	}
	
	/**
	 * Sets the scaling vectors based on the training example.
	 * @param vectors The training vectors.
	 */
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
			double[] scaledVector = new double[n];
			
			for(int i = 0; i < n; i++)
				scaledVector[i] = (tVec.getVector()[i] - trainingset_feature_mean[i]) / (0.5 * trainingset_feature_range[i]);
			
			scaledVectors.add(new TrainingVector(tVec.isPositive(), scaledVector));
		}
		
		return scaledVectors;
	}
	
	private double[][] calculateKernalMatrix_Linear(double[][] X)
	{
		return matrixMultiplication(X, matrixRotation(X));
	}
	
	private double[][] matrixMultiplication(double[][] A, double[][] B)
	{
		if(A.length != B[0].length)
			throw new IllegalArgumentException("Not valid matrix dimensions to multiply.");
		
		// Initialise the result matrix.
		double[][] c = new double[A.length][B[0].length];
		
		// Populate the result matrix.
		for(int i = 0; i < A.length; i++)
			for(int j = 0; j < B[0].length; j++)
				for(int k = 0; k < A[0].length; k++)
					c[i][j] += A[i][k] * B[k][j];
		
		return c;
	}
	
	private double[][] matrixRotation(double[][] X)
	{
		double[][] x_prime = new double[X[0].length][X.length];
		
		for(int i = 0; i < X.length; i++)
			for(int j = 0; j < X[0].length; j++)
				x_prime[j][i] = X[i][j];
		
		return x_prime;
	}
}
