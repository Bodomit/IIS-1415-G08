import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ClassifierN implements IClassifier {
	
	public ClassifierN()
	{
		
	}

	List<Double> result;
	
	@Override
	public void train(ArrayList<TrainingVector> vectors) {
		
		//add the training image vectorD into the result
		
		result = new ArrayList<Double>();
		for (double d : vectorD(vectors)) {
			result.add(d);
		}
		
		
		
	}
	
	//find vector T
	private double [] vectorT(ArrayList<TrainingVector> vectors)
	{
		double a= 0,p= 0;		
		for(int i = 0; i < vectors.size(); i++)
		{
			a = a + vectors.get(i).getVector()[0];
			p = p + vectors.get(i).getVector()[1];
		}
		
		a = a/vectors.size();
		p = p/vectors.size();
		
		double [] vT = {a,p};
		return vT;
	}
	
	//calculate the diffrence vectors
	private double [] vectorD(ArrayList<TrainingVector> vectors)
	{
		double [] vD = {};
		for (int i = 0; i < vectors.size(); i ++)
		{
			vD[i] = Math.sqrt(Math.pow((vectorT(vectors)[0] - vectors.get(i).getVector()[0]), 2) 
					+ Math.pow((vectorT(vectors)[1] - vectors.get(i).getVector()[1]), 2));
		}		
		
		return vD;
	}

	@Override
	public boolean predictSVM(double[] vector) {
		return false;
	}
	@Override
	public boolean predictN(ArrayList<TrainingVector> vector)
	{
		//find the vectorD of the compare image
		
		double [] vectorC = vectorD(vector);
		
		//add the compare image vectorD into the result
		for (double d : vectorD(vector)) {
			result.add(d);
		}
		//sort the result
		Collections.sort(result);
		
		List<Double> vectorCList = new ArrayList<Double>();
		for (double d : vectorC) {
			vectorCList.add(d);
		}
		
		//compare image
		int contain = 0;
		contain = contain + (vectorCList.contains(result.get(0)) ? 1 : 0);
		contain = contain + (vectorCList.contains(result.get(1)) ? 1 : 0);
		contain = contain + (vectorCList.contains(result.get(2)) ? 1 : 0);
		
		return contain >= 2;
	}
	
	

}
