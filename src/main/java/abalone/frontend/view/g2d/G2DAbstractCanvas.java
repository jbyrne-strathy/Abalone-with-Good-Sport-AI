package abalone.frontend.view.g2d;


import java.awt.Graphics;
import java.awt.Graphics2D;

/* 
 * Implements an abstract canvas to draw on
 * The idea is that G2D objects are drawn in an abstract space that is always the same size
 * This class then converts to "physical" coordinates when drawing into an actual Canvas Graphics
 * For ease of use when scaling and rotating, the abstract space is measured in doubles not ints
 * However, the physical space is integers
 * 
 * This version does uncentred isotropic (aspect-ratio preserving) scaling
 * v2.0
 */
public class G2DAbstractCanvas {
	private double abstractWidth,abstractHeight;// the size of the abstract canvas we draw into
	private int physicalWidth, physicalHeight;// the size of the real Canvas that we have to draw into
	private Graphics physicalGraphics = null; // the graphics we draw into - warning - changes on each paint - setPhysical must be called !
	private double scaleX = 1.0, scaleY = 1.0;
	
	public G2DAbstractCanvas(double abstractWidth, double abstractHeight){
		this.abstractWidth = abstractWidth;
		this.abstractHeight = abstractHeight;
	}

	/*
	 * Sets up the size of the physical display and the Graphics into which objects should draw
	 * The "paint" method of a Canvas gets passed the Graphics each time - this method MUST
	 * be called at the start of Paint
	 */
	public void setPhysicalDisplay(int physicalWidth, int physicalHeight, Graphics physicalGraphics){
		this.physicalWidth = physicalWidth;
		this.physicalHeight = physicalHeight;
		this.physicalGraphics = physicalGraphics;
		scaleX = physicalWidth / abstractWidth ;
		scaleY = physicalHeight / abstractHeight ;
		double isoScale = Math.min(scaleX, scaleY);
		scaleX = isoScale;
		scaleY = isoScale;
	}
	
	public int physicalX(double abstractX){
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return (int) Math.round( (abstractX  * scaleX) + ((physicalWidth-(abstractWidth*scaleX))/2));
	}
	public double abstractX(int physicalX){
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return physicalX / scaleX;
	}
	
	public int physicalY(double abstractY){
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return (int) Math.round((abstractY * scaleY) + ((physicalHeight-(abstractHeight*scaleY))/2));
	}
	public double abstractY(int physicalY){
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return physicalY / scaleY;
	}
	/*
	 * Created this for G2DText.
	 */
	public int physicalSize(double abstractSize){
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return (int) Math.round(abstractSize*scaleX);		
	}
	
	public double getAbstractWidth() {
		return abstractWidth;
	}

	public void setAbstractWidth(int width) {
		this.abstractWidth = width;
	}

	public double getAbstractHeight() {
		return abstractHeight;
	}

	public void setAbstractHeight(int height) {
		this.abstractHeight = height;
	}
	public Graphics2D getPhysicalGraphics() {
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return (Graphics2D) physicalGraphics;
	}

	public int getPhysicalWidth() {
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return physicalWidth;
	}

	public int getPhysicalHeight() {
		if (physicalGraphics==null) throw new RuntimeException("physical graphics should be set before calculating scalings");
		return physicalHeight;
	}
}
