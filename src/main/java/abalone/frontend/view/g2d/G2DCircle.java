package abalone.frontend.view.g2d;

import java.awt.*;

/*
 * A class that draws a circle.
 * @author James Byrne.
 */
public class G2DCircle extends G2DShape {
	
	private int diameter;

	/**
	 * Creates a circle of size 0 at the top-left corner.
	 * Default settings: weight = 1, colour = black, fill = false.
	 */
	public G2DCircle(){
		this(0,0,0,1,Color.BLACK,false);	
	}

	/** 
	 * Creates a circle centred at the given coordinates, with the given radius.
	 * Default settings: weight = 1, colour = black, fill = false. 
	 * @param x The centre X-coordinate
	 * @param y The centre Y-coordinate
	 * @param radius The circle's radius
	 */
	public G2DCircle(double x, double y, double radius){
		this(x,y,radius,1,Color.BLACK,false);
	}

	/**
	 *  Creates a circle centred at the given coordinates, with the given radius,
	 *  outline weight, colour and fill settings.
	 * @param x The centre X-coordinate
	 * @param y The centre Y-coordinate
	 * @param radius The circle's radius
	 * @param weight The thickness of the outline
	 * @param colour The colour of the circle & outline
	 * @param fill Whether or not the circle should be filled.                  
	 */
	public G2DCircle(double x, double y, double radius, int weight, Color colour, Boolean fill){
		this.points.add(new G2DPoint(x-radius,y-radius));
		this.setDiameter((int) radius*2);
		this.setColor(colour);
		this.setFill(fill);
		this.setWeight(weight);
	}

	@Override
	public G2DCircle deepClone() {
		G2DCircle newCircle = new G2DCircle(points.get(0).getX() + ( this.diameter/2 ),
                                                points.get(0).getY() + ( diameter/2 ),
												    diameter/2, weight, color, fill );
		return newCircle;
	}
	
	public void setDiameter(int diameter){
		this.diameter = diameter;
	}

	/**
	 * @return An awt point with the current top-left position of this circle.
	 */
	public Point getPoint(){
		Point reply = new Point();
		reply.setLocation(points.get(0).getX(), points.get(0).getY());
		return reply;
	}

    /**
     * Replaces the current G2DPoint with a new one at the given point.
     * @param point The point to set.
     */
    public void setPoint(Point point){
        G2DPoint newPoint = new G2DPoint(point.getX(), point.getY());
        points.clear();
        points.add(newPoint);
    }

	/**
	 * @return The diameter of this circle.
	 */
	public int getDiameter(){
		return diameter;
	}
	
	/**
	 * Added this to override G2DShape.
	 * Draws nicer circles when anti-aliasing is enabled,
	 * than my original G2DCircle did.
	 * @param absCanvas The abstract canvas.
	 */
	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		Graphics2D G2D = absCanvas.getPhysicalGraphics();
		G2D.setColor(color);
		G2D.setStroke(new BasicStroke(weight));
		int x = absCanvas.physicalX(points.get(0).getX());
		int y = absCanvas.physicalY(points.get(0).getY());
		int size = absCanvas.physicalSize(diameter);
		if(fill)
			G2D.fillOval(x, y, size, size);
		else
			G2D.drawOval(x, y, size, size);
	}
}
