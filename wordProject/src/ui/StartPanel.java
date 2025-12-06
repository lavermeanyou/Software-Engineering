package ui;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {
    private JButton newGameButton;
    private JCheckBox slangModeCheckBox; // Added

    public StartPanel() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Word Master Game", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH); // Changed to NORTH to make room for controls

        // Controls Panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        slangModeCheckBox = new JCheckBox("Slanguage");
        controlPanel.add(slangModeCheckBox);
        
        // Game Button
        newGameButton = new JButton("새 게임 시작");
        
        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(newGameButton, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }
    
    // Getter for the new feature
    public boolean isSlangModeSelected() {
        return slangModeCheckBox.isSelected();
    }
}