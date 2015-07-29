package abalone.frontend.view.g2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

/*
 * An abstract shape class.  All of the G2D shape classes contained
 * all of the methods below, which were identical, so I created this
 * as a superclass that the others will extend.  This will make it
 * considerably easier to add or modify new shared methods and/or
 * new shape classes.
 */

public abstract class G2DShape implements G2DObject {
	
	protected ArrayList<G2DPoint> points = new ArrayList<G2DPoint>();
	protected Color color = Color.BLACK;
	protected boolean fill = true;
	protected int weight = 1;
	
	@Override
	public abstract G2DObject deepClone();
	
	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		// First create a polygon of physical points 
		Polygon awtPolygon = new Polygon();
		for (G2DPoint absPt : points )
			awtPolygon.addPoint(absCanvas.physicalX(absPt.getX()), absCanvas.physicalY(absPt.getY()));
		// Draw the polygon on the physical graphics with AWT, with the settings
		// defined in G2DCircle
		Graphics2D G2D = absCanvas.getPhysicalGraphics(); 
		G2D.setColor(color);
		G2D.setStroke(new BasicStroke(weight));
		if(fill)
			G2D.fillPolygon(awtPolygon);
		else
			G2D.drawPolygon(awtPolygon);
	}
	
	public void setWeight(int w){
		this.weight = w;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public void setFill(Boolean fill){
		this.fill = fill;
	}

	@Override
	public void transform(Matrix transformationMatrix) {
		//Nothing needs done here.
	}

	@Override
	public void translate(double x, double y) {
		for (G2DPoint pt : points) pt.translate(x, y);
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		for (G2DPoint pt : points) pt.rotateAroundOrigin(degrees);
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		for (G2DPoint pt : points) pt.rotateAroundPoint(x, y, degrees);		
	}

}
