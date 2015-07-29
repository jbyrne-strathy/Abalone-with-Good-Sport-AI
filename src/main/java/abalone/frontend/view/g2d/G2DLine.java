package abalone.frontend.view.g2d;


import java.awt.BasicStroke;
import java.awt.Color;

/*
 * A Graphics 2D Line - defined as a start and end point
 */
public class G2DLine implements G2DObject {
	
	private G2DPoint start, end;
	private Color color = Color.BLACK;
	private int weight = 1;
	
	// -- Constructors
	
	public G2DLine(G2DPoint start, G2DPoint end){
		this.start = start;
		this.end = end;
	}
	
	public G2DLine(G2DPoint start, G2DPoint end, Color color, int weight){
		this(start,end); setColor(color); setWeight(weight);
	}
	
	// -- other methods
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setWeight(int w){
		this.weight = w;
	}

	public G2DLine(double x1, double y1, double x2, double y2){
		this(new G2DPoint(x1,y1), new G2DPoint(x2,y2));
	}

	public G2DLine(double x1, double y1, double x2, double y2, Color color, int weight){
		this(new G2DPoint(x1,y1), new G2DPoint(x2,y2), color, weight);
	}

	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		absCanvas.getPhysicalGraphics().setColor(color);
		absCanvas.getPhysicalGraphics().setStroke(new BasicStroke(weight));
		int px1 = absCanvas.physicalX(start.getX());
		int py1 = absCanvas.physicalY(start.getY());
		int px2 = absCanvas.physicalX(end.getX());
		int py2 = absCanvas.physicalY(end.getY());
		absCanvas.getPhysicalGraphics().drawLine(px1, py1, px2, py2);
	}

	@Override
	public G2DLine deepClone() {
		return new G2DLine(start.deepClone(),end.deepClone(), new Color(color.getRGB()), weight);
	}

	G2DPoint getStart() {
		return start;
	}

	void setStart(G2DPoint start) {
		this.start = start;
	}

	G2DPoint getEnd() {
		return end;
	}

	void setEnd(G2DPoint end) {
		this.end = end;
	}

	@Override
	public void transform(Matrix transformationMatrix) {
		//Nothing needs done here.
	}

	@Override
	public void translate(double x, double y) {
		start.translate(x, y);
		end.translate(x, y);
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		start.rotateAroundOrigin(degrees);
		end.rotateAroundOrigin(degrees);
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		start.rotateAroundPoint(x, y, degrees);
		end.rotateAroundPoint(x, y, degrees);
	}
	
	public void rotateAroundPoint(G2DPoint pt, double degrees) {
		start.rotateAroundPoint(pt.getX(), pt.getY(), degrees);
		end.rotateAroundPoint(pt.getX(), pt.getY(), degrees);
	}

}
