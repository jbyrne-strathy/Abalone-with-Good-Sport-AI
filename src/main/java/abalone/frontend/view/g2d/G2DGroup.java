package abalone.frontend.view.g2d;


import java.awt.Color;
import java.util.ArrayList;

public class G2DGroup implements G2DObject {
	
	private ArrayList<G2DObject> objects ;
	
	public G2DGroup(){ objects = new ArrayList<G2DObject>();}
	
	private G2DGroup(ArrayList<G2DObject> objects){
		this.objects = objects;
	}

	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		for (G2DObject obj : objects) obj.draw(absCanvas);
	}

	@Override
	public G2DGroup deepClone() {
		ArrayList<G2DObject> newObjects = new ArrayList<G2DObject>();
		for (G2DObject obj : objects)
			newObjects.add(obj.deepClone());
		return new G2DGroup(newObjects);
	}

	@Override
	public void setColor(Color color) {
	}

	@Override
	public void transform(Matrix transformationMatrix) {
		for (G2DObject o : objects) o.transform(transformationMatrix);
	}

	@Override
	public void translate(double x, double y) {
		for (G2DObject obj : objects) obj.translate(x,y);
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		for (G2DObject obj : objects) obj.rotateAroundOrigin(degrees);
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		for (G2DObject obj : objects) obj.rotateAroundPoint(x,y,degrees);
	}
	
	public void rotateAroundPoint(G2DPoint pt, double degrees){
		this.rotateAroundPoint(pt.getX(), pt.getY(), degrees);
	}
	
	public void add(G2DObject obj){
		objects.add(obj);
	}
	
	public void clear(){
		objects.clear();
	}

}
