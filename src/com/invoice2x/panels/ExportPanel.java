package com.invoice2x.ui.panels;

import com.invoice2x.model.Invoice;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.service.ExcelExportService;
import com.invoice2x.ui.MainFrame;
import com.invoice2x.util.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


 // FIXED Export Panel - Unique filenames and correct data

public class ExportPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JRadioButton exportAllRadio;
    private JRadioButton exportDateRangeRadio;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField filePathField;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JButton exportButton;
    // Optional single-invoice export target (can be passed into constructor)
    private Invoice selectedInvoice;
    
    public ExportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    public ExportPanel(MainFrame mainFrame, Invoice selectedInvoice) {
        this.mainFrame = mainFrame;
        this.selectedInvoice = selectedInvoice;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = createContent();
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        JLabel titleLabel = new JLabel("Export to Excel");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_DARK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(UIConstants.BG_LIGHT);
        
        exportButton = UIConstants.createSuccessButton("EXPORT NOW");
        exportButton.setPreferredSize(UIConstants.BUTTON_LARGE);
        exportButton.addActionListener(e -> performExport());
        
        JButton cancelBtn = UIConstants.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> mainFrame.showPanel("dashboard"));
        
        buttonPanel.add(exportButton);
        buttonPanel.add(cancelBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_LIGHT);
        
        panel.add(createSelectionSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createOptionsSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createOutputSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createProgressSection());
        panel.add(Box.createRigidArea(new Dimension(0, 24)));
        
        panel.add(createBottomButton());
        
        return panel;
    }
    
    private JPanel createSelectionSection() {
        JPanel section = UIConstants.createTitledPanel("Select Invoices to Export");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        ButtonGroup group = new ButtonGroup();
        
        exportAllRadio = new JRadioButton("Export all invoices");
        exportAllRadio.setFont(UIConstants.BODY_FONT);
        exportAllRadio.setForeground(UIConstants.TEXT_DARK);
        exportAllRadio.setBackground(UIConstants.BG_CARD);
        exportAllRadio.setSelected(true);
        
        exportDateRangeRadio = new JRadioButton("Export by date range:");
        exportDateRangeRadio.setFont(UIConstants.BODY_FONT);
        exportDateRangeRadio.setForeground(UIConstants.TEXT_DARK);
        exportDateRangeRadio.setBackground(UIConstants.BG_CARD);
        
        group.add(exportAllRadio);
        group.add(exportDateRangeRadio);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        datePanel.setBackground(UIConstants.BG_CARD);
        
        JLabel fromLabel = UIConstants.createLabel("From:");
        startDateField = UIConstants.createStyledTextField();
        startDateField.setPreferredSize(new Dimension(150, 38));
        startDateField.setText(LocalDate.now().minusMonths(1).toString());
        
        JLabel toLabel = UIConstants.createLabel("To:");
        endDateField = UIConstants.createStyledTextField();
        endDateField.setPreferredSize(new Dimension(150, 38));
        endDateField.setText(LocalDate.now().toString());
        
        datePanel.add(fromLabel);
        datePanel.add(startDateField);
        datePanel.add(toLabel);
        datePanel.add(endDateField);
        
        section.add(exportAllRadio);
        section.add(Box.createRigidArea(new Dimension(0, 8)));
        section.add(exportDateRangeRadio);
        section.add(datePanel);
        
        return section;
    }
    
    private JPanel createOptionsSection() {
        JPanel section = UIConstants.createTitledPanel("LibreOffice Compatibility");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        
        JCheckBox optimizeCheckbox = new JCheckBox("Optimize for LibreOffice Calc");
        optimizeCheckbox.setFont(UIConstants.BODY_FONT);
        optimizeCheckbox.setForeground(UIConstants.TEXT_DARK);
        optimizeCheckbox.setBackground(UIConstants.BG_CARD);
        optimizeCheckbox.setSelected(true);
        
        JCheckBox simpleFormatCheckbox = new JCheckBox("Use simple formatting");
        simpleFormatCheckbox.setFont(UIConstants.BODY_FONT);
        simpleFormatCheckbox.setForeground(UIConstants.TEXT_DARK);
        simpleFormatCheckbox.setBackground(UIConstants.BG_CARD);
        simpleFormatCheckbox.setSelected(true);
        
        JLabel infoLabel = new JLabel("<html><i> These options ensure maximum compatibility</i></html>");
        infoLabel.setFont(UIConstants.SMALL_FONT);
        infoLabel.setForeground(UIConstants.TEXT_MEDIUM);
        
        section.add(optimizeCheckbox);
        section.add(Box.createRigidArea(new Dimension(0, 8)));
        section.add(simpleFormatCheckbox);
        section.add(Box.createRigidArea(new Dimension(0, 8)));
        section.add(infoLabel);
        
        return section;
    }
    
    private JPanel createOutputSection() {
        JPanel section = UIConstants.createTitledPanel("Output Location");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        JPanel filePanel = new JPanel(new BorderLayout(12, 12));
        filePanel.setBackground(UIConstants.BG_CARD);
        
        JLabel pathLabel = UIConstants.createLabel("Save to:");
        
        filePathField = UIConstants.createStyledTextField();
        filePathField.setPreferredSize(new Dimension(500, 38));
        
        // FIX: Generate UNIQUE filename with timestamp
        updateDefaultFilename();
        
        JButton browseBtn = UIConstants.createSecondaryButton("Browse");
        browseBtn.addActionListener(e -> browseForFile());
        
        JPanel fieldPanel = new JPanel(new BorderLayout(8, 0));
        fieldPanel.setBackground(UIConstants.BG_CARD);
        fieldPanel.add(filePathField, BorderLayout.CENTER);
        fieldPanel.add(browseBtn, BorderLayout.EAST);
        
        filePanel.add(pathLabel, BorderLayout.NORTH);
        filePanel.add(fieldPanel, BorderLayout.CENTER);
        
        section.add(filePanel);
        
        return section;
    }
    // inside ExportPanel
