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
	private double b;
	private double[] W;
	double[][] K;
	private double[] trainingset_feature_mean;
	private double[] trainingset_feature_range;
	private double[] alphas = new double[m];
	
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
		
		// Get the kernal matrix.
		K = calculateKernalMatrix_Linear(X);
		
		// Store E;
		double[] E = new double[m];
		
		// Start the training.
		int passNum = 0;
		int numberOfChangedAlphas = 0;
		while(passNum < NUMBER_OF_PASSES)
		{
			for(int i = 0; i < m; i++)
			{
				// Calculate E_i
				E[i] = calculateE_i(i, X, Y);
				
				if((Y[i] * E[i] < -TOLERANCE && alphas[i] < C) || (Y[i]*E[i] > TOLERANCE && alphas[i] > 0))
				{
					// Get j randomly, but make sure that it does not equal i.
					int j;
					do{ 
						j = (int) Math.floor(m * Math.random());
					}
					while(i == j);
					
					// Calculate E_j.
					E[j] = calculateE_i(j, X, Y);
					
					// Save the old alphas.
					double alpha_i_old = alphas[i];
					double alpha_j_old = alphas[j];
					
					// Calculate L and H.
					double L = Math.max(0, alphas[j] - alphas[i]);
					double H = Math.max(C, C + alphas[j] - alphas[i]);
					
					// If L == H then continue to the next i.
					if(L == H)
						continue;
					
					// Calculate eta.
					double eta = 2 * K[i][j] - K[i][i] - K[j][j];
					
					if(eta > 1)
						continue;
					
					// Compute new value for alpha j.
					alphas[j] = alphas[j] - (Y[j] * (E[i] - E[j])) / eta;
					
					// Clip the new value.
					alphas[j] = Math.min(H, alphas[j]);
					alphas[j] = Math.max(L, alphas[j]);
					
					// Check if the change in alpha is significant.
					if(Math.abs(alphas[j] - alpha_j_old) < TOLERANCE)
					{
						alphas[j] = alpha_j_old;
						continue;
					}
					
					// Compute b1 and b2.
					double b1 = b - E[i] - Y[i] * (alphas[i] - alpha_i_old) * K[i][j]
										 - Y[j] * (alphas[j] - alpha_j_old) * K[i][j];
					
					double b2 = b - E[i] - Y[i] * (alphas[i] - alpha_i_old) * K[i][j]
							 			 - Y[j] * (alphas[j] - alpha_j_old) * K[j][j];
					
					// Select b.
					if(0 < alphas[i] && alphas[i] < C)
						b = b1;
					else if (0 < alphas[j] && alphas[j] < C)
						b = b2;
					else
						b = (b1+b2)/2;
					
					numberOfChangedAlphas++;
				}
			}
			
			if(numberOfChangedAlphas == 0)
				passNum++;
			else
				passNum = 0;
		}
		
		System.out.println("Done...");
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
	
	private double  calculateE_i(int i, double[][] X, double[] Y)
	{
		double E_i;
		double[] K_i = getMatrixColumn(K, i);
		
		double sum = 0;
		for(int j = 0; j < alphas.length; j++)
		{
			sum += alphas[j] * Y[j] * K_i[j];
		}		
		
		E_i = sum - Y[i];
		
		return E_i;
	}
	
	private double[] getMatrixColumn(double[][] X, int i)
	{
		double[] X_ci = new double[X.length];
		
		for(int j = 0; j < X_ci.length; j++)
			X_ci[j] = X[j][i];
		
		return X_ci;
	}

}
