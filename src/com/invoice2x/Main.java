package com.invoice2x;

import com.invoice2x.ui.MainFrame;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.util.ConfigManager;
import com.invoice2x.util.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        logger.log(Level.INFO, "Invoice2X Simple Pro starting...");
        
        // Set system properties for better UI rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Set look and feel - Nimbus for consistent colors
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            if (UIManager.getLookAndFeel().getName().equals("Default")) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            
            // Custom UI defaults using your constants
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextField.arc", 8);
            UIManager.put("TextArea.arc", 8);
            UIManager.put("Button.font", UIConstants.BUTTON_FONT);
            UIManager.put("Label.font", UIConstants.BODY_FONT);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to set look and feel: " + e.getMessage());
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                logger.log(Level.INFO, "Initializing configuration...");
                ConfigManager.getInstance();
                
                logger.log(Level.INFO, "Initializing database...");
                DatabaseService dbService = DatabaseService.getInstance();
                if (!dbService.initializeDatabase()) {
                    logger.log(Level.SEVERE, "Database initialization failed");
                    JOptionPane.showMessageDialog(null,
                        "Failed to initialize database. Please check configuration.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                
                logger.log(Level.INFO, "Creating main frame...");
                MainFrame mainFrame = new MainFrame();
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.revalidate();
                mainFrame.repaint();
                mainFrame.setVisible(true);
                
                logger.log(Level.INFO, "Application started successfully");
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Application startup failed: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null,
                    "Application failed to start: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