private java.util.List<Invoice> preloadedInvoices; // for multi-select batch from list

public void setPreloadedInvoices(java.util.List<Invoice> invoices) {
    this.preloadedInvoices = invoices;
}

    
    /**
     * FIX: Generate unique filename with timestamp
     */
    private void updateDefaultFilename() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String defaultPath = System.getProperty("user.home") + File.separator + 
                           "Invoices_" + timestamp + ".xlsx";
        filePathField.setText(defaultPath);
    }
    
    private JPanel createProgressSection() {
        JPanel section = UIConstants.createTitledPanel("Export Progress");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 32));
        progressBar.setFont(UIConstants.BODY_FONT);
        progressBar.setForeground(UIConstants.SUCCESS_COLOR);
        progressBar.setBackground(UIConstants.BG_LIGHT);
        
        progressLabel = new JLabel("⏳ Ready to export");
        progressLabel.setFont(UIConstants.BODY_FONT);
        progressLabel.setForeground(UIConstants.TEXT_MEDIUM);
        
        section.add(progressLabel);
        section.add(Box.createRigidArea(new Dimension(0, 8)));
        section.add(progressBar);
        
        return section;
    }
    
    private JPanel createBottomButton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JButton bigExportBtn = UIConstants.createSuccessButton("EXPORT TO EXCEL NOW");
        bigExportBtn.setPreferredSize(new Dimension(280, 50));
        bigExportBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bigExportBtn.addActionListener(e -> performExport());
        
        panel.add(bigExportBtn);
        
        return panel;
    }
    
    private void browseForFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setSelectedFile(new File(filePathField.getText()));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) {
                path += ".xlsx";
            }
            filePathField.setText(path);
        }
    }
    
   
    private void performExport() {
    updateDefaultFilename();
        exportButton.setEnabled(false);
        progressBar.setValue(0);
        progressLabel.setText("Preparing export...");
        progressLabel.setForeground(UIConstants.PRIMARY_COLOR);
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        private String errorMessage = null;
        private List<Invoice> invoicesToExport = null;

        @Override
       protected Void doInBackground() {
    try {
        DatabaseService db = DatabaseService.getInstance();

        if (selectedInvoice != null) {
            // SINGLE INVOICE export (already working)
            invoicesToExport = new java.util.ArrayList<>();
            Invoice full = db.getInvoiceById(selectedInvoice.getId());
            if (full == null) {
                errorMessage = "Selected invoice not found in database";
                return null;
            }
            if (full.getItems() == null) {
                full.setItems(new java.util.ArrayList<>());
            } else {
                full.setItems(new java.util.ArrayList<>(full.getItems()));
            }
            invoicesToExport.add(full);

            System.out.println("DEBUG: Exporting SINGLE invoice - " +
                               full.getInvoiceNumber() + " - Items: " + full.getItems().size());

        } else if (preloadedInvoices != null && !preloadedInvoices.isEmpty()) {
            // MULTI-SELECT from list
            invoicesToExport = new java.util.ArrayList<>();

            for (Invoice inv : preloadedInvoices) {
                Invoice full = db.getInvoiceById(inv.getId());
                if (full != null) {
                    if (full.getItems() == null) {
                        full.setItems(new java.util.ArrayList<>());
                    } else {
                        full.setItems(new java.util.ArrayList<>(full.getItems()));
                    }
                    invoicesToExport.add(full);
                }
            }

            System.out.println("DEBUG: Exporting SELECTED invoices - Count: " + invoicesToExport.size());

        } else {
            // ORIGINAL batch (all/date range) for menu/sidebar Export
            if (exportAllRadio.isSelected()) {
                invoicesToExport = db.getAllInvoices();
                System.out.println("DEBUG: Exporting ALL invoices - Count: " + invoicesToExport.size());
            } else {
                LocalDate startDate = LocalDate.parse(startDateField.getText());
                LocalDate endDate = LocalDate.parse(endDateField.getText());

                java.util.List<Invoice> allInvoices = db.getAllInvoices();
                invoicesToExport = new java.util.ArrayList<>();
                for (Invoice inv : allInvoices) {
                    if (!inv.getInvoiceDate().isBefore(startDate) &&
                        !inv.getInvoiceDate().isAfter(endDate)) {
                        invoicesToExport.add(inv);
                    }
                }
                System.out.println("DEBUG: Exporting date range - Count: " + invoicesToExport.size());
            }

            if (invoicesToExport == null || invoicesToExport.isEmpty()) {
                errorMessage = "No invoices found to export";
                return null;
            }
        }

                String filePath = filePathField.getText();
                ExcelExportService exportService = ExcelExportService.getInstance();

                exportService.exportInvoices(invoicesToExport, filePath,
                    new ExcelExportService.ExportProgressListener() {
                        @Override
                        public void onProgress(int current, int total, String message) {
                            SwingUtilities.invokeLater(() -> {
                                int percent = (int) ((current / (double) total) * 100);
                                progressBar.setValue(percent);
                                progressLabel.setText(message);
                                progressLabel.setForeground(UIConstants.PRIMARY_COLOR);
                            });
                        }

                        @Override
                        public void onComplete(String filePath) {
                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(100);
                                progressLabel.setText("Export completed successfully!");
                                progressLabel.setForeground(UIConstants.SUCCESS_COLOR);
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            errorMessage = e.getMessage();
                        }
                    });

            } catch (Exception e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            exportButton.setEnabled(true);
            // keep your existing JOptionPane and open-folder code
        }
    };

    worker.execute();
}
}
