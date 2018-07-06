package paint;

import javax.swing.JFrame;

public class CreateWindow {
  /* Main opens a window which will display the panels*/
  public static void main(String[] args) {
        JFrame window = new JFrame("To add a shape click where you want to add it then click on the shape "
                + "you want to add. For key press options right click.");

        window.setContentPane( new Paint() );

        window.setSize(1200,800);

        window.setLocation(150,100);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setVisible(true);

    }  
}