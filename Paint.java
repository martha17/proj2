package paint;

import java.awt.*;

import java.awt.event.*;

import java.awt.geom.GeneralPath;

import java.awt.print.PageFormat;

import javax.swing.*;

import java.awt.print.Printable;

import java.awt.print.PrinterException;

import java.awt.print.PrinterJob;

import java.awt.Color;

public class Paint extends JPanel {
    /*Sets up the drawing area and row of buttons below it.*/

    public Paint() {  

        setBackground(Color.LIGHT_GRAY);

        JButton printButton = new JButton("Print"); // button for printing diagram
        
        JButton clearButton = new JButton("Clear All"); // button for clearing shapes
        
        JButton undoButton = new JButton("Undo Shape"); // button for undoing shapes
        
        JButton redoButton = new JButton("Redo Shape"); // button for redoing shapes
        
        JButton colorButtonBG = new JButton("Background Color"); //button to choose background color
        
        JButton colorButtonG = new JButton("Grid Color"); //button to choose grid color
        
        JButton colorButtonS = new JButton("Shape Color"); //button to choose shape color
        
        JButton colorButtonB = new JButton("Border Color"); //button to choose border color
        
        /*Listeners for buttons*/
        colorButtonBG.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Color color = JColorChooser.showDialog(Paint.this,"Choose a background color", backgroundColor);
            if (color != null) { // new color selected
               backgroundColor = color;
            }
            canvas.repaint();
          }
        });
        
        colorButtonG.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Color color = JColorChooser.showDialog(Paint.this,"Choose a gridline color", gridlineColor);
            if (color != null) { // new color selected
               gridlineColor = color;
            }
            canvas.repaint();
          }
        });
        
        colorButtonS.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Color color = JColorChooser.showDialog(Paint.this,"Choose a shape color", shapeColor);
            if (color != null) { // new color selected
               shapeColor = color;
            }
          }
        });

        colorButtonB.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            Color color = JColorChooser.showDialog(Paint.this,"Choose a border color", borderColor);
            if (color != null) { // new color selected
               borderColor = color;
            }
           }
        });
        
        canvas.addMouseListener(new PopClickListener());
        
        printButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(canvas);
            if (printJob.printDialog()) {
                try {
                    printJob.print();
                } catch (PrinterException p) {
                    System.out.println("Error printing: " + p);
                }
            }
           }
        });
        
        clearButton.addActionListener(canvas);
        
        undoButton.addActionListener(canvas);
        
        redoButton.addActionListener(canvas);

        JPanel bottom = new JPanel();   // panel that holds buttons

        MyPanel top = new MyPanel();    // panel that shows the shapes

        top.setLayout(new GridLayout(1,4,3,3));

        bottom.setLayout(new GridLayout(1,4,3,3));
        
        bottom.add(colorButtonS);
        
        bottom.add(colorButtonB);
        
        bottom.add(colorButtonBG);
        
        bottom.add(colorButtonG);
        
        bottom.add(undoButton);
        
        bottom.add(redoButton);
        
        bottom.add(clearButton);
        
        bottom.add(printButton);
      
        top.add(new MyPanel ());

        setLayout(new BorderLayout(3,3));

        add(top,BorderLayout.NORTH); //add top panel

        add(canvas, BorderLayout.CENTER); //add canvas panel

        add(bottom, BorderLayout.SOUTH); //add button panel   
    } //end of constructor

Board canvas = new Board();  // creating the canvas

public Point startPoint, endPoint;

/*Our 'canvas' for drawing shapes.*/
class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener, Printable {
        
