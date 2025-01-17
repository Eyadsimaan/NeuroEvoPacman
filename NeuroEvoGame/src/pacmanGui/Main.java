package pacmanGui;

import newGui.infra.Display;
import newGui.infra.Game;
import pacmanGui.PacmanGame;

import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Main class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Main{


	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Game game = new PacmanGame();
                Display view = new Display(game);
                JFrame frame = new JFrame();
                frame.setTitle("Pacman");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(view);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                view.requestFocus();
                view.start();
            }

        });
    }
    
}
