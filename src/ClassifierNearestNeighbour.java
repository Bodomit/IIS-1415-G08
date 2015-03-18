import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ClassifierNearestNeighbour implements IClassifier {
	
	private double[][] trainingVectors;
	private boolean[] trainingVectorClasses;
	private final int K;
	
	public ClassifierNearestNeighbour(int K)
	{
		this.K = K;
	}
	
	@Override
	public void train(ArrayList<TrainingVector> vectors) 
	{
		// Store the vectors.
		trainingVectors = new double[vectors.size()][vectors.get(0).getVector().length];
		trainingVectorClasses = new boolean[vectors.size()];
		
		for(int i = 0; i < vectors.size(); i++)
		{
			trainingVectors[i] = vectors.get(i).getVector();
			trainingVectorClasses[i] = vectors.get(i).isPositive();
		}
		
	}

	@Override
	public boolean predict(double[] vector)
	{
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
}
