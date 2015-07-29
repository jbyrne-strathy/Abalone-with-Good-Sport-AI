package abalone.frontend.view.g2d;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/*
 * A point object implementing G2DObject
 * This isn't much use for drawing on screen but is used extensively 
 * to define other objects
 */
public class G2DPoint implements G2DObject {

	private Matrix ptMatrix;
	private Color color = Color.BLACK;
	
	// -- Constructors
	
	public G2DPoint(double x, double y){
		ptMatrix = new Matrix(3,1);//create homogeneous 2D coordinate vector - a 3row 1col matrix
		this.setX(x);
		this.setY(y);
		ptMatrix.set(2, 0, 1);
	}
	
	public G2DPoint(double x, double y, Color color){
		this(x,y);
		this.setColor(color);
	}
	
	// -- other methods
	
	public void setColor(Color color){
		this.color = color;
	}
	
	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		absCanvas.getPhysicalGraphics().setColor(color);
		int px = absCanvas.physicalX(getX());
		int py = absCanvas.physicalY(getY());
		//Set a default stroke weight if point is drawn directly on canvas.
		Graphics2D G2D = absCanvas.getPhysicalGraphics(); 
		G2D.setColor(color);
		G2D.setStroke(new BasicStroke(3));
		absCanvas.getPhysicalGraphics().drawLine(px, py, px,py);
	}

	@Override
	public G2DPoint deepClone() {
		return new G2DPoint(getX(), getY(), new Color(color.getRGB()));
	}

	double getX() {
		return ptMatrix.get(0, 0);
	}

	void setX(double x) {
		ptMatrix.set(0, 0, x);
	}

	double getY() {
		return ptMatrix.get(1, 0);
	}

	void setY(double y) {
		ptMatrix.set(1, 0, y);
	}

	/*
	 * The shapes themselves don't need to do any
	 * calculations but simply pass the numbers into
	 * G2DPoint and all of the matrix calculations 
	 * are done here. 
	 */

	private double cos(double degrees){
		return Math.cos(Math.toRadians(degrees));
	}
	
	private double sin(double degrees){
		return Math.sin(Math.toRadians(degrees));
	}
	
	@Override
	public void transform(Matrix transformationMatrix) {
		ptMatrix = transformationMatrix.multiply(ptMatrix);
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		this.rotateAroundPoint(0.0, 0.0, degrees);
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		this.translate(-x, -y);
		double[][] rotation = { {cos(degrees), -sin(degrees), 0.0}, 
				{sin(degrees), cos(degrees), 0.0}, 
					{0.0, 0.0, 1.0} };
		this.transform(new Matrix(rotation));
		this.translate(x, y);
	}

	@Override
	public void translate(double x, double y) {
		double[][] translation = { {1.0, 0.0, x} , {0.0, 1.0, y}, {0.0, 0.0, 1.0} };
		this.transform(new Matrix(translation));
	}

}
