package com.invoice2x;

import com.invoice2x.ui.MainFrame;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.util.ConfigManager;
import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
        // Set system properties for better UI rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Apply custom UI defaults for professional appearance
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize configuration
                ConfigManager.getInstance();
                
                // Initialize database
                DatabaseService dbService = DatabaseService.getInstance();
                if (!dbService.initializeDatabase()) {
                    JOptionPane.showMessageDialog(null,
                        "Failed to initialize database. Please check configuration.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                
                // Create main frame
                MainFrame mainFrame = new MainFrame();
                
                // Ensure proper layout and repaint
                mainFrame.pack();           // Fit components
                mainFrame.setLocationRelativeTo(null); // Center on screen
                mainFrame.revalidate();     // Refresh layout
                mainFrame.repaint();        // Refresh visuals
                
                // Show the frame
                mainFrame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Application failed to start: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
