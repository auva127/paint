/* Auva Amirmokri
 * Painter GUI
 * This program simulates the paint application with a GUI, and allows you to choose different colors, sizes, save your work, etc.
 */

import java.awt.*;

import javax.swing.*;


import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

public class PainterAmirmokri extends JFrame implements ActionListener{

	private Color currentColor;
	private JColorChooser colorChooser;
	private DrawPanel canvas;
	private ArrayList<Point> points;
	private int currentSize;
	private JFileChooser choose;

	public PainterAmirmokri(){

		setSize(415,600);
		setTitle("Paint");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocationRelativeTo(null);

		//default size and color
		currentSize = 10;
		currentColor = Color.black;
		
		colorChooser= new JColorChooser();
		points = new ArrayList<Point>();

		JButton clear = new JButton("Clear");
		clear.setBounds(0,460 , 400, 100);
		clear.addActionListener(this);

		choose = new JFileChooser();

		canvas = new DrawPanel();	
		canvas.setBounds(0,0,415,600);

		//menu bar, menu, items, and sub items being created 
		JMenuBar menu = new JMenuBar();

		JMenu file  = new JMenu("File");
		JMenu opt = new JMenu("Options");

		JMenuItem load = new JMenuItem("Load");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem imp = new JMenuItem("Impose");
		JMenuItem color = new JMenuItem("Color");
		JMenu size = new JMenu("Size");

		JMenuItem s1 = new JMenuItem("10");
		JMenuItem s2 = new JMenuItem("25");
		JMenuItem s3 = new JMenuItem("50");

		//adds all actionlisteners 
		load.addActionListener(this);
		save.addActionListener(this);
		imp.addActionListener(this);
		color.addActionListener(this);
		size.addActionListener(this);
		s1.addActionListener(this);
		s2.addActionListener(this);
		s3.addActionListener(this);

		//adds sizes to size menu
		size.add(s1);
		size.add(s2);
		size.add(s3);

		file.add(load);
		file.add(save);
		file.add(imp);

		opt.add(color);
		opt.add(size);

		menu.add(file);
		menu.add(opt);

		setJMenuBar(menu);
		add(clear);
		add(canvas);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae){

		Scanner fileIn = null;

		//changes look of file chooser -- this only works on windows. comment out for mac 
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		}catch(Exception e) {
//			System.out.println("That look and feel is not found");
//			System.exit(-1);
//		}

		//loads file into screen 
		if(ae.getActionCommand().equals("Load")) {
			
			choose.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = choose.showOpenDialog(null);
			
			//clears previous drawings
			points = new ArrayList<Point>();
			canvas.repaint();

			openFile(result);

		}

		//saves file 
		else if(ae.getActionCommand().equals("Save")) {
			
			choose.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = choose.showOpenDialog(null);
			
			saveFile(result);

		}

		//stacks two pictures 
		else if (ae.getActionCommand().equals("Impose")) {
			
			choose.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = choose.showOpenDialog(null);
			
			//opens selected file on top of current one 
			openFile(result);

		}

		//change the color of the brush
		else if (ae.getActionCommand().equals("Color")) {
			Color newColor = colorChooser.showDialog(null, "Select a color", currentColor);
			if(newColor !=null)
				currentColor = newColor;
		}

		//sets sizes 
		else if(ae.getActionCommand().equals("10")) {
			currentSize = 10;

		}
		else if (ae.getActionCommand().equals("25")) {
			currentSize = 25;

		}
		else if(ae.getActionCommand().equals("50")) {
			currentSize = 50;

		}

		//clears screen of all drawings
		else if(ae.getActionCommand().equals("Clear")) {
			points = new ArrayList<Point>();
			canvas.repaint();
			currentColor = Color.black;
			currentSize = 10;
		}
	}

	//helper method to open file 
	private void openFile(int result) {

		Scanner fileIn = null;

		if(result == JFileChooser.APPROVE_OPTION){

			File toOpen = choose.getSelectedFile();

			//attempts to open
			try {
				fileIn = new Scanner(toOpen);
			}catch(FileNotFoundException e) {
				System.out.println("File Not Found - Try Again");
				System.exit(-1); 
			}

			//takes in each integer and adds to arraylist of points
			while(fileIn.hasNextInt()) {

				// x y r g b size
				points.add(new Point(fileIn.nextInt(),fileIn.nextInt(), new Color(fileIn.nextInt(),fileIn.nextInt(),fileIn.nextInt()), fileIn.nextInt()));
				repaint();
			}
		}

	}

	//helper to save file 
	private void saveFile(int result) {
		File toSave = null;

		if(result == JFileChooser.APPROVE_OPTION) {

			toSave = choose.getSelectedFile();

			//saves all components to file in correct format
			try {

				FileWriter outFile = new FileWriter(toSave);


				//adds each component of each point to new file
				for(int i =0; i < points.size(); i++) {

					//x y color size 
					outFile.write(points.get(i).xLoc + "\t"  );
					outFile.write(points.get(i).yLoc+ "\t");
					outFile.write(points.get(i).ptColor.getRed()+ "\t");
					outFile.write(points.get(i).ptColor.getGreen() +"\t");
					outFile.write(points.get(i).ptColor.getBlue()+"\t" );
					outFile.write(points.get(i).ptSize +"\r\n" );

				}

				outFile.close();

			}catch(IOException e){
				System.out.println("IO Issue");
				System.exit(-1);
			}

		}
	}

	public class Point{

		private int xLoc;
		private int yLoc;
		private Color ptColor;
		private int ptSize;

		public Point(int x, int y, Color c, int s){
			xLoc = x;
			yLoc = y;
			ptColor = c;
			ptSize = s;
		}
	}

	public class DrawPanel extends JPanel implements MouseMotionListener {

		public DrawPanel(){
			addMouseMotionListener(this); 
		}

		public void paintComponent(Graphics g){		

			super.paintComponent(g);
			setBackground(Color.white);

			//draw a point
			for(Point nextP: points){
				g.setColor(nextP.ptColor);
				g.fillOval(nextP.xLoc, nextP.yLoc, nextP.ptSize, nextP.ptSize); 
			}

		}

		//clicked mouse
		public void mouseClicked(MouseEvent me) {
			points.add(new Point(me.getX(),me.getY(), currentColor, currentSize));
			this.repaint();
		}

		//dragged mouse
		public void mouseDragged(MouseEvent me) {
			points.add(new Point(me.getX(), me.getY(), currentColor, currentSize));
			this.repaint();
		}


		//don't need to implement
		public void mouseMoved(MouseEvent me) {

		}

		public void mouseEntered(MouseEvent arg0) {

		}

		public void mouseExited(MouseEvent arg0) {

		}

		public void mousePressed(MouseEvent arg0) {

		}

		public void mouseReleased(MouseEvent arg0) {

		}
	}

	public static void main(String[] args){
		new PainterAmirmokri();
	}


}
