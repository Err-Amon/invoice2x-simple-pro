package com.invoice2x.ui.panels;

import com.invoice2x.model.Invoice;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.ui.MainFrame;
import com.invoice2x.util.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class InvoiceListPanel extends JPanel {
    
    private MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    
    public InvoiceListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        JLabel titleLabel = new JLabel("Invoice List");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_DARK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(UIConstants.BG_LIGHT);
        
        JButton newBtn = UIConstants.createPrimaryButton("New Invoice");
        newBtn.addActionListener(e -> mainFrame.showPanel("newInvoice"));
        
        JButton exportBtn = UIConstants.createSecondaryButton("Export Selected");
        exportBtn.addActionListener(e -> exportSelected());
        
        JButton deleteBtn = UIConstants.createDangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelected());
        
        buttonPanel.add(newBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createToolbar() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BG_LIGHT);
        
        // Search and filter bar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        toolbar.setBackground(UIConstants.BG_LIGHT);
        
        JLabel searchLabel = UIConstants.createLabel("Search:");
        
        searchField = UIConstants.createStyledTextField();
        searchField.setPreferredSize(new Dimension(250, 38));
        searchField.addActionListener(e -> refreshData());
        
        JLabel filterLabel = UIConstants.createLabel("Filter:");
        
        String[] filters = {"All", "Draft", "Pending", "Paid", "Overdue", "Cancelled"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(UIConstants.BODY_FONT);
        filterCombo.setBackground(UIConstants.BG_WHITE);
        filterCombo.setForeground(UIConstants.TEXT_DARK);
        filterCombo.setPreferredSize(new Dimension(150, 38));
        filterCombo.addActionListener(e -> refreshData());
        
        JButton searchBtn = UIConstants.createPrimaryButton("Search");
        searchBtn.addActionListener(e -> refreshData());
        
        toolbar.add(searchLabel);
        toolbar.add(searchField);
        toolbar.add(filterLabel);
        toolbar.add(filterCombo);
        toolbar.add(searchBtn);
        
        // Table with action buttons
        createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void createTable() {
        String[] columnNames = {"Select", "Invoice #", "Customer", "Date", "Amount", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 6; // Checkbox and Actions
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(UIConstants.BODY_FONT);
        table.setForeground(UIConstants.TEXT_DARK);
        table.setBackground(UIConstants.BG_WHITE);
        table.setRowHeight(45);
        table.getTableHeader().setFont(UIConstants.TITLE_FONT);
        table.getTableHeader().setBackground(UIConstants.BG_LIGHT);
        table.getTableHeader().setForeground(UIConstants.TEXT_DARK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(UIConstants.BORDER_LIGHT);
        table.setSelectionBackground(UIConstants.PRIMARY_LIGHT);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // Select
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Invoice #
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Customer
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Date
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Amount
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Status
        table.getColumnModel().getColumn(6).setPreferredWidth(180);  // Actions
        
        // Add custom renderer for Actions column with buttons
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor(new JCheckBox()));
    }
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewBtn;
        private JButton editBtn;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setBackground(UIConstants.BG_WHITE);
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.SMALL_FONT);
            viewBtn.setBackground(UIConstants.INFO_COLOR);
            viewBtn.setForeground(UIConstants.TEXT_WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setBorderPainted(false);
            viewBtn.setPreferredSize(new Dimension(75, 32));
            
            editBtn = new JButton("Edit");
            editBtn.setFont(UIConstants.SMALL_FONT);
            editBtn.setBackground(UIConstants.PRIMARY_COLOR);
            editBtn.setForeground(UIConstants.TEXT_WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setPreferredSize(new Dimension(75, 32));
            
            add(viewBtn);
            add(editBtn);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn;
        private JButton editBtn;
        private int currentRow;
        
        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            panel.setBackground(UIConstants.BG_WHITE);
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.SMALL_FONT);
            viewBtn.setBackground(UIConstants.INFO_COLOR);
            viewBtn.setForeground(UIConstants.TEXT_WHITE);
            viewBtn.setFocusPainted(false);
            viewBtn.setBorderPainted(false);
            viewBtn.setPreferredSize(new Dimension(75, 32));
            
            editBtn = new JButton("Edit");
            editBtn.setFont(UIConstants.SMALL_FONT);
            editBtn.setBackground(UIConstants.PRIMARY_COLOR);
            editBtn.setForeground(UIConstants.TEXT_WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setPreferredSize(new Dimension(75, 32));
            
            // VIEW BUTTON ACTION
            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                viewInvoice(currentRow);
            });
            
            // EDIT BUTTON ACTION
            editBtn.addActionListener(e -> {
                fireEditingStopped();
                editInvoice(currentRow);
            });
            
            panel.add(viewBtn);
            panel.add(editBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
    
    private void viewInvoice(int row) {
        try {
            String invoiceNumber = tableModel.getValueAt(row, 1).toString();
            DatabaseService db = DatabaseService.getInstance();
            List<Invoice> allInvoices = db.getAllInvoices();
            
            for (Invoice inv : allInvoices) {
                if (inv.getInvoiceNumber().equals(invoiceNumber)) {
                    showInvoiceViewDialog(inv);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error viewing invoice: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editInvoice(int row) {
        try {
            String invoiceNumber = tableModel.getValueAt(row, 1).toString();
            DatabaseService db = DatabaseService.getInstance();
            List<Invoice> allInvoices = db.getAllInvoices();
            
            for (Invoice inv : allInvoices) {
                if (inv.getInvoiceNumber().equals(invoiceNumber)) {
                    System.out.println("DEBUG: Editing invoice ID: " + inv.getId() + 
                                     " - " + inv.getInvoiceNumber());
                    mainFrame.editInvoice(inv.getId());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading invoice: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showInvoiceViewDialog(Invoice invoice) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "View Invoice - " + invoice.getInvoiceNumber(), true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Invoice header
        JLabel titleLabel = new JLabel("Invoice: " + invoice.getInvoiceNumber());
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Invoice details
        panel.add(createViewRow("Date:", invoice.getInvoiceDate().toString()));
        panel.add(createViewRow("Due Date:", invoice.getDueDate().toString()));
        panel.add(createViewRow("Status:", invoice.getStatus().getDisplayName()));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panel.add(createViewRow("Customer:", invoice.getCustomerName()));
        panel.add(createViewRow("Email:", invoice.getCustomerEmail()));
        panel.add(createViewRow("Address:", invoice.getCustomerAddress()));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Items
        JLabel itemsLabel = new JLabel("Items:");
        itemsLabel.setFont(UIConstants.TITLE_FONT);
        panel.add(itemsLabel);
        
        for (int i = 0; i < invoice.getItems().size(); i++) {
            var item = invoice.getItems().get(i);
            String itemText = String.format("%d. %s - Qty: %s × $%s = $%s",
                i + 1,
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal()
            );
            JLabel itemLabel = new JLabel(itemText);
            itemLabel.setFont(UIConstants.BODY_FONT);
            panel.add(itemLabel);
        }
        
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Totals
        panel.add(createViewRow("Subtotal:", "$" + invoice.getSubtotal()));
        panel.add(createViewRow("Tax:", "$" + invoice.getTax()));
        JLabel totalLabel = new JLabel("Total: $" + invoice.getTotal());
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(UIConstants.SUCCESS_COLOR);
        panel.add(totalLabel);
        
        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(createViewRow("Notes:", invoice.getNotes()));
        }
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
    
    private JPanel createViewRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setBackground(UIConstants.BG_WHITE);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(UIConstants.TITLE_FONT);
        labelComp.setForeground(UIConstants.TEXT_DARK);
        
        JLabel valueComp = new JLabel(value != null ? value : "");
        valueComp.setFont(UIConstants.BODY_FONT);
        valueComp.setForeground(UIConstants.TEXT_MEDIUM);
        
        row.add(labelComp);
        row.add(valueComp);
        
        return row;
    }
    
    public void refreshData() {
        try {
            DatabaseService db = DatabaseService.getInstance();
            List<Invoice> invoices = db.getAllInvoices();
            
            String searchTerm = searchField.getText().toLowerCase();
            String filter = filterCombo.getSelectedItem().toString();
            
            tableModel.setRowCount(0);
            
            for (Invoice inv : invoices) {
                // Apply search filter
                if (!searchTerm.isEmpty()) {
                    boolean matches = inv.getInvoiceNumber().toLowerCase().contains(searchTerm) ||
                                    inv.getCustomerName().toLowerCase().contains(searchTerm);
                    if (!matches) continue;
                }
                
                // Apply status filter
                if (!filter.equals("All")) {
                    if (!inv.getStatus().getDisplayName().equalsIgnoreCase(filter)) {
                        continue;
                    }
                }
                
                tableModel.addRow(new Object[]{
                    false,
                    inv.getInvoiceNumber(),
                    inv.getCustomerName(),
                    inv.getInvoiceDate().toString(),
                    "$" + inv.getTotal(),
                    inv.getStatus().getDisplayName(),
                    "" // Actions column (buttons rendered)
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading invoices: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportSelected() {
        List<String> selectedInvoices = getSelectedInvoices();
        if (selectedInvoices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one invoice to export",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        mainFrame.showPanel("export");
    }
    
    private void deleteSelected() {
        List<String> selectedInvoices = getSelectedInvoices();
        if (selectedInvoices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one invoice to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + selectedInvoices.size() + " invoice(s)?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseService db = DatabaseService.getInstance();
                List<Invoice> allInvoices = db.getAllInvoices();
                
                for (String invoiceNumber : selectedInvoices) {
                    for (Invoice inv : allInvoices) {
                        if (inv.getInvoiceNumber().equals(invoiceNumber)) {
                            db.deleteInvoice(inv.getId());
                            break;
                        }
                    }
                }
                
                refreshData();
                
                JOptionPane.showMessageDialog(this,
                    "" + selectedInvoices.size() + " invoice(s) deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting invoices: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private List<String> getSelectedInvoices() {
        List<String> selected = new java.util.ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                selected.add(tableModel.getValueAt(i, 1).toString());
            }
        }
        return selected;
    }
}