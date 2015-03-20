import java.util.ArrayList;


public interface IClassifier {
	public void train(ArrayList<TrainingVector> vectors, boolean isVerbose);
	public boolean predict(double[] vector);
}
