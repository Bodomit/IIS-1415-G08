import java.util.ArrayList;


public interface IClassifier {
	public void train(ArrayList<TrainingVector> vectors);
	public boolean predictSVM(double[] vector);
	public boolean predictN(ArrayList<TrainingVector> vector);
}
