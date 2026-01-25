package com.invoice2x.service;

import com.invoice2x.model.Invoice;
import com.invoice2x.model.InvoiceItem;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class DatabaseService {
    
    private static DatabaseService instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:invoice2x.db";
    
    private DatabaseService() {
    }
    
    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    public boolean initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        
        String invoicesTable = "CREATE TABLE IF NOT EXISTS invoices (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "invoice_number VARCHAR(50) UNIQUE NOT NULL," +
            "customer_name VARCHAR(200) NOT NULL," +
            "customer_email VARCHAR(200)," +
            "customer_address TEXT," +
            "invoice_date DATE NOT NULL," +
            "due_date DATE NOT NULL," +
            "status VARCHAR(20) NOT NULL," +
            "subtotal DECIMAL(10,2) NOT NULL," +
            "tax DECIMAL(10,2) NOT NULL," +
            "total DECIMAL(10,2) NOT NULL," +
            "notes TEXT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        stmt.execute(invoicesTable);
        
        String itemsTable = "CREATE TABLE IF NOT EXISTS invoice_items (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "invoice_id INTEGER NOT NULL," +
            "description VARCHAR(500) NOT NULL," +
            "quantity DECIMAL(10,2) NOT NULL," +
            "unit_price DECIMAL(10,2) NOT NULL," +
            "total DECIMAL(10,2) NOT NULL," +
            "FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE" +
            ")";
        stmt.execute(itemsTable);
        
        String settingsTable = "CREATE TABLE IF NOT EXISTS settings (" +
            "key VARCHAR(100) PRIMARY KEY," +
            "value TEXT" +
            ")";
        stmt.execute(settingsTable);
        
        stmt.close();
    }
    
    public int saveInvoice(Invoice invoice) throws SQLException {
        if (invoice.getId() == 0) {
            return insertInvoice(invoice);
        } else {
            updateInvoice(invoice);
            return invoice.getId();
        }
    }
    
    private int insertInvoice(Invoice invoice) throws SQLException {
        // Ensure invoice number is present and unique before inserting
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().trim().isEmpty()) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        } else {
            String checkSql = "SELECT id FROM invoices WHERE invoice_number=?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, invoice.getInvoiceNumber());
            ResultSet rsCheck = checkStmt.executeQuery();
            boolean exists = rsCheck.next();
            rsCheck.close();
            checkStmt.close();

            if (exists) {
                // If the provided invoice number already exists, generate a new unique one
                String newNum;
                do {
                    newNum = generateInvoiceNumber();
                    PreparedStatement tmp = connection.prepareStatement(checkSql);
                    tmp.setString(1, newNum);
                    ResultSet r = tmp.executeQuery();
                    exists = r.next();
                    r.close();
                    tmp.close();
                } while (exists);
                invoice.setInvoiceNumber(newNum);
            }
        }

        String sql = "INSERT INTO invoices (invoice_number, customer_name, customer_email, " +
            "customer_address, invoice_date, due_date, status, subtotal, tax, total, notes) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, invoice.getInvoiceNumber());
        pstmt.setString(2, invoice.getCustomerName());
        pstmt.setString(3, invoice.getCustomerEmail());
        pstmt.setString(4, invoice.getCustomerAddress());
        pstmt.setDate(5, Date.valueOf(invoice.getInvoiceDate()));
        pstmt.setDate(6, Date.valueOf(invoice.getDueDate()));
        pstmt.setString(7, invoice.getStatus().name());
        pstmt.setBigDecimal(8, invoice.getSubtotal());
        pstmt.setBigDecimal(9, invoice.getTax());
        pstmt.setBigDecimal(10, invoice.getTotal());
        pstmt.setString(11, invoice.getNotes());
        
        pstmt.executeUpdate();

        int invoiceId = 0;
        try {
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                invoiceId = rs.getInt(1);
                invoice.setId(invoiceId);
            }
            if (rs != null) rs.close();
        } catch (SQLFeatureNotSupportedException ex) {
            // SQLite JDBC may not support getGeneratedKeys; fallback to last_insert_rowid()
            Statement lastIdStmt = connection.createStatement();
            ResultSet rs2 = lastIdStmt.executeQuery("SELECT last_insert_rowid()");
            if (rs2.next()) {
                invoiceId = rs2.getInt(1);
                invoice.setId(invoiceId);
            }
            rs2.close();
            lastIdStmt.close();
        }
        
        for (InvoiceItem item : invoice.getItems()) {
            saveInvoiceItem(invoiceId, item);
        }
        
        pstmt.close();
        return invoiceId;
    }
    
    private void updateInvoice(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET invoice_number=?, customer_name=?, customer_email=?, " +
            "customer_address=?, invoice_date=?, due_date=?, status=?, subtotal=?, tax=?, " +
            "total=?, notes=? WHERE id=?";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, invoice.getInvoiceNumber());
        pstmt.setString(2, invoice.getCustomerName());
        pstmt.setString(3, invoice.getCustomerEmail());
        pstmt.setString(4, invoice.getCustomerAddress());
        pstmt.setDate(5, Date.valueOf(invoice.getInvoiceDate()));
        pstmt.setDate(6, Date.valueOf(invoice.getDueDate()));
        pstmt.setString(7, invoice.getStatus().name());
        pstmt.setBigDecimal(8, invoice.getSubtotal());
        pstmt.setBigDecimal(9, invoice.getTax());
        pstmt.setBigDecimal(10, invoice.getTotal());
        pstmt.setString(11, invoice.getNotes());
        pstmt.setInt(12, invoice.getId());
        
        pstmt.executeUpdate();
        
        deleteInvoiceItems(invoice.getId());
        for (InvoiceItem item : invoice.getItems()) {
            saveInvoiceItem(invoice.getId(), item);
        }
        
        pstmt.close();
    }
    
    private void saveInvoiceItem(int invoiceId, InvoiceItem item) throws SQLException {
        String sql = "INSERT INTO invoice_items (invoice_id, description, quantity, " +
            "unit_price, total) VALUES (?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, invoiceId);
        pstmt.setString(2, item.getDescription());
        pstmt.setBigDecimal(3, item.getQuantity());
        pstmt.setBigDecimal(4, item.getUnitPrice());
        pstmt.setBigDecimal(5, item.getTotal());
        
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    private void deleteInvoiceItems(int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoice_items WHERE invoice_id=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, invoiceId);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    public Invoice getInvoiceById(int id) throws SQLException {
        String sql = "SELECT * FROM invoices WHERE id=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        ResultSet rs = pstmt.executeQuery();
        Invoice invoice = null;
        
        if (rs.next()) {
            invoice = mapResultSetToInvoice(rs);
            // CRITICAL: Always load items
            invoice.setItems(getInvoiceItems(id));
        }
        
        pstmt.close();
        return invoice;
    }
    public Invoice getInvoiceByNumber(String invoiceNumber) throws SQLException {
    String sql = "SELECT * FROM invoices WHERE invoice_number=?";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, invoiceNumber);
    
    ResultSet rs = pstmt.executeQuery();
    Invoice invoice = null;
    
    if (rs.next()) {
        invoice = mapResultSetToInvoice(rs);
        // CRITICAL: Always load items
        int invoiceId = rs.getInt("id");
        invoice.setItems(getInvoiceItems(invoiceId));
        
        System.out.println("DEBUG: Loaded invoice by number " + invoiceNumber + 
                         " with " + invoice.getItems().size() + " items");
    }
    
    rs.close();
    pstmt.close();
    return invoice;
}
    
    
    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY invoice_date DESC";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            Invoice invoice = mapResultSetToInvoice(rs);
            
            // CRITICAL FIX: Load items for EACH invoice
            int invoiceId = rs.getInt("id");
            List<InvoiceItem> items = getInvoiceItems(invoiceId);
            invoice.setItems(items);
            
            System.out.println("DEBUG: Loaded invoice " + invoice.getInvoiceNumber() + 
                             " with " + items.size() + " items");
            
            invoices.add(invoice);
        }
        
        stmt.close();
        
        System.out.println("DEBUG: Total invoices loaded: " + invoices.size());
        
        return invoices;
    }
    
    
    private List<InvoiceItem> getInvoiceItems(int invoiceId) throws SQLException {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT * FROM invoice_items WHERE invoice_id=? ORDER BY id";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, invoiceId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            InvoiceItem item = new InvoiceItem();
            item.setId(rs.getInt("id"));
            item.setInvoiceId(rs.getInt("invoice_id"));
            item.setDescription(rs.getString("description"));
            item.setQuantity(rs.getBigDecimal("quantity"));
            item.setUnitPrice(rs.getBigDecimal("unit_price"));
            item.setTotal(rs.getBigDecimal("total"));
            
            items.add(item);
        }
        
        pstmt.close();
        
        System.out.println("DEBUG: Loaded " + items.size() + " items for invoice ID " + invoiceId);
        
        return items;
    }
    
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setCustomerName(rs.getString("customer_name"));
        invoice.setCustomerEmail(rs.getString("customer_email"));
        invoice.setCustomerAddress(rs.getString("customer_address"));
        invoice.setInvoiceDate(rs.getDate("invoice_date").toLocalDate());
        invoice.setDueDate(rs.getDate("due_date").toLocalDate());
        invoice.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status")));
        invoice.setSubtotal(rs.getBigDecimal("subtotal"));
        invoice.setTax(rs.getBigDecimal("tax"));
        invoice.setTotal(rs.getBigDecimal("total"));
        invoice.setNotes(rs.getString("notes"));
        return invoice;
    }
    
    public void deleteInvoice(int id) throws SQLException {
        String sql = "DELETE FROM invoices WHERE id=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    public String generateInvoiceNumber() throws SQLException {
        String sql = "SELECT invoice_number FROM invoices ORDER BY id DESC LIMIT 1";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        int nextNumber = 1;
        if (rs.next()) {
            String lastNumber = rs.getString("invoice_number");
            try {
                String numPart = lastNumber.replaceAll("[^0-9]", "");
                nextNumber = Integer.parseInt(numPart) + 1;
            } catch (Exception e) {
                // Parsing failed, start from 1
            }
        }
        
        stmt.close();
        return String.format("INV-%d-%04d", LocalDate.now().getYear(), nextNumber);
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}