package abalone.frontend.view.g2d;


import java.awt.Canvas;

/*
 *  This is a Rolex Oyster with matrix transformations. 
 *  
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MySecondCanvas extends Canvas implements MouseListener, MouseMotionListener{
	
	/**
	 * Added Serial Version for serialization as Canvas is Serialisable - auto generated as version 1
	 */
	private static final long serialVersionUID = 1L;
	
	private G2DAbstractCanvas absCanvas = new G2DAbstractCanvas(500,500);
	private ArrayList<G2DObject> drawings = new ArrayList<G2DObject>();
	private G2DGroup hour, minute, second;
	private double height = absCanvas.getAbstractHeight();
	private double width = absCanvas.getAbstractWidth();
	private G2DPoint centre = new G2DPoint(width/2, height/2);
	private Color silver = new Color(192,192,192);
	private Color gold = new Color(238,201,0);
	private boolean useSystemTime, running;
	private G2DRectangle timeFwd, timeBack, systemTime, start, stop;
	private Timer timer; 
	private G2DStopWatch stopWatch;
	
	public MySecondCanvas(){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.useSystemTime = true;
		this.running = true;
		this.drawFace();
		timer = new Timer();
		timer.scheduleAtFixedRate(new Ticker(), 0, 1000);
	}
	/*
	 * Creates the entire clock face.
	 */
	private void drawFace(){
		this.drawings.clear();
		
		// Draw the Clock face, and buttons and labels.
		this.drawings.add(timeFwd = new G2DRectangle(centre.getX()*1.6, (centre.getY()/2)-50, (centre.getX()*1.85), centre.getY()/2));
		this.drawings.add(timeBack = new G2DRectangle(centre.getX()*1.6, (centre.getY()*1.5), (centre.getX()*1.85), (centre.getY()*1.5)+50));
		this.drawings.add(systemTime = new G2DRectangle(centre.getX()*1.75, (centre.getY()-25), (centre.getX()*2.05), centre.getY()+25));
		this.drawings.add(start = new G2DRectangle(centre.getX()*0.16, (centre.getY()/2)-50, (centre.getX()*0.4), centre.getY()/2));
		this.drawings.add(stop = new G2DRectangle(centre.getX()*0.16, (centre.getY()*1.5), (centre.getX()*0.4), (centre.getY()*1.5)+50));
		this.drawings.add(new G2DCircle(centre.getX(), centre.getY(),(height/2)-10, 2, silver, true));
		this.drawings.add(new G2DCircle(centre.getX(), centre.getY(),(height/2)-10, 11, Color.BLACK, false));
		this.drawings.add(new G2DCircle(centre.getX(), centre.getY(),(height/2)-10, 10, gold, false));
		G2DText fwd = new G2DText("Time Fwd", new G2DPoint(centre.getX()*1.4, (centre.getY()/2)-25));
		fwd.setColor(Color.BLACK);
		fwd.setSize(16);
		this.drawings.add(fwd);
		G2DText back = new G2DText("Time Back", new G2DPoint(centre.getX()*1.5, (centre.getY()*1.5)+25));
		back.setColor(Color.BLACK);
		back.setSize(16);
		this.drawings.add(back);
		G2DText reset = new G2DText("System Time", new G2DPoint(centre.getX()*1.65, centre.getY()-15));
		reset.setColor(Color.BLACK);
		reset.setSize(16);
		this.drawings.add(reset);
		G2DText strt = new G2DText("Start / Lap", new G2DPoint(centre.getX()*0.5, (centre.getY()/2)-25));
		strt.setColor(Color.BLACK);
		strt.setSize(16);
		this.drawings.add(strt);
		G2DText stp = new G2DText("Stop / Reset", new G2DPoint(centre.getX()*0.55, (centre.getY()*1.5)+25));
		stp.setColor(Color.BLACK);
		stp.setSize(16);
		this.drawings.add(stp);
		
		//Draw the numbers and dots between them.
		for(int i = 1; i <= 60; i++){
			G2DPoint pt = new G2DPoint(centre.getX(), height/15);
			pt.rotateAroundPoint(centre.getX(), centre.getY(), (360.0/60)*i);
			if(0 == (i % 5)) {
				G2DText txt = new G2DText(Integer.toString(i/5));
				txt.setColor(Color.BLACK);
				txt.setSize(20);
				txt.setPoint(pt);
				this.drawings.add(txt);
			} else {
				this.drawings.add(pt);
			}
		}
		
		//Digital stopwatch.
		this.drawings.add(new G2DRectangle(125, 310, 375, 390, Color.WHITE, true));
		this.drawings.add(stopWatch = new G2DStopWatch(130, 320));
		
		//Prepare the hands.
		this.drawings.add(this.hour = new G2DGroup());
		this.drawings.add(this.minute = new G2DGroup());
		this.drawings.add(this.second = new G2DGroup());
		this.setSystemTime();
	}
	
	/*
	 * Creates the hands, displaying the current system time.
	 */
	private void setSystemTime(){
		//Clear hands and redraw them to noon.
		this.hour.clear();
		this.minute.clear();
		this.second.clear();		
		
		//hour hand
		G2DPolygon h1 = new G2DPolygon(Color.BLACK);
		h1.setWeight(6);
		h1.setFill(false);
		h1.addPoint((height/2)-5, height/2);
		h1.addPoint((height/2)-20, (height/3));
		h1.addPoint((height/2), (height/4));
		h1.addPoint((height/2)+20, (height/3));
		h1.addPoint((height/2)+5, height/2);
		this.hour.add(h1);
		G2DPolygon h2 = h1.deepClone();
		h2.setColor(gold);
		h2.setWeight(5);
		this.hour.add(h2);

		//minute hand
		G2DPolygon m1 = new G2DPolygon(Color.BLACK);
		m1.setWeight(6);
		m1.setFill(false);
		m1.addPoint((height/2)-5, height/2);
		m1.addPoint((height/2)-10, (height/6));
		m1.addPoint((height/2), (height/8));
		m1.addPoint((height/2)+10, (height/6));
		m1.addPoint((height/2)+5, height/2);
		this.minute.add(m1);
		G2DPolygon m2 = m1.deepClone();
		m2.setWeight(5);
		m2.setColor(gold);
		this.minute.add(m2);

		//second hand
		second.add(new G2DLine(height/2, height/2, height/2, height/25, Color.BLACK, 4));
		second.add(new G2DLine(height/2, height/2, height/2, height/25, gold, 3));
		
		//circle to cover the centre where the hands meet.
		this.drawings.add(new G2DCircle(centre.getX(), centre.getY(), height/25, 2, Color.BLACK, false));
		this.drawings.add(new G2DCircle(centre.getX(), centre.getY(), height/25, 1, gold, true));

		//Get the current system time
		GregorianCalendar now = new GregorianCalendar();		
		now.setTimeInMillis(System.currentTimeMillis());
		double hr = now.get(GregorianCalendar.HOUR);
		double min = now.get(GregorianCalendar.MINUTE);
		double sec = now.get(GregorianCalendar.SECOND);

		//Rotate the hands to the current time
		double secRot = sec*(360.0/60);
		double minRot = (min*(360.0/60))+(secRot/60);
		double hrRot = (hr*(360.0/12))+((minRot/12));
		this.second.rotateAroundPoint(centre, secRot);
		this.minute.rotateAroundPoint(centre, minRot);
		this.hour.rotateAroundPoint(centre, hrRot);
	}
	
	private Image bufferImage;
	/* 
	 * Paint the scene from the ArrayList of objects into the given physical graphics
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 * 
	 * Added antialiasing ability using RenderingHints class, following example at
	 * http://www.exampledepot.com/egs/java.awt/AntiAlias.html
	 */
	private Graphics2D buffer;
	private boolean canPaint = true;
	public void paint(Graphics g){
		if (canPaint){ //Lock out other threads while currently running
			canPaint = false;
			if(this.isDisplayable()){
				bufferImage = createImage(getWidth(), getHeight() );
				
				buffer = (Graphics2D) bufferImage.getGraphics();
				//Create the buffer and draw to the graphics
				absCanvas.setPhysicalDisplay(getWidth(), getHeight(), buffer);
				buffer.clearRect(0, 0, getWidth(), getHeight());
				//Turn antialiasing on
				buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				buffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				for (G2DObject drawing : this.drawings ){
					drawing.draw(absCanvas);
				}
				g.drawImage(bufferImage, 0, 0, null); 
			}
			canPaint = true; //Unlock as now finished.
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//Move the time forward.
		if(arg0.getX() > absCanvas.physicalX(timeFwd.getStartPoint().getX())
				&& arg0.getY() > absCanvas.physicalY(timeFwd.getStartPoint().getY())
				&& arg0.getX() < absCanvas.physicalX(timeFwd.getEndPoint().getX())
				&& arg0.getY() < absCanvas.physicalY(timeFwd.getEndPoint().getY())){
			this.running = false;
			this.useSystemTime = false;
			new Thread(new Rotate()).start();
			//Move the time back.
		}else if(arg0.getX() > absCanvas.physicalX(timeBack.getStartPoint().getX())
				&& arg0.getY() > absCanvas.physicalY(timeBack.getStartPoint().getY())
				&& arg0.getX() < absCanvas.physicalX(timeBack.getEndPoint().getX())
				&& arg0.getY() < absCanvas.physicalY(timeBack.getEndPoint().getY())){
			this.running = false;
			this.useSystemTime = false;
			new Thread(new RotateBack()).start();
			//Reset to system time.
		}else if(arg0.getX() > absCanvas.physicalX(systemTime.getStartPoint().getX())
				&& arg0.getY() > absCanvas.physicalY(systemTime.getStartPoint().getY())
				&& arg0.getX() < absCanvas.physicalX(systemTime.getEndPoint().getX())
				&& arg0.getY() < absCanvas.physicalY(systemTime.getEndPoint().getY())){
			this.useSystemTime = true;
			this.tickCount = 0;
			//Start the stopwatch or get lap time
		}else if(arg0.getX() > absCanvas.physicalX(start.getStartPoint().getX())
				&& arg0.getY() > absCanvas.physicalY(start.getStartPoint().getY())
				&& arg0.getX() < absCanvas.physicalX(start.getEndPoint().getX())
				&& arg0.getY() < absCanvas.physicalY(start.getEndPoint().getY())){
			stopWatch.start();
			//Stop or reset the stopwatch.
		}else if(arg0.getX() > absCanvas.physicalX(stop.getStartPoint().getX())
				&& arg0.getY() > absCanvas.physicalY(stop.getStartPoint().getY())
				&& arg0.getX() < absCanvas.physicalX(stop.getEndPoint().getX())
				&& arg0.getY() < absCanvas.physicalY(stop.getEndPoint().getY())){
			stopWatch.stop();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
			this.running = true;
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}
	
	/*
	 * Ticks the clock forward by 1 second.	
	 * Does so by just moving the hands a little, to save system resources
	 * of constantly checking the system time and redrawing the whole canvas.
	 * Resets to system time every minute, assuming the time hasn't been
	 * manually changed by the user or if the user has reset to system time
	 */
	private int tickCount = 0;
	private class Ticker extends TimerTask{
		@Override
		public void run() {
			if(running && canPaint){
				if(useSystemTime && (0 == tickCount % 60)){
					tickCount = 1;
					setSystemTime();
				}else{
					tickCount++;
					second.rotateAroundPoint(centre, 360.0/60);
					minute.rotateAroundPoint(centre, (360.0/60)/60);
					hour.rotateAroundPoint(centre, ((360.0/60)/60)/12);
				}
				paint(getGraphics());
			}
		}
	}
	
	/*
	 * Rotates the minute and hour hands forward.
	 */
	private class Rotate implements Runnable{
		@Override
		public void run(){
			while(!running){
				try {
					Thread.sleep(20);
					minute.rotateAroundPoint(centre, 360.0/60);						
					hour.rotateAroundPoint(centre, (360.0/60)/12);
					paint(getGraphics());
				} catch (InterruptedException e) {
					System.out.println("Interrupted Exception: "+e.getMessage());
				}
			}
		}
	}
	
	/*
	 * Rotates the minute and hour hands backward.
	 */
	private class RotateBack implements Runnable{

		@Override
		public void run() {
			while(!running){
				try{
					Thread.sleep(20);
					minute.rotateAroundPoint(centre, -360.0/60);
					hour.rotateAroundPoint(centre, (-360.0/60)/12);
					paint(getGraphics());
				} catch (InterruptedException e) {
					System.out.println("Interrupted Exception: "+e.getMessage());
				}
			}
		}
	}

}
