import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class GUI {
    
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(GUI::createAndShowGUI);
    }

    @SuppressWarnings("deprecation")
    private static void createAndShowGUI() {
        JCheckBox drawQuadTree = new JCheckBox("Draw QuadTree");
        JCheckBox StickyCollisions = new JCheckBox("Sticky Collisions");
        drawQuadTree.setSelected(false);
        StickyCollisions.setSelected(false);

        JFrame frame = new JFrame("Simulation GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JButton galaxyCollision = new JButton("galaxyCollision");
        galaxyCollision.addActionListener(_ -> Templates.galaxyCollision(drawQuadTree,StickyCollisions));
        panel.add(galaxyCollision);

        JButton galaxy = new JButton("galaxy");
        galaxy.addActionListener(_ -> Templates.galaxy(drawQuadTree,StickyCollisions));
        panel.add(galaxy);

        JButton ballThroughDust = new JButton("ballThroughDust");
        ballThroughDust.addActionListener(_ -> Templates.ballThroughDust(drawQuadTree,StickyCollisions));
        panel.add(ballThroughDust);

        JButton windStream = new JButton("windStream");
        windStream.addActionListener(_ -> Templates.windStream(drawQuadTree,StickyCollisions));
        panel.add(windStream);

        JButton dropTest = new JButton("dropTest");
        dropTest.addActionListener(_ -> Templates.dropTest(drawQuadTree,StickyCollisions));
        panel.add(dropTest);

        JButton solarSystem = new JButton("Our Solar System");
        solarSystem.addActionListener(_ -> Templates.solarSystem(drawQuadTree,StickyCollisions));
        panel.add(solarSystem);

        JCheckBox portuguese = new JCheckBox("Portuguese");
        portuguese.addActionListener(_ -> Locale.setDefault(portuguese.isSelected() ? new Locale("pt", "BR") : Locale.ENGLISH));
        panel.add(portuguese);
        panel.add(drawQuadTree);
        panel.add(StickyCollisions);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}