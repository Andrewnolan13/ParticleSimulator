import javax.swing.*;
import java.awt.*;

public class GUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Simulation GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JButton galaxyCollision = new JButton("galaxyCollision");
        galaxyCollision.addActionListener(e -> test.galaxyCollision(false, true));
        panel.add(galaxyCollision);

        JButton galaxy = new JButton("galaxy");
        galaxy.addActionListener(e -> test.galaxy(true,true));
        panel.add(galaxy);

        JButton ballThroughDust = new JButton("ballThroughDust");
        ballThroughDust.addActionListener(e -> test.ballThroughDust(true));
        panel.add(ballThroughDust);

        JButton windStream = new JButton("windStream");
        windStream.addActionListener(e -> test.windStream());
        panel.add(windStream);

        JButton dropTest = new JButton("dropTest");
        dropTest.addActionListener(e -> test.dropTest());
        panel.add(dropTest);

        JButton fluid = new JButton("fluid");
        fluid.addActionListener(e -> test.fluid());
        panel.add(fluid);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}