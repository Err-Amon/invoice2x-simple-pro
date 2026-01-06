package com.invoice2x.model;

import java.math.BigDecimal;


 // Invoice item data model representing a single line item
 
public class InvoiceItem {
    
    private int id;
    private int invoiceId;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
    
    public InvoiceItem() {
        this.quantity = BigDecimal.ONE;
        this.unitPrice = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }
    
    public InvoiceItem(String description, BigDecimal quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotal();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        calculateTotal();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    
    public void calculateTotal() {
        if (quantity != null && unitPrice != null) {
            this.total = quantity.multiply(unitPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.total = BigDecimal.ZERO;
        }
    }
}