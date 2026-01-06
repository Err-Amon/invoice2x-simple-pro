package com.invoice2x.ui.panels;

import com.invoice2x.model.Invoice;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.ui.MainFrame;
import com.invoice2x.util.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


 // Invoice list panel with search and filter capabilities

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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING_LARGE, UIConstants.PADDING_LARGE,
            UIConstants.PADDING_LARGE, UIConstants.PADDING_LARGE
        ));
        
        // Header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Search and filter
        JPanel toolbarPanel = createToolbar();
        add(toolbarPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 
            UIConstants.PADDING_LARGE, 0));
        
        JLabel titleLabel = new JLabel("Invoice List");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newBtn = UIConstants.createPrimaryButton("New Invoice");
        newBtn.addActionListener(e -> mainFrame.showPanel("newInvoice"));
        
        JButton exportBtn = UIConstants.createSecondaryButton("Export Selected");
        exportBtn.addActionListener(e -> exportSelected());
        
        JButton deleteBtn = UIConstants.createSecondaryButton("Delete");
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
        mainPanel.setBackground(Color.WHITE);
        
        // Search and filter bar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        toolbar.setBackground(Color.WHITE);
        
        // Search
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.BODY_FONT);
        
        searchField = UIConstants.createStyledTextField();
        searchField.setPreferredSize(new Dimension(250, 32));
        searchField.addActionListener(e -> refreshData());
        
        // Filter
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(UIConstants.BODY_FONT);
        
        String[] filters = {"All", "Draft", "Pending", "Paid", "Overdue"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(UIConstants.BODY_FONT);
        filterCombo.setPreferredSize(new Dimension(150, 32));
        filterCombo.addActionListener(e -> refreshData());
        
        JButton searchBtn = UIConstants.createPrimaryButton("Search");
        searchBtn.addActionListener(e -> refreshData());
        
        toolbar.add(searchLabel);
        toolbar.add(searchField);
        toolbar.add(filterLabel);
        toolbar.add(filterCombo);
        toolbar.add(searchBtn);
        
        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1));
        
        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void createTable() {
        String[] columnNames = {"Select", "Invoice #", "Customer", "Date", "Amount", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox editable
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(UIConstants.BODY_FONT);
        table.setRowHeight(35);
        table.getTableHeader().setFont(UIConstants.TITLE_FONT);
        table.getTableHeader().setBackground(UIConstants.BG_SECONDARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Double-click to edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String invoiceNumber = tableModel.getValueAt(row, 1).toString();
                        editInvoiceByNumber(invoiceNumber);
                    }
                }
            }
        });
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
                    if (!inv.getStatus().toString().equalsIgnoreCase(filter)) {
                        continue;
                    }
                }
                
                tableModel.addRow(new Object[]{
                    false,
                    inv.getInvoiceNumber(),
                    inv.getCustomerName(),
                    inv.getInvoiceDate().toString(),
                    "$" + inv.getTotal(),
                    inv.getStatus().getDisplayName()
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
    
    private void editInvoiceByNumber(String invoiceNumber) {
        try {
            DatabaseService db = DatabaseService.getInstance();
            List<Invoice> invoices = db.getAllInvoices();
            
            for (Invoice inv : invoices) {
                if (inv.getInvoiceNumber().equals(invoiceNumber)) {
                    mainFrame.editInvoice(inv.getId());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    try {
        DatabaseService db = DatabaseService.getInstance();
        List<Invoice> allInvoices = db.getAllInvoices();

        // Collect IDs of ALL selected invoices
        java.util.List<Integer> selectedIds = new java.util.ArrayList<>();

        for (String invoiceNumber : selectedInvoices) {
            for (Invoice inv : allInvoices) {
                if (inv.getInvoiceNumber().equals(invoiceNumber)) {
                    selectedIds.add(inv.getId());
                    break;
                }
            }
        }

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selected invoices not found in database",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // IMPORTANT: export ALL selected invoices in ONE Excel file (multiple sheets)
        mainFrame.exportMultipleInvoices(selectedIds);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error exporting invoices: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
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
                    "Invoices deleted successfully",
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
            if (isSelected) {
                selected.add(tableModel.getValueAt(i, 1).toString());
            }
        }
        return selected;
    }
}
