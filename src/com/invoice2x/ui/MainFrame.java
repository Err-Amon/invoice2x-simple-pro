package com.invoice2x.ui;

import com.invoice2x.ui.panels.*;
import com.invoice2x.util.UIConstants;
import com.invoice2x.model.Invoice;
import com.invoice2x.service.DatabaseService;
import javax.swing.*;
import java.awt.*;


 // Main application window with SMOOTH scrolling
 
public class MainFrame extends JFrame {
    
    private JPanel navigationPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private DashboardPanel dashboardPanel;
    private InvoiceListPanel invoiceListPanel;
    private InvoiceFormPanel invoiceFormPanel;
    private ExportPanel exportPanel;
    private SettingsPanel settingsPanel;
    
    public MainFrame() {
        initializeFrame();
        createMenuBar();
        createNavigationPanel();
        createContentPanel();
        layoutComponents();
    }
    
    private void initializeFrame() {
        setTitle("Invoice2X Simple Pro");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/app-icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Icon not found, continue
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT));
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newInvoiceItem = new JMenuItem("New Invoice");
        newInvoiceItem.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newInvoiceItem.addActionListener(e -> showPanel("newInvoice"));
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newInvoiceItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Invoices Menu
        JMenu invoicesMenu = new JMenu("Invoices");
        JMenuItem listItem = new JMenuItem("View All Invoices");
        listItem.addActionListener(e -> showPanel("invoiceList"));
        invoicesMenu.add(listItem);
        
        // Export Menu
        JMenu exportMenu = new JMenu("Export");
        JMenuItem exportItem = new JMenuItem("Export to Excel");
        exportItem.setAccelerator(KeyStroke.getKeyStroke("control E"));
        exportItem.addActionListener(e -> showPanel("export"));
        exportMenu.add(exportItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(invoicesMenu);
        menuBar.add(exportMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    

public void exportSingleInvoice(int invoiceId) {
    try {
        // Load full invoice from DB by ID
        Invoice invoice = DatabaseService.getInstance().getInvoiceById(invoiceId);
        if (invoice == null) {
            JOptionPane.showMessageDialog(this,
                "Invoice not found: " + invoiceId,
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new ExportPanel for this specific invoice
        ExportPanel singleExportPanel = new ExportPanel(this, invoice);

        // Add it to the card layout under a dedicated name and show it
        contentPanel.add(singleExportPanel, "exportSingle");
        cardLayout.show(contentPanel, "exportSingle");

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Failed to load invoice for export:\n" + ex.getMessage(),
            "Export Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
public void exportMultipleInvoices(java.util.List<Integer> invoiceIds) {
    try {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No invoices selected to export",
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        DatabaseService db = DatabaseService.getInstance();
        java.util.List<Invoice> invoices = new java.util.ArrayList<>();

        for (Integer id : invoiceIds) {
            Invoice inv = db.getInvoiceById(id);
            if (inv != null) {
                invoices.add(inv);
            }
        }

        if (invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selected invoices not found in database",
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create an ExportPanel that will export this list (use the batch path)
        ExportPanel batchPanel = new ExportPanel(this);
        // You can add a setter in ExportPanel to inject the list:

        batchPanel.setPreloadedInvoices(invoices);

        contentPanel.add(batchPanel, "exportBatch");
        cardLayout.show(contentPanel, "exportBatch");

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Failed to load invoices for export:\n" + ex.getMessage(),
            "Export Error",
            JOptionPane.ERROR_MESSAGE);
    }
}


    private void createNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(UIConstants.SIDEBAR_BG);
        navigationPanel.setPreferredSize(new Dimension(200, 0));
        navigationPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER_LIGHT));
        
        // Add navigation buttons
        addNavigationButton(" Dashboard", "dashboard");
        addNavigationButton(" New Invoice", "newInvoice");
        addNavigationButton(" Invoice List", "invoiceList");
        addNavigationButton(" Export", "export");
        addNavigationButton(" Settings", "settings");
        
        // Add spacer
        navigationPanel.add(Box.createVerticalGlue());
    }
    
    private void addNavigationButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(UIConstants.BODY_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(UIConstants.TEXT_DARK);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        button.addActionListener(e -> showPanel(panelName));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.HOVER_BG);
                button.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setOpaque(false);
            }
        });
        
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navigationPanel.add(button);
    }
    
    private void createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.BG_LIGHT);
        
        // Initialize panels
        dashboardPanel = new DashboardPanel(this);
        invoiceListPanel = new InvoiceListPanel(this);
        invoiceFormPanel = new InvoiceFormPanel(this);
        exportPanel = new ExportPanel(this);
        settingsPanel = new SettingsPanel(this);
        
        // Add panels to card layout
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(invoiceListPanel, "invoiceList");
        contentPanel.add(invoiceFormPanel, "newInvoice");
        contentPanel.add(exportPanel, "export");
        contentPanel.add(settingsPanel, "settings");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(navigationPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UIConstants.SIDEBAR_BG);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_LIGHT));
        statusBar.setPreferredSize(new Dimension(0, 28));
        
        JLabel statusLabel = new JLabel(" Ready | Database: Connected");
        statusLabel.setFont(UIConstants.SMALL_FONT);
        statusLabel.setForeground(UIConstants.TEXT_MEDIUM);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        return statusBar;
    }
    
public void showPanel(String panelName) {
    cardLayout.show(contentPanel, panelName);
    
    // Refresh panel data if needed
    switch (panelName) {
        case "dashboard":
            dashboardPanel.refreshData();
            break;
        case "invoiceList":
            invoiceListPanel.refreshData();
            break;
        case "newInvoice":
            // REMOVE THIS LINE:
            // invoiceFormPanel.clearForm();  
            break;
    }
}

public void editInvoice(int invoiceId) {
    showPanel("newInvoice");                  // 1st: Show form (no clearForm() here)
    invoiceFormPanel.loadInvoice(invoiceId);  // 2nd: Load data AFTER showing
}

    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Invoice2X Simple Pro\n" +
            "Version 1.0\n\n" +
            "Professional invoice management with Excel export\n" +
            "Optimized for LibreOffice compatibility\n\n" +
            "© 2025 Invoice2X",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    public static JScrollPane createSmoothScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);  // Smooth scrolling
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Enable smooth scrolling with mouse wheel
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);
        
        return scrollPane;
    }
}
