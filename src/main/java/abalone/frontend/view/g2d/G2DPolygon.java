package abalone.frontend.view.g2d;


import java.awt.Color;

/*
 * A Graphics 2D Polygon 
 * Defined as a set of G2DPoints 
 * On construction an empty ArrayList of points is created then
 * addPoint is called to add new points.
 * 
 */
public class G2DPolygon extends G2DShape {
	
	// -- Constructors 
	public G2DPolygon(){
		this(Color.BLACK);
		this.setWeight(1);
	}
	
	public G2DPolygon(Color color){
		this.color = color;
		this.weight = 1;
	}
	
	// -- other functions

	public void addPoint(G2DPoint pt){
		points.add(pt);
	}
		
	public void addPoint(double x, double y) {
		addPoint(new G2DPoint(x,y));
	}

	@Override
	public G2DPolygon deepClone() {
		G2DPolygon newPoly = new G2DPolygon(new Color(this.color.getRGB()));
		for (G2DPoint pt : points )
			newPoly.addPoint(pt.deepClone());
		newPoly.setFill(this.fill);
		newPoly.setWeight(this.weight);
		return newPoly;
	}
}
