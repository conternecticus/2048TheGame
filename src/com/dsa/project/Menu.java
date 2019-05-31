import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Menu {
    private JButton movableObstacleButton;
    private JButton fixedObstacleButton;
    private JButton normalModeButton;
    private JPanel menuView;

    //Game
    private JFrame game;
    private Game2048 logic;

    public Menu() {
        //Start game
        this.game = new JFrame();
        game.setTitle("2048 by Heart Hunters in Monster Kingdom");          // set title of game window
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);       //exit program when window is closed
        game.setSize(490, 700);                                 //set window size
        game.setResizable(false);                                           // cannot resize window
        game.setLocationRelativeTo(null);
        game.setVisible(false);                                             // set visibility of window, program will stop if 'false'

        fixedObstacleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (logic != null) {
                        game.remove(logic);
                    }
                    logic = new Game2048(false, false);
                    game.add(logic);
                    game.setVisible(true);
                } catch (IOException err) {
                    System.exit(1);
                }
            }
        });


        movableObstacleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (logic != null) {
                        game.remove(logic);
                    }

                    logic = new Game2048(true, false);
                Game2048.isObstacleExist = false;
                    game.add(logic);
                    game.setVisible(true);
                } catch (IOException err) {
                    System.exit(1);
                }
            }
        });


        normalModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (logic != null) {
                        game.remove(logic);
                    }
                    logic = new Game2048(false, true);
                    game.add(logic);
                    game.setVisible(true);
                } catch (IOException err) {
                    System.exit(1);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("2048 Menu by Heart Hunters in Monster Kingdom");
        frame.setContentPane(new Menu().menuView);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(450, 450));
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
