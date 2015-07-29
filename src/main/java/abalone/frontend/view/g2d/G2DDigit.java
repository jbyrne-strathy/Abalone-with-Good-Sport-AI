package abalone.frontend.view.g2d;

import java.awt.Color;
import java.util.ArrayList;
/*
 * Class to handle individual digits on the stopwatch.
 * Basically a very stripped-down equivalent of G2DGroup but
 * specifies lines rather than objects.
 */
public class G2DDigit implements G2DObject{

	private ArrayList<G2DLine> lines = new ArrayList<G2DLine>();
	
	public void add(G2DLine ln){
		lines.add(ln);
	}

	/*
	 * Sets the visibility on each line, to represent the
	 * required digit.
	 */
	public void setDigit(boolean[] points){
		for(int i = 0; i < points.length; i++){
			if(true == points[i]){
				lines.get(i).setColor(new Color(0,0,0,255));
			} else {
				lines.get(i).setColor(new Color(0,0,0,0));
			}
		}
	}

	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		for(G2DLine ln : lines) ln.draw(absCanvas);
	}

	@Override
	public G2DLine deepClone() {
		return null;
	}

	@Override
	public void setColor(Color color) {
		for(G2DLine ln : lines) ln.setColor(color);
	}

	@Override
	public void transform(Matrix transformationMatrix) {
		for (G2DLine ln : lines) ln.transform(transformationMatrix);
	}

	@Override
	public void translate(double x, double y) {
		for (G2DLine ln : lines) ln.translate(x,y);
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		for (G2DLine ln : lines) ln.rotateAroundOrigin(degrees);
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		for (G2DLine ln : lines) ln.rotateAroundPoint(x,y,degrees);
	}
}
