package com.invoice2x.ui.panels;

import com.invoice2x.ui.MainFrame;
import com.invoice2x.util.UIConstants;
import com.invoice2x.util.ConfigManager;
import javax.swing.*;
import java.awt.*;


public class SettingsPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTextField companyNameField;
    private JTextArea companyAddressArea;
    private JTextField taxIdField;
    private JTextField taxRateField;
    private JTextField currencyField;
    
    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
        loadSettings();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_LIGHT);
        
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // SMOOTH SCROLLING for settings form
        JPanel formContent = createFormContent();
        JScrollPane scrollPane = createSmoothScrollPane(formContent);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    
    private JScrollPane createSmoothScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);
        return scrollPane;
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel titleLabel = new JLabel("⚙️ Settings");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_DARK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(UIConstants.BG_LIGHT);
        
        JButton saveBtn = UIConstants.createSuccessButton("💾 Save Settings");
        saveBtn.setPreferredSize(UIConstants.BUTTON_LARGE);
        saveBtn.addActionListener(e -> saveSettings());
        
        JButton cancelBtn = UIConstants.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> mainFrame.showPanel("dashboard"));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFormContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        
        panel.add(createCompanySection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createInvoiceSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createDatabaseSection());
        panel.add(Box.createRigidArea(new Dimension(0, 24))); // Extra space
        
        return panel;
    }
    
    private JPanel createCompanySection() {
        JPanel section = UIConstants.createTitledPanel("🏢 Company Information");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 16, 12));
        gridPanel.setBackground(UIConstants.BG_CARD);
        
        gridPanel.add(UIConstants.createLabel("Company Name:"));
        companyNameField = UIConstants.createStyledTextField();
        companyNameField.setPreferredSize(new Dimension(400, 38));
        gridPanel.add(companyNameField);
        
        gridPanel.add(UIConstants.createLabel("Tax ID:"));
        taxIdField = UIConstants.createStyledTextField();
        gridPanel.add(taxIdField);
        
        gridPanel.add(UIConstants.createLabel("Address:"));
        companyAddressArea = UIConstants.createStyledTextArea(3, 30);
        
        // SMOOTH SCROLLING for address
        JScrollPane addressScroll = new JScrollPane(companyAddressArea);
        addressScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT));
        addressScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addressScroll.getVerticalScrollBar().setUnitIncrement(12);
        
        gridPanel.add(addressScroll);
        
        section.add(gridPanel);
        return section;
    }
    
    private JPanel createInvoiceSection() {
        JPanel section = UIConstants.createTitledPanel("Invoice Settings");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 16, 12));
        gridPanel.setBackground(UIConstants.BG_CARD);
        
        gridPanel.add(UIConstants.createLabel("Default Tax Rate (%):"));
        taxRateField = UIConstants.createStyledTextField();
        taxRateField.setPreferredSize(new Dimension(150, 38));
        gridPanel.add(taxRateField);
        
        gridPanel.add(UIConstants.createLabel("Currency Symbol:"));
        currencyField = UIConstants.createStyledTextField();
        currencyField.setPreferredSize(new Dimension(150, 38));
        gridPanel.add(currencyField);
        
        section.add(gridPanel);
        return section;
    }
    
    private JPanel createDatabaseSection() {
        JPanel section = UIConstants.createTitledPanel("Database Maintenance");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        buttonPanel.setBackground(UIConstants.BG_CARD);
        
        JButton backupBtn = UIConstants.createSecondaryButton("Backup Database");
        backupBtn.addActionListener(e -> backupDatabase());
        
        JButton compactBtn = UIConstants.createSecondaryButton("Compact Database");
        compactBtn.addActionListener(e -> compactDatabase());
        
        buttonPanel.add(backupBtn);
        buttonPanel.add(compactBtn);
        
        JLabel infoLabel = new JLabel("<html><i>Regular backups are recommended to prevent data loss</i></html>");
        infoLabel.setFont(UIConstants.SMALL_FONT);
        infoLabel.setForeground(UIConstants.TEXT_MEDIUM);
        
        section.add(buttonPanel);
        section.add(Box.createRigidArea(new Dimension(0, 8)));
        section.add(infoLabel);
        
        return section;
    }
    
    private void loadSettings() {
        ConfigManager config = ConfigManager.getInstance();
        
        companyNameField.setText(config.getProperty("company.name", ""));
        companyAddressArea.setText(config.getProperty("company.address", ""));
        taxIdField.setText(config.getProperty("company.taxid", ""));
        taxRateField.setText(config.getProperty("invoice.taxrate", "10"));
        currencyField.setText(config.getProperty("invoice.currency", "$"));
    }
    
    private void saveSettings() {
        try {
            ConfigManager config = ConfigManager.getInstance();
            
            config.setProperty("company.name", companyNameField.getText());
            config.setProperty("company.address", companyAddressArea.getText());
            config.setProperty("company.taxid", taxIdField.getText());
            config.setProperty("invoice.taxrate", taxRateField.getText());
            config.setProperty("invoice.currency", currencyField.getText());
            
            config.save();
            
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving settings: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void backupDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Backup");
        fileChooser.setSelectedFile(new java.io.File("invoice2x_backup_" + 
            java.time.LocalDate.now().toString() + ".db"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Files.copy(
                    java.nio.file.Paths.get("invoice2x.db"),
                    fileChooser.getSelectedFile().toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                JOptionPane.showMessageDialog(this,
                    "Database backed up successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error backing up database: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void compactDatabase() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will optimize the database. Continue?",
            "Confirm",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:invoice2x.db");
                java.sql.Statement stmt = conn.createStatement();
                stmt.execute("VACUUM");
                stmt.close();
                conn.close();
                
                JOptionPane.showMessageDialog(this,
                    "Database compacted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error compacting database: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}