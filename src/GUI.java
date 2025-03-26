import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.function.Function;

public class GUI {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(GUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Simulation GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JButton galaxyCollision = new JButton("galaxyCollision");
        galaxyCollision.addActionListener(_ -> Templates.galaxyCollision(false, true));
        panel.add(galaxyCollision);

        JButton galaxy = new JButton("galaxy");
        galaxy.addActionListener(_ -> Templates.galaxy(true,true));
        panel.add(galaxy);

        JButton ballThroughDust = new JButton("ballThroughDust");
        ballThroughDust.addActionListener(_ -> Templates.ballThroughDust(true));
        panel.add(ballThroughDust);

        JButton windStream = new JButton("windStream");
        windStream.addActionListener(_ -> Templates.windStream());
        panel.add(windStream);

        JButton dropTest = new JButton("dropTest");
        dropTest.addActionListener(_ -> Templates.dropTest());
        panel.add(dropTest);

        JButton fluid = new JButton("fluid");
        fluid.addActionListener(_ -> Templates.fluid());
        panel.add(fluid);

        JCheckBox portuguese = new JCheckBox("Portuguese");
        portuguese.addActionListener(_ -> Locale.setDefault(portuguese.isSelected() ? new Locale("pt", "BR") : Locale.ENGLISH));
        panel.add(portuguese);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}