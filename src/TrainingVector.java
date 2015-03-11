
public class TrainingVector 
{
	private final boolean isPositive;
	private final double[] vector;
	
	public TrainingVector(boolean isPositive, double[] vector) {
		this.isPositive = isPositive;
		this.vector = vector;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public double[] getVector() {
		return vector;
	}
}
