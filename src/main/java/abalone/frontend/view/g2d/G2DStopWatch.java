package abalone.frontend.view.g2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class G2DStopWatch implements G2DObject{
	
	private G2DDigit h1, h2, m1, m2, s1, s2;
	private int x, y, hours, minutes, seconds;
	private Timer timer;
	private ArrayList<boolean[]> numbers;
	private G2DGroup colon1, colon2;

	public G2DStopWatch(int xi, int yi){
		x = xi; y = yi;
		h1 = new G2DDigit(); //2 digits each for hour, minute and second. 
		h2 = new G2DDigit();
		m1 = new G2DDigit();
		m2 = new G2DDigit();
		s1 = new G2DDigit();
		s2 = new G2DDigit();
		buildGroup(h1, x, y);
		x+=41;
		buildGroup(h2, x, y);
		colon1= new G2DGroup();
		colon1.add(new G2DPoint(x+35, y+15));
		colon1.add(new G2DPoint(x+35, y+45));
		x+=41;
		buildGroup(m1, x, y);
		x+=41;
		buildGroup(m2, x, y);
		colon2= new G2DGroup();
		colon2.add(new G2DPoint(x+35, y+15));
		colon2.add(new G2DPoint(x+35, y+45));
		x+=41;
		buildGroup(s1, x, y);
		x+=41;
		buildGroup(s2, x, y);
		//Arrays to define which lines in the digit will be visible
		//for each number.
		boolean[] zero =  { true,  true,  true, false,  true,  true,  true};
		boolean[] one =   {false, false,  true, false, false,  true, false};
		boolean[] two =   { true, false,  true,  true,  true, false,  true};
		boolean[] three = { true, false,  true,  true, false,  true,  true};
		boolean[] four =  {false,  true,  true,  true, false,  true, false};
		boolean[] five =  { true,  true, false,  true, false,  true,  true};
		boolean[] six =   { true,  true, false,  true,  true,  true,  true};
		boolean[] seven = { true, false,  true, false, false,  true, false};
		boolean[] eight = { true,  true,  true,  true,  true,  true,  true};
		boolean[] nine =  { true,  true,  true,  true, false,  true,  true};
		numbers = new ArrayList<boolean[]>();
		numbers.add(zero);
		numbers.add(one);
		numbers.add(two);
		numbers.add(three);
		numbers.add(four);
		numbers.add(five);
		numbers.add(six);
		numbers.add(seven);
		numbers.add(eight);
		numbers.add(nine);
		
		reset();

		new Thread(new ShowTime()).start();
	}
	/*
	 * Creates all the lines for each digit.
	 */
	private void buildGroup(G2DDigit g, int x, int y){
		G2DLine ln = new G2DLine(x,y,x+30,y,Color.BLACK,3);
		g.add(ln);
		ln = new G2DLine(x,y,x,y+30,Color.BLACK,3);
		g.add(ln);
		ln = new G2DLine(x+30,y,x+30,y+30,Color.BLACK,3);
		g.add(ln);
		ln = new G2DLine(x,y+30,x+30,y+30,Color.BLACK,3);
		g.add(ln);
		ln = new G2DLine(x,y+30,x,y+60,Color.BLACK,3);
		g.add(ln);
		ln = new G2DLine(x+30,y+30,x+30,y+60,Color.BLACK,3);
		g.add(ln);
		ln = new G2DLine(x,y+60,x+30,y+60,Color.BLACK,3);
		g.add(ln);
	}
	@Override
	public void draw(G2DAbstractCanvas absCanvas) {
		h1.draw(absCanvas);
		h2.draw(absCanvas);
		colon2.draw(absCanvas);
		m1.draw(absCanvas);
		m2.draw(absCanvas);
		colon1.draw(absCanvas);
		s1.draw(absCanvas);
		s2.draw(absCanvas);
	}
		
	public void start(){
		if(null == timer){
			paused = false;
			timer = new Timer();
			timer.scheduleAtFixedRate(new Ticker(), 1000, 1000);	
		} else{
			lap();
		}
		
	}
	
	public void stop(){
		if(null != timer){
			timer.cancel();
			timer = null;
			new Thread(new ShowTime()).start();
		} else {
			reset();
		}
		paused = false;
	}
	
	private void reset(){
		hours = 0;
		minutes = 0;
		seconds = 0;
		new Thread(new ShowTime()).start();
	}
	
	private boolean paused = false;
	private void lap(){
		new Thread(new ShowTime()).start();
		if(paused)
			paused = false;
		else
			paused = true;
	}
	/*
	 * Ticks the stopwatch one second at a time.
	 */
	private class Ticker extends TimerTask{
		@Override
		public void run() {
			seconds++;
			if(60 == seconds){
				minutes++; seconds=0;
				if(60 == minutes){
					hours++; minutes=0;
					if(100 == hours){
						hours=0;
					}
				}
			}
			if(!paused)
				new Thread(new ShowTime()).start();				
		}	
	}
	/*
	 * Works out the digits to display to show the current time
	 * on the stopwatch.
	 */
	private boolean showTimeLock = false;
	private class ShowTime implements Runnable{

		@Override
		public void run() {
			if(!showTimeLock){ //Lock out other threads from modifying these.
				showTimeLock = true;
				String hr = Integer.toString(hours);
				String min = Integer.toString(minutes);
				String sec = Integer.toString(seconds);
				if(2 == sec.length()){
					s2.setDigit(numbers.get(Integer.parseInt(""+sec.charAt(1))));
					s1.setDigit(numbers.get(Integer.parseInt(""+sec.charAt(0))));
				} else {
					s2.setDigit(numbers.get(Integer.parseInt(""+sec.charAt(0))));
					s1.setDigit(numbers.get(0));
				}
				if(2 == min.length()){
					m2.setDigit(numbers.get(Integer.parseInt(""+min.charAt(1))));
					m1.setDigit(numbers.get(Integer.parseInt(""+min.charAt(0))));
				} else {
					m2.setDigit(numbers.get(Integer.parseInt(""+min.charAt(0))));
					m1.setDigit(numbers.get(0));
				}
				if(2 == hr.length()){
					h2.setDigit(numbers.get(Integer.parseInt(""+hr.charAt(1))));
					h1.setDigit(numbers.get(Integer.parseInt(""+hr.charAt(0))));
				} else {
					h2.setDigit(numbers.get(Integer.parseInt(""+hr.charAt(0))));
					h1.setDigit(numbers.get(0));
				}
				showTimeLock = false; //Unlock as finished.
			}
		}
	}

	@Override
	public G2DObject deepClone() {
		return null;
	}

	@Override
	public void setColor(Color color) {
		
	}

	@Override
	public void transform(Matrix transformationMatrix) {
		
	}

	@Override
	public void translate(double x, double y) {
		
	}

	@Override
	public void rotateAroundOrigin(double degrees) {
		
	}

	@Override
	public void rotateAroundPoint(double x, double y, double degrees) {
		
	}

}
