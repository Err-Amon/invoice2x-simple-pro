package com.invoice2x.ui.panels;

import com.invoice2x.model.Invoice;
import com.invoice2x.service.DatabaseService;
import com.invoice2x.ui.MainFrame;
import com.invoice2x.util.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;


 // Dashboard with SMOOTH scrolling for invoice list
 
public class DashboardPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel totalInvoicesLabel;
    private JLabel thisMonthLabel;
    private JLabel pendingLabel;
    private JPanel recentInvoicesPanel;
    
    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
        refreshData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_DARK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(UIConstants.BG_LIGHT);
        
        JButton newInvoiceBtn = UIConstants.createPrimaryButton("New Invoice");
        newInvoiceBtn.addActionListener(e -> mainFrame.showPanel("newInvoice"));
        
        JButton exportBtn = UIConstants.createSecondaryButton("Export");
        exportBtn.addActionListener(e -> mainFrame.showPanel("export"));
        
        buttonPanel.add(newInvoiceBtn);
        buttonPanel.add(exportBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);
        
        // Stats cards row
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsPanel.setBackground(UIConstants.BG_LIGHT);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        JPanel totalCard = createStatCard("Total Invoices", "0", UIConstants.PRIMARY_COLOR);
        JPanel monthCard = createStatCard("This Month", "$0", UIConstants.SUCCESS_COLOR);
        JPanel pendingCard = createStatCard("Pending", "$0", UIConstants.WARNING_COLOR);
        
        // The content panel contains: titleLabel (0), rigid spacer (1), valueLabel (2)
        totalInvoicesLabel = (JLabel) ((JPanel) totalCard.getComponent(0)).getComponent(2);
        thisMonthLabel = (JLabel) ((JPanel) monthCard.getComponent(0)).getComponent(2);
        pendingLabel = (JLabel) ((JPanel) pendingCard.getComponent(0)).getComponent(2);
        
        cardsPanel.add(totalCard);
        cardsPanel.add(monthCard);
        cardsPanel.add(pendingCard);
        
        // Recent invoices with SMOOTH SCROLLING
        JPanel recentSection = createRecentInvoicesSection();
        
        panel.add(cardsPanel, BorderLayout.NORTH);
        panel.add(recentSection, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BG_CARD);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.BODY_FONT);
        titleLabel.setForeground(UIConstants.TEXT_MEDIUM);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(valueLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentInvoicesSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(UIConstants.BG_CARD);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Recent Invoices");
        titleLabel.setFont(UIConstants.SUBHEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        recentInvoicesPanel = new JPanel();
        recentInvoicesPanel.setLayout(new BoxLayout(recentInvoicesPanel, BoxLayout.Y_AXIS));
        recentInvoicesPanel.setBackground(UIConstants.BG_CARD);
        
        // SMOOTH SCROLLING for invoice list
        JScrollPane scrollPane = new JScrollPane(recentInvoicesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UIConstants.BG_CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);  // Smooth!
        scrollPane.getVerticalScrollBar().setBlockIncrement(80);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        section.add(titleLabel, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);
        
        return section;
    }
    
    public void refreshData() {
        try {
            DatabaseService db = DatabaseService.getInstance();
            List<Invoice> allInvoices = db.getAllInvoices();
            
            totalInvoicesLabel.setText(String.valueOf(allInvoices.size()));
            
            BigDecimal monthTotal = BigDecimal.ZERO;
            BigDecimal pendingTotal = BigDecimal.ZERO;
            
            java.time.LocalDate now = java.time.LocalDate.now();
            for (Invoice inv : allInvoices) {
                if (inv.getInvoiceDate().getMonth() == now.getMonth() &&
                    inv.getInvoiceDate().getYear() == now.getYear()) {
                    monthTotal = monthTotal.add(inv.getTotal());
                }
                
                if (inv.getStatus() == Invoice.InvoiceStatus.PENDING) {
                    pendingTotal = pendingTotal.add(inv.getTotal());
                }
            }
            
            thisMonthLabel.setText("$" + monthTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
            pendingLabel.setText("$" + pendingTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
            
            // Update recent invoices
            recentInvoicesPanel.removeAll();
            
            // Show more invoices (10 instead of 5)
            int count = 0;
            for (Invoice inv : allInvoices) {
                if (count >= 10) break;
                recentInvoicesPanel.add(createInvoiceRow(inv));
                count++;
            }
            
            recentInvoicesPanel.revalidate();
            recentInvoicesPanel.repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading dashboard data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createInvoiceRow(Invoice invoice) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(UIConstants.BG_CARD);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(12, 0, 12, 0)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        infoPanel.setBackground(UIConstants.BG_CARD);
        
        JLabel numberLabel = new JLabel(invoice.getInvoiceNumber());
        numberLabel.setFont(UIConstants.TITLE_FONT);
        numberLabel.setForeground(UIConstants.TEXT_DARK);
        
        JLabel customerLabel = new JLabel(invoice.getCustomerName());
        customerLabel.setFont(UIConstants.BODY_FONT);
        customerLabel.setForeground(UIConstants.TEXT_MEDIUM);
        
        JLabel dateLabel = new JLabel(invoice.getInvoiceDate().toString());
        dateLabel.setFont(UIConstants.SMALL_FONT);
        dateLabel.setForeground(UIConstants.TEXT_LIGHT);
        
        JLabel amountLabel = new JLabel("$" + invoice.getTotal());
        amountLabel.setFont(UIConstants.TITLE_FONT);
        amountLabel.setForeground(UIConstants.SUCCESS_COLOR);
        
        infoPanel.add(numberLabel);
        infoPanel.add(new JLabel("•"));
        infoPanel.add(customerLabel);
        infoPanel.add(new JLabel("•"));
        infoPanel.add(dateLabel);
        
        row.add(infoPanel, BorderLayout.WEST);
        row.add(amountLabel, BorderLayout.EAST);
        
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.editInvoice(invoice.getId());
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                row.setBackground(UIConstants.HOVER_BG);
                infoPanel.setBackground(UIConstants.HOVER_BG);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                row.setBackground(UIConstants.BG_CARD);
                infoPanel.setBackground(UIConstants.BG_CARD);
            }
        });
        
        return row;
    }
}