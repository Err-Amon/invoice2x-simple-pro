package com.invoice2x;

import com.invoice2x.ui.MainFrame;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.util.ConfigManager;
import com.invoice2x.util.UIConstants;  // Add this import
import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
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
            e.printStackTrace();
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                ConfigManager.getInstance();
                DatabaseService dbService = DatabaseService.getInstance();
                if (!dbService.initializeDatabase()) {
                    JOptionPane.showMessageDialog(null,
                        "Failed to initialize database. Please check configuration.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                
                MainFrame mainFrame = new MainFrame();
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.revalidate();
                mainFrame.repaint();
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
