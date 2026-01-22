package com.invoice2x.ui.panels;

import com.invoice2x.model.Invoice;
import com.invoice2x.model.InvoiceItem;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.ui.MainFrame;
import com.invoice2x.util.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceFormPanel extends JPanel {
    
    private MainFrame mainFrame;
    private Invoice currentInvoice;
    private JLabel titleLabel;  // Make titleLabel a field for dynamic updates
    
    private JTextField invoiceNumberField;
    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextArea customerAddressArea;
    private JTextField invoiceDateField;
    private JTextField dueDateField;
    private JComboBox<Invoice.InvoiceStatus> statusCombo;
    private JTextArea notesArea;
    
    private DefaultTableModel itemsTableModel;
    private JTable itemsTable;
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;
    private JButton headerSaveBtn;
    private JButton bottomSaveBtn;
    private boolean calculatingTotals = false;
    
    public InvoiceFormPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_LIGHT);
        
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // SMOOTH SCROLLING - Content in optimized scroll pane
        JPanel formContent = createFormContent();
        JScrollPane scrollPane = createSmoothScrollPane(formContent);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JScrollPane createSmoothScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(null);
        
        // SMOOTH SCROLLING SETTINGS
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);      // Smooth scroll
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);     // Page scroll
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        
        // Always show vertical scrollbar for long forms
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Enable mouse wheel scrolling
        scrollPane.setWheelScrollingEnabled(true);
        
        return scrollPane;
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        titleLabel = new JLabel("Create New Invoice");  // Initialize as field
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_DARK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(UIConstants.BG_LIGHT);
        
        headerSaveBtn = UIConstants.createSuccessButton("Save Invoice");
        headerSaveBtn.setPreferredSize(UIConstants.BUTTON_LARGE);
        headerSaveBtn.addActionListener(e -> saveInvoice());
        
        JButton cancelBtn = UIConstants.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Discard unsaved changes?", "Confirm",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.showPanel("dashboard");
            }
        });
        
        buttonPanel.add(headerSaveBtn);
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
        
        panel.add(createInvoiceDetailsSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createCustomerSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createItemsSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createTotalsSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        panel.add(createNotesSection());
        panel.add(Box.createRigidArea(new Dimension(0, 24)));
        
        panel.add(createBottomButtons());
        panel.add(Box.createRigidArea(new Dimension(0, 24))); // Extra space at bottom
        
        return panel;
    }
    
    private JPanel createInvoiceDetailsSection() {
        JPanel section = UIConstants.createTitledPanel("Invoice Information");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JPanel grid = new JPanel(new GridLayout(2, 4, 16, 12));
        grid.setBackground(UIConstants.BG_CARD);
        
        grid.add(UIConstants.createLabel("Invoice Number:"));
        invoiceNumberField = UIConstants.createStyledTextField();
        grid.add(invoiceNumberField);
        
        grid.add(UIConstants.createLabel("Invoice Date:"));
        invoiceDateField = UIConstants.createStyledTextField();
        invoiceDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        grid.add(invoiceDateField);
        
        grid.add(UIConstants.createLabel("Status: *"));
        statusCombo = new JComboBox<>(Invoice.InvoiceStatus.values());
        statusCombo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusCombo.setBackground(UIConstants.BG_WHITE);
        statusCombo.setForeground(UIConstants.PRIMARY_COLOR);
        statusCombo.setPreferredSize(new Dimension(0, 38));
        statusCombo.setBorder(BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR, 2));
        grid.add(statusCombo);
        
        grid.add(UIConstants.createLabel("Due Date:"));
        dueDateField = UIConstants.createStyledTextField();
        dueDateField.setText(LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE));
        grid.add(dueDateField);
        
        section.add(grid);
        return section;
    }
    
    private JPanel createCustomerSection() {
        JPanel section = UIConstants.createTitledPanel("Customer Details");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        
        JPanel grid = new JPanel(new GridLayout(3, 2, 16, 12));
        grid.setBackground(UIConstants.BG_CARD);
        
        grid.add(UIConstants.createLabel("Customer Name: *"));
        customerNameField = UIConstants.createStyledTextField();
        grid.add(customerNameField);
        
        grid.add(UIConstants.createLabel("Email:"));
        customerEmailField = UIConstants.createStyledTextField();
        grid.add(customerEmailField);
        
        grid.add(UIConstants.createLabel("Address:"));
        customerAddressArea = UIConstants.createStyledTextArea(3, 30);
        
        // Scrollable text area for long addresses
        JScrollPane addressScroll = new JScrollPane(customerAddressArea);
        addressScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT));
        addressScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addressScroll.getVerticalScrollBar().setUnitIncrement(12);
        
        grid.add(addressScroll);
        
        section.add(grid);
        return section;
    }
    
    private JPanel createItemsSection() {
        JPanel section = UIConstants.createTitledPanel("Line Items");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        
        String[] columnNames = {"#", "Description", "Quantity", "Unit Price", "Total"};
        itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 4;
            }
        };
        
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setFont(UIConstants.BODY_FONT);
        itemsTable.setForeground(UIConstants.TEXT_DARK);
        itemsTable.setBackground(UIConstants.BG_WHITE);
        itemsTable.setRowHeight(32);
        itemsTable.setGridColor(UIConstants.BORDER_LIGHT);
        itemsTable.setSelectionBackground(UIConstants.PRIMARY_LIGHT);
        itemsTable.setSelectionForeground(UIConstants.TEXT_DARK);
        
        itemsTable.getTableHeader().setFont(UIConstants.TITLE_FONT);
        itemsTable.getTableHeader().setBackground(UIConstants.BG_LIGHT);
        itemsTable.getTableHeader().setForeground(UIConstants.TEXT_DARK);
        
        itemsTable.getModel().addTableModelListener(e -> calculateTotals());
        
        // SMOOTH SCROLLING for table
        JScrollPane tableScroll = new JScrollPane(itemsTable);
        tableScroll.setPreferredSize(new Dimension(0, 200));
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT));
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        section.add(tableScroll);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        buttonPanel.setBackground(UIConstants.BG_CARD);
        
        JButton addBtn = UIConstants.createPrimaryButton("Add Item");
        addBtn.addActionListener(e -> addItem());
        
        JButton removeBtn = UIConstants.createDangerButton("Remove");
        removeBtn.addActionListener(e -> removeSelectedItem());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        section.add(buttonPanel);
        
        return section;
    }
    
    private JPanel createTotalsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(UIConstants.BG_CARD);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.SUCCESS_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 10));
        grid.setBackground(UIConstants.BG_CARD);
        
        JLabel subLabel = UIConstants.createLabel("Subtotal:");
        grid.add(subLabel);
        subtotalLabel = new JLabel("$0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        subtotalLabel.setForeground(UIConstants.TEXT_DARK);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        grid.add(subtotalLabel);
        
        JLabel taxLabelTitle = UIConstants.createLabel("Tax (10%):");
        grid.add(taxLabelTitle);
        taxLabel = new JLabel("$0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        taxLabel.setForeground(UIConstants.TEXT_DARK);
        taxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        grid.add(taxLabel);
        
        JLabel totalLabelTitle = UIConstants.createLabel("TOTAL:");
        totalLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        grid.add(totalLabelTitle);
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(UIConstants.SUCCESS_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        grid.add(totalLabel);
        
        section.add(grid);
        return section;
    }
    
    private JPanel createNotesSection() {
        JPanel section = UIConstants.createTitledPanel("Additional Notes");
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        notesArea = UIConstants.createStyledTextArea(4, 50);
        
        // SMOOTH SCROLLING for notes
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        
        section.add(scrollPane);
        
        return section;
    }
    
    private JPanel createBottomButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        bottomSaveBtn = UIConstants.createSuccessButton("SAVE INVOICE");
        bottomSaveBtn.setPreferredSize(new Dimension(200, 46));
        bottomSaveBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bottomSaveBtn.addActionListener(e -> saveInvoice());
        
        JButton cancelBtn = UIConstants.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> mainFrame.showPanel("dashboard"));
        
        panel.add(bottomSaveBtn);
        panel.add(cancelBtn);
        
        return panel;
    }
    
    private void addItem() {
        int rowNum = itemsTableModel.getRowCount() + 1;
        itemsTableModel.addRow(new Object[]{rowNum, "New Item", "1", "0.00", "0.00"});
    }
    
    private void removeSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            itemsTableModel.removeRow(selectedRow);
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                itemsTableModel.setValueAt(i + 1, i, 0);
            }
            calculateTotals();
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a row to remove", "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void calculateTotals() {
        if (calculatingTotals) return; // Prevent infinite recursion
        
        calculatingTotals = true;
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            try {
                String qtyStr = itemsTableModel.getValueAt(i, 2).toString();
                String priceStr = itemsTableModel.getValueAt(i, 3).toString();
                
                BigDecimal qty = new BigDecimal(qtyStr);
                BigDecimal price = new BigDecimal(priceStr);
                BigDecimal lineTotal = qty.multiply(price);
                
                itemsTableModel.setValueAt(lineTotal.setScale(2, RoundingMode.HALF_UP).toString(), i, 4);
                subtotal = subtotal.add(lineTotal);
            } catch (Exception e) {
                // Invalid number, skip
            }
        }
        
        BigDecimal taxRate = new BigDecimal("0.10");
        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);
        
        subtotalLabel.setText("$" + subtotal.setScale(2, RoundingMode.HALF_UP));
        taxLabel.setText("$" + tax);
        totalLabel.setText("$" + total);
        
        calculatingTotals = false;
    }
    
    public void clearForm() {
        try {
            currentInvoice = new Invoice();
            titleLabel.setText("Create New Invoice");  // Update title for new
            invoiceNumberField.setEnabled(true);  // Enable for new
            invoiceNumberField.setText(DatabaseService.getInstance().generateInvoiceNumber());
            customerNameField.setText("");
            customerEmailField.setText("");
            customerAddressArea.setText("");
            invoiceDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            dueDateField.setText(LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE));
            statusCombo.setSelectedItem(Invoice.InvoiceStatus.DRAFT);
            notesArea.setText("");
            
            itemsTableModel.setRowCount(0);
            calculateTotals();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadInvoice(int invoiceId) {
        try {
            System.out.println("DEBUG loadInvoice: Loading invoice ID: " + invoiceId);
            
            // Get invoice from database with ALL data
            currentInvoice = DatabaseService.getInstance().getInvoiceById(invoiceId);
            
            if (currentInvoice == null) {
                System.out.println("ERROR: Invoice not found in database!");
                JOptionPane.showMessageDialog(this,
                    "Invoice not found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("DEBUG: Invoice loaded - " + currentInvoice.getInvoiceNumber());
            System.out.println("DEBUG: Customer - " + currentInvoice.getCustomerName());
            System.out.println("DEBUG: Items count - " + currentInvoice.getItems().size());
            
            // Load ALL fields
            invoiceNumberField.setText(currentInvoice.getInvoiceNumber());
            invoiceNumberField.setEnabled(false); // Don't allow changing invoice number
            
            customerNameField.setText(currentInvoice.getCustomerName());
            customerEmailField.setText(currentInvoice.getCustomerEmail() != null ? currentInvoice.getCustomerEmail() : "");
            customerAddressArea.setText(currentInvoice.getCustomerAddress() != null ? currentInvoice.getCustomerAddress() : "");
            
            invoiceDateField.setText(currentInvoice.getInvoiceDate().format(DateTimeFormatter.ISO_DATE));
            dueDateField.setText(currentInvoice.getDueDate().format(DateTimeFormatter.ISO_DATE));
            
            // Set status
            statusCombo.setSelectedItem(currentInvoice.getStatus());
            System.out.println("DEBUG: Status set to - " + currentInvoice.getStatus());
            
            notesArea.setText(currentInvoice.getNotes() != null ? currentInvoice.getNotes() : "");
            
            // Load items
            itemsTableModel.setRowCount(0);
            int rowNum = 1;
            for (InvoiceItem item : currentInvoice.getItems()) {
                System.out.println("DEBUG: Loading item " + rowNum + " - " + item.getDescription());
                itemsTableModel.addRow(new Object[]{
                    rowNum++,
                    item.getDescription(),
                    item.getQuantity().toString(),
                    item.getUnitPrice().toString(),
                    item.getTotal().toString()
                });
            }
            
            // Recalculate totals
            calculateTotals();
            
            System.out.println("DEBUG: Invoice fully loaded - ready to edit!");
            titleLabel.setText("Edit Invoice");  // Update title for edit
            invoiceNumberField.setEnabled(false);  // Already disabled, but ensure
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR in loadInvoice: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error loading invoice: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveInvoice() {
        try {
            if (customerNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter customer name",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                customerNameField.requestFocus();
                return;
            }
            
            if (itemsTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Please add at least one item",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate table data
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                try {
                    new BigDecimal(itemsTableModel.getValueAt(i, 2).toString());
                    new BigDecimal(itemsTableModel.getValueAt(i, 3).toString());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid number in row " + (i + 1) + ". Please enter valid quantity and unit price.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Validate dates
            try {
                LocalDate.parse(invoiceDateField.getText());
                LocalDate.parse(dueDateField.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD format.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Run save operation in background thread
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private Invoice invoice;
                private Exception error;
                
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        invoice = (currentInvoice != null) ? currentInvoice : new Invoice();
                        invoice.setInvoiceNumber(invoiceNumberField.getText());
                        invoice.setCustomerName(customerNameField.getText());
                        invoice.setCustomerEmail(customerEmailField.getText());
                        invoice.setCustomerAddress(customerAddressArea.getText());
                        invoice.setInvoiceDate(LocalDate.parse(invoiceDateField.getText()));
                        invoice.setDueDate(LocalDate.parse(dueDateField.getText()));
                        invoice.setStatus((Invoice.InvoiceStatus) statusCombo.getSelectedItem());
                        invoice.setNotes(notesArea.getText());
                        
                        invoice.getItems().clear();
                        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                            InvoiceItem item = new InvoiceItem();
                            item.setDescription(itemsTableModel.getValueAt(i, 1).toString());
                            item.setQuantity(new BigDecimal(itemsTableModel.getValueAt(i, 2).toString()));
                            item.setUnitPrice(new BigDecimal(itemsTableModel.getValueAt(i, 3).toString()));
                            item.calculateTotal();
                            invoice.addItem(item);
                        }
                        
                        invoice.calculateTotals(new BigDecimal("0.10"));
                        DatabaseService.getInstance().saveInvoice(invoice);
                    } catch (Exception e) {
                        error = e;
                        throw e;
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get(); // Check for exceptions
                        JOptionPane.showMessageDialog(InvoiceFormPanel.this,
                            "Invoice saved successfully!\n\nInvoice: " + invoice.getInvoiceNumber(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        mainFrame.showPanel("invoiceList");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(InvoiceFormPanel.this,
                            "Error saving invoice: " + (error != null ? error.getMessage() : e.getMessage()),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving invoice: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}