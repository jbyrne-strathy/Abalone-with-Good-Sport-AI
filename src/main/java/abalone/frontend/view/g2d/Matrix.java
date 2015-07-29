package abalone.frontend.view.g2d;

public class Matrix {
	
	private double[][] thematrix;
	private int rows, cols;

	public Matrix(double[][] matrixAsArray){
		this(matrixAsArray.length, matrixAsArray[0].length);
		this.thematrix = matrixAsArray;
	}
	
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.thematrix = new double[rows][cols];
	}
	
	public void set(int row, int col, double value) {
		thematrix[row][col]=value;
	}
	
	public double get(int row, int col) {
		return thematrix[row][col];
	}
	
	public Matrix multiply(Matrix other){
		if (this.cols!=other.rows) throw new RuntimeException("Cannot multiply a "+this.shapeString()+
																									" by a "+ other.shapeString()+" matrix");
		Matrix result = new Matrix(this.rows, other.cols);
		for (int r=0; r<this.rows; r++)
			for (int c=0; c<other.cols; c++){
				// set result[r][c] to dot product of this.r * other.c
				double dot = 0;
				for (int i=0; i<this.cols; i++)
					dot += this.thematrix[r][i] * other.thematrix[i][c];
				result.thematrix[r][c]=dot;
			}
		return result;
	}

	private String shapeString() {
		return rows+"x"+cols;
	}
	public String toString(){
		StringBuffer s = new StringBuffer();
		for (int r=0; r<rows; r++) {
			s.append("[ ");
			for (int c=0; c<cols; c++)
				s.append(String.format("%1$.2f", thematrix[r][c])+" ");
			s.append("]");
		}
		return "[ "+s+" ]";
	}

}
