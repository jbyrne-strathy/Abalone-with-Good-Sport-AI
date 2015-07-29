package abalone.frontend.view.g2d;


import java.awt.Color;

/*
 * A rectangle class, similar to G2DPolygon but is
 * constructed by passing in only 2 points for
 * opposite corners.
 * 
 * Originally, this constructed a G2DPolygon and
 * filled it in but after I created G2DShape, it
 * made more sense to have it simply extend that.
 */

public class G2DRectangle extends G2DShape {
	
	public G2DRectangle(G2DPoint topLeft, G2DPoint bottomRight){
		this(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY(), Color.BLACK, true);
	}
	public G2DRectangle(G2DPoint topLeft, G2DPoint bottomRight, Color color, boolean fill){
		this(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY(), color, fill);
	}
	public G2DRectangle(double x1, double y1, double x2, double y2){
		this(x1,y1,x2,y2,Color.BLACK, true);
	}
	public G2DRectangle(double x1, double y1, double x2, double y2, Color color, boolean fill){
		this.setWeight(1);
		this.setFill(fill);
		this.color = color;
		this.points.add(new G2DPoint(x1, y1));
		this.points.add(new G2DPoint(x2, y1));
		this.points.add(new G2DPoint(x2, y2));
		this.points.add(new G2DPoint(x1, y2));
	}

	@Override
	public G2DRectangle deepClone() {
		G2DRectangle newRect = new G2DRectangle(points.get(0).deepClone(), points.get(2).deepClone(), new Color(this.color.getRGB()), this.fill);
		return newRect;
	}
	
	public G2DPoint getStartPoint(){
		return this.points.get(0);
	}
	
	public G2DPoint getEndPoint(){
		return this.points.get(2);
	}

}