        public Board() {
            addMouseListener(this);
            
            addMouseMotionListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            
            super.paintComponent(g);
            
            Color lineColor=gridlineColor;
            Color bgColor=backgroundColor;

            //Draw background color
            g.setColor(bgColor);
            g.fillRect(0,0,getSize().width-2,getSize().height-2);
            
            int pt=10; // spacing between lines
            int k = 0;
            int i = 0;
            int j = 0;
            
            //Draws gridlines
            while (i < getSize().width) {
                g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 10));
                if ((k % 4) == 0) {
                    g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 30));
                }
		g.drawLine(i, 0, i, getSize().height);
		i += (pt * 96) / 76;
		k++;
            }
            while (j < getSize().height) {
		g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 10));
		if ((k % 4) == 0) {
                    g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 30));
		}
		g.drawLine(0, j, getSize().width, j);
		j += (pt * 96) / 76;
		k++;
            }

            // Draws all of the shapes in the ArrayList onto the canvas
            for (int y = 0; y < shapeCount; y++) {
                Shape s = shapes[y];
                s.draw(g);
            }
            
            for (int y = 0; y < lineCount; y++) {
                Line l = lines[y];
                l.draw(g);
            }
            g.setColor(Color.black);  // Draws a black border around the edge of the drawing area
            g.drawRect(0,0,getWidth()-1,getHeight()-1);
        }
        
        //Actions performed when clear, undo, or redo are pressed
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            //Removes all of the shapes from the ArrayList
            if (command.equals("Clear All")){
                while(shapeCount!=0){
                    shapes[shapeCount]=null;
                    shapeCount=shapeCount-1;
                }
                shapes[shapeCount]=null;
                while(lineCount!=0){
                    lines[lineCount]=null;
                    lineCount=lineCount-1;
                }
                lines[lineCount]=null;
                repaint();
            }
            
            //Removes the last shape
            if (command.equals("Undo Shape")){
                if(shapeCount!=0){
                    shapeCount=shapeCount-1;
                    repaint();
                }
            }
            
            //Adds the last removed shape back
            if(command.equals("Redo Shape")){
                if(shapes[shapeCount]!=null){
                    shapeCount=shapeCount+1;
                    repaint();
                }
            }
        }
        
        /*implementing dragging*/
        Shape shapeBeingDragged = null; // Indicates whether the shape is being dragged
        
        int prevDragX, prevDragY;  // Previous position of the mouse

        public void mousePressed(MouseEvent evt) {

                // start dragging it.  If the user was holding down the shift key, then bring

                // the dragged shape to the front, in front of all the other shapes.

            int x = evt.getX();  // x-coordinate of point where the mouse was clicked

            int y = evt.getY();  // y-coordinate of point where the mouse was clicked

            for ( int i = shapeCount - 1; i >= 0; i-- ) {

                Shape s = shapes[i];
                
                //Checks if there is a shape at the position where the mouse was clicked.
                if (s.containsPoint(x,y)) {
                    //If so, the shape then begins to be dragged.
                    shapeBeingDragged = s;

                    prevDragX = x;

                    prevDragY = y;

                    //If the user was holding the shift key, the shape is brought to front.
                    if (evt.isShiftDown()) { // s should be moved on top of all the other shapes       
                        for (int j = i; j < shapeCount-1; j++) {
                            // moves the shapes following s down in the list
                            shapes[j] = shapes[j+1];
                        }
                        shapes[shapeCount-1] = s;  // puts s at the end of the list
                        repaint();  // repaints canvas to show s in front of the other shapes
                    }
                    
                    //If the user was holding the control key, the shape is sent to back.
                    if (evt.isControlDown()) { // s should be moved on the bottom of all the other shapes
                        for(int j=0; j<shapeCount;j++){
                            if(shapes[j]==s){ // finds the shape that the user has clicked on
                                while(j!=0){
                                   // moves shapes before s up
                                   shapes[j]=shapes[j-1];
                                   j--;
                                }
                            shapes[0] = s; // puts s at the beggining of the list
                            repaint();  // repaints canvas to show s behind the other shapes
                        }
                      }
                    }
                    
                    //If the user was holding the alt key, the shape is deleted.
                    if (evt.isAltDown()) {
                        for(int j=0; j<shapeCount;j++){
                            if(shapes[j]==s){
                                for(int k=j;k<shapeCount;k++){
                                    shapes[k]=shapes[k+1];
                                }
                                shapes[shapeCount]=null;
                                    
                                shapeCount=shapeCount-1;
                            }
                        }             
                    }                
                    return;
                }
            }
        }
        
        public void mouseDragged(MouseEvent evt) {
            int x = evt.getX();
            int y = evt.getY();

            // Move the shape by the same amount the user moves the mouse
            if (shapeBeingDragged != null) {
                shapeBeingDragged.moveBy(x - prevDragX, y - prevDragY);
                prevDragX = x;
                prevDragY = y;
                repaint(); // redraw canvas to show shape in new position
            }
        }

        public void mouseReleased(MouseEvent evt) {
            endPoint = evt.getPoint();
            
            int x = evt.getX();
            
            int y = evt.getY();

            if (shapeBeingDragged != null) {

                shapeBeingDragged.moveBy(x - prevDragX, y - prevDragY);

                shapeBeingDragged = null; //Indicates that dragging is over

                repaint();

            }

        }

        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

            if (pageIndex > 0) {
                
                return NO_SUCH_PAGE;
                
            } else {
                int x = (int) pageFormat.getImageableX() + 1;

                int y = (int) pageFormat.getImageableY() + 1;

                g.translate(x, y);

                RepaintManager m = RepaintManager.currentManager(this);

                m.setDoubleBufferingEnabled(false);

                paint(g);

                m.setDoubleBufferingEnabled(true);

                return PAGE_EXISTS;
            }
        }
        // Other methods required for MouseListener and MouseMotionListener interfaces.
        public void mouseEntered(MouseEvent evt) { }

        public void mouseExited(MouseEvent evt) { }

        public void mouseMoved(MouseEvent evt) { }

      
        int count=0;
        public void mouseClicked(MouseEvent evt) {
            count++;
            if(count%2==0){
                x2 = evt.getX();  // x-coordinate of point where the mouse was clicked
                y2 = evt.getY();  // y-coordinate of point where the mouse was clicked
                for(int i= shapeCount - 1; i >= 0; i--){
            Shape s = shapes[i];
            if (s.containsPoint(x1,y1)) {
                    for ( int j = shapeCount - 1; j >= 0; j-- ) {
                //Checks if there is a shape at the position where the mouse was clicked.
                        s = shapes[j];
                        if(s.containsPoint(x2, y2)&&i!=j){
                            lines[lineCount]=new Line(x1,y1,x2,y2);
                            lineCount++;
                            canvas.repaint();
                        }
                    }                    
            }
            
        }
              
            } else {
                x1 = evt.getX();  // x-coordinate of point where the mouse was clicked
                y1 = evt.getY();  // y-coordinate of point where the mouse was clicked
            }
    }
}// end of class Board
    int x1,y1,x2,y2;
    Shape[] shapes = new Shape[500]; // array that holds up to 500 shapes
    Line[] lines = new Line[500];
    
    int shapeCount = 0;  // the actual number of shapes in the array
    int lineCount = 0; // the actual number of lines in the array

    Color shapeColor = Color.black;  // default shape color
     
    Color borderColor = Color.black; // default border color
    
    Color backgroundColor = Color.WHITE; // default background color
     
    Color gridlineColor = Color.WHITE; // default gridline color
    
    /*A class representing shapes. The subclasses of this class draw different types of shapes.*/
    abstract class Shape {
        int left, top, width, height; //top-left coordinates of bound, width, and height of shape

        Color colorS = Color.white;  // color of the shape
        
        Color colorB = Color.white; // color of the border
        
        void reshape(int left, int top, int width, int height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        // Moves the shape by changing the position of the top-left corner
        void moveBy(int x, int y) {
            left += x;
            top += y;
        }

        // Sets the colors of the shape.
        void setColor(Color colors, Color colorb) {
            colorS=colors;
            colorB=colorb;
        }

        boolean containsPoint(int x, int y) {
            // Checks whether the shape's rectangular bound contains the point (x,y).
            if (x >= left && x < left+width && y >= top && y < top+height)
                return true;
            else
                return false;
        }

        abstract void draw(Graphics g);
    }  // end of class Shape

    //Draws a rectangle.
    class RectShape extends Shape {
        void draw(Graphics g) {

            g.setColor(colorS);

            g.fillRect(left,top,width,height);

            g.setColor(colorB);

            g.drawRect(left,top,width,height);

        }

    }
    
    //Draws an oval.
    class OvalShape extends Shape {
        void draw(Graphics g) {

            g.setColor(colorS);

            g.fillOval(left,top,width,height);

            g.setColor(colorB);

            g.drawOval(left,top,width,height);

        }

        boolean containsPoint(int x, int y) {
            // Check whether (x,y) is inside this oval.

            double rx = width/2.0;   // horizontal radius of ellipse

            double ry = height/2.0;  // vertical radius of ellipse 

            double cx = left + rx;   // x-coord of center of ellipse

            double cy = top + ry;    // y-coord of center of ellipse

            if ( (ry*(x-cx))*(ry*(x-cx)) + (rx*(y-cy))*(rx*(y-cy)) <= rx*rx*ry*ry )
                return true;
            else
                return false;
        }
    }
    
    //Draws a star.
    class StarShape extends Shape {
        void draw(Graphics g) {
            int[] x  = {42,52,72,52,60,40,15,28,9,32,42};
            int[] y = {38,62,68,80,105,85,102,75,58,60,38};
            g.setColor(colorS);
            g.fillPolygon(x , y, 11);
            g.setColor(colorB);
            g.drawPolygon(x,y,11);
        }
    }
   
    //Draws a triangle.
    class TriangleShape extends Shape {
        void draw(Graphics g) {
            g.setColor(colorS);
            g.fillPolygon(new int[] {left, left+(width/2), left+(width)}, new int[] {top+height, top, top+height},3);
            g.setColor(colorB);
            g.drawPolygon(new int[] {left, left+(width/2), left+(width)}, new int[] {top+height, top, top+height},3);
        }
    }

    class MyPanel extends JPanel implements ActionListener {
        private int squareX = 120;
        private int squareY = 15;
        private int squareW = 80;
        private int squareH = 80;
 
        public void actionPerformed(ActionEvent evt) {

        }
    
        public MyPanel() {
            setBorder(BorderFactory.createLineBorder(Color.gray));
        
            addMouseListener(new MouseAdapter() {
                
            //the conditions are based on where the shape is located in the panel
            public void mousePressed(MouseEvent e) {
                
                if(e.getX()>220 && e.getX()<300& e.getY()<95 && e.getY()>15){

                   addShape(new OvalShape());

                }

                if(e.getX()>300 && e.getX()<400& e.getY()<100 && e.getY()>20){

                    addShape(new TriangleShape());

                }

                if(e.getX()>120 && e.getX()<200& e.getY()<95 && e.getY()>15){

                    addShape(new RectShape());

                }
                
                if(e.getX()>410 && e.getX()<520& e.getY()<95 && e.getY()>15){

                    addShape(new StarShape());

                }
                
                canvas.repaint(); // Redraws the canvas to show the newly added shape
            }

        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                
            }

        });
    }
    
    // Adds the shape to the canvas, and sets its size, position and color.
    void addShape(Shape shape) {
        if(endPoint!=null){
            shape.setColor(shapeColor, borderColor);
            
            shape.reshape(endPoint.x,endPoint.y,80,50);

            shapes[shapeCount] = shape;

            shapeCount++;
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(850, 110);
    }

    int x, y, width, height;

    public void Star(int x, int y) {

        this.x = x;

        this.y = y;

    }

    /*Draws the shapes shown on the top panel.*/
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);   

        Graphics2D g2 = (Graphics2D) g;

        Graphics2D g3 = (Graphics2D) g;

        int xPoints[] = {459, 498, 400, 518, 420};

        int yPoints[] = {7, 103, 43, 43, 103};

        Graphics2D g2d = (Graphics2D) g;

        GeneralPath star = new GeneralPath();

        star.moveTo(xPoints[0] + x, yPoints[0] + y);

        for (int i = 1; i < xPoints.length; i++) {

            star.lineTo(xPoints[i] + x, yPoints[i] + y);

        }

        star.closePath();

        g2d.setColor(Color.BLACK);

        g2d.fill(star);

        g2.setColor(Color.BLACK);

        g2.fillOval(220, 15, 80, 80);

        g2.setColor(Color.BLACK);

        g2.drawOval(220, 15, 80, 80);

        g2.drawPolygon(new int[] {300, 350, 400}, new int[] {100, 20, 100}, 3);

        g3.drawPolygon(new int[] {300, 350, 400}, new int[] {100, 20, 100}, 3);

        g3.setColor(Color.BLACK);

        g3.drawRect(squareX,squareY,squareW,squareH);
        
    }
  }
  class PopUpDemo extends JPopupMenu {
    JMenuItem Message1;
    JMenuItem Message2;
    JMenuItem Message3;
    public PopUpDemo(){
        Message1 = new JMenuItem("Delete            Alt+Click shape");
        Message2= new JMenuItem("Bring to Front   Shift+Click shape");
        Message3 = new JMenuItem("Send to Back   Control+Click shape");
        add(Message1);
        add(Message2);
        add(Message3);
    }
  }
  class PopClickListener extends MouseAdapter {
    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
        PopUpDemo menu = new PopUpDemo();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
    }
}