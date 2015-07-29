package abalone.frontend.view.g2d;

import java.awt.Color;
import java.awt.Font;

public class G2DText implements G2DObject {
	
	private String str;
	private Font font;
	private Color color;
	private G2DPoint centre;
	
	public G2DText(String string){
		this(string, new G2DPoint(0,0), Color.BLACK, new Font("myFont", Font.BOLD, 36));
	}
	
	public G2DText(String string, G2DPoint ctr){
		this(string, ctr, Color.BLACK, new Font("myFont", Font.BOLD, 36));
	}
	
	public G2DText(String string, G2DPoint ctr, Color colour, Font fnt){
		this.str = string;
		this.font = fnt;
		this.color = colour;
		this.centre = ctr;
		
	}

	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
        //Adjusts text size to scale with absCanvas.
        absCanvas.getPhysicalGraphics().setFont(font.deriveFont((float)absCanvas.physicalSize(font.getSize())));
        absCanvas.getPhysicalGraphics().setColor(color);
		int length = absCanvas.getPhysicalGraphics().getFontMetrics().stringWidth(str);
		int height = absCanvas.getPhysicalGraphics().getFontMetrics().getHeight();
		G2DPoint absPoint = new G2DPoint((absCanvas.physicalX(centre.getX())-((double)length/2)), 
											absCanvas.physicalY(centre.getY())+((double)height/3));
		absCanvas.getPhysicalGraphics().drawString(str, 
													(int)Math.round((absPoint.getX())), 
														(int)Math.round(absPoint.getY()));
		
	}

	@Override
	public G2DObject deepClone() {
		G2DText newText = new G2DText(new String(this.str));
		newText.setColor(new Color(this.color.getRGB()));
		newText.setPoint(new G2DPoint(this.centre.getX(), this.centre.getY()));
		return newText;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setStyle(int style){
		this.font = this.font.deriveFont(style);
	}
	
	public void setSize(float size){
		this.font = this.font.deriveFont(size);
	}
	
	public void setText(String string){
		this.str = string;
	}
	
	public void setPoint(G2DPoint pt){
		this.centre = pt;
	}

	@Override
	public void transform(Matrix transformationMatrix) {
		this.centre.transform(transformationMatrix);
	}

	@Override
	public void translate(double x, double y) {
		this.centre.translate(x, y);
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		this.centre.rotateAroundOrigin(degrees);
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		this.centre.rotateAroundPoint(x, y, degrees);
	}

}
