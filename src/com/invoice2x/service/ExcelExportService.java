package com.invoice2x.service;

import com.invoice2x.model.Invoice;
import com.invoice2x.model.InvoiceItem;
import com.invoice2x.util.ConfigManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class ExcelExportService {
    
    private static ExcelExportService instance;
    
    private ExcelExportService() {
    }
    
    public static synchronized ExcelExportService getInstance() {
        if (instance == null) {
            instance = new ExcelExportService();
        }
        return instance;
    }
    
    
    public void exportInvoices(List<Invoice> invoices, String filePath, 
                               ExportProgressListener listener) throws Exception {
        
        Workbook workbook = new XSSFWorkbook();
        
        try {
            int current = 0;
            for (Invoice invoice : invoices) {
                // BUG FIX: Create unique sheet for EACH invoice
                createInvoiceSheet(workbook, invoice, current + 1);
                
                current++;
                if (listener != null) {
                    listener.onProgress(current, invoices.size(), 
                        "Processing invoice " + current + " of " + invoices.size() + 
                        " (" + invoice.getInvoiceNumber() + ")");
                }
            }
            
            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
            
            if (listener != null) {
                listener.onComplete(filePath);
            }
            
        } finally {
            workbook.close();
        }
    }
    
   
    private void createInvoiceSheet(Workbook workbook, Invoice invoice, int sheetNumber) {
        
        // Create unique sheet name for each invoice
        String sheetName = sanitizeSheetName(invoice.getInvoiceNumber());
        
        // If sheet name already exists, append number
        if (workbook.getSheet(sheetName) != null) {
            sheetName = sanitizeSheetName(invoice.getInvoiceNumber() + "_" + sheetNumber);
        }
        
        Sheet sheet = workbook.createSheet(sheetName);
        
        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle tableHeaderStyle = createTableHeaderStyle(workbook);
        CellStyle tableCellStyle = createTableCellStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("INVOICE");
        titleCell.setCellStyle(headerStyle);
        
        rowNum++; // Empty row
        
        // Invoice details
        createRow(sheet, rowNum++, "Invoice #:", invoice.getInvoiceNumber(), boldStyle);
        createRow(sheet, rowNum++, "Date:", invoice.getInvoiceDate().toString(), boldStyle);
        createRow(sheet, rowNum++, "Due Date:", invoice.getDueDate().toString(), boldStyle);
        createRow(sheet, rowNum++, "Status:", invoice.getStatus().getDisplayName(), boldStyle);
        
        rowNum++; // Empty row
        
        // Company info
        ConfigManager config = ConfigManager.getInstance();
        createRow(sheet, rowNum++, "From:", config.getProperty("company.name", "Your Company"), boldStyle);
        createRow(sheet, rowNum++, "", config.getProperty("company.address", ""), null);
        
        rowNum++; // Empty row
        
        // Customer info
        createRow(sheet, rowNum++, "To:", invoice.getCustomerName(), boldStyle);
        if (invoice.getCustomerEmail() != null && !invoice.getCustomerEmail().isEmpty()) {
            createRow(sheet, rowNum++, "", invoice.getCustomerEmail(), null);
        }
        if (invoice.getCustomerAddress() != null && !invoice.getCustomerAddress().isEmpty()) {
            createRow(sheet, rowNum++, "", invoice.getCustomerAddress(), null);
        }
        
        rowNum++; // Empty row
        
        // Items table header
        Row tableHeaderRow = sheet.createRow(rowNum++);
        String[] headers = {"#", "Description", "Quantity", "Unit Price", "Total"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = tableHeaderRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(tableHeaderStyle);
        }
        
       
        int itemNum = 1;
        for (InvoiceItem item : invoice.getItems()) {
            Row itemRow = sheet.createRow(rowNum++);
            
            Cell numCell = itemRow.createCell(0);
            numCell.setCellValue(itemNum++);
            numCell.setCellStyle(tableCellStyle);
            
            Cell descCell = itemRow.createCell(1);
            descCell.setCellValue(item.getDescription());
            descCell.setCellStyle(tableCellStyle);
            
            Cell qtyCell = itemRow.createCell(2);
            qtyCell.setCellValue(item.getQuantity().doubleValue());
            qtyCell.setCellStyle(tableCellStyle);
            
            Cell priceCell = itemRow.createCell(3);
            priceCell.setCellValue(item.getUnitPrice().doubleValue());
            priceCell.setCellStyle(currencyStyle);
            
            Cell totalCell = itemRow.createCell(4);
            totalCell.setCellValue(item.getTotal().doubleValue());
            totalCell.setCellStyle(currencyStyle);
        }
        
        rowNum++; // Empty row
        
        
        createTotalRow(sheet, rowNum++, "Subtotal:", invoice.getSubtotal(), boldStyle, currencyStyle);
        createTotalRow(sheet, rowNum++, "Tax:", invoice.getTax(), boldStyle, currencyStyle);
        createTotalRow(sheet, rowNum++, "TOTAL:", invoice.getTotal(), totalStyle, totalStyle);
        
        rowNum++; // Empty row
        
        // Notes
        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            createRow(sheet, rowNum++, "Notes:", invoice.getNotes(), boldStyle);
        }
        
        // Set column widths
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3500);
        sheet.setColumnWidth(4, 3500);
    }
    
    private void createRow(Sheet sheet, int rowNum, String label, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        if (style != null) {
            labelCell.setCellStyle(style);
        }
        
        if (value != null) {
            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(value);
        }
    }
    
    private void createTotalRow(Sheet sheet, int rowNum, String label, BigDecimal amount,
                               CellStyle labelStyle, CellStyle amountStyle) {
        Row row = sheet.createRow(rowNum);
        
        Cell labelCell = row.createCell(3);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        
        Cell amountCell = row.createCell(4);
        amountCell.setCellValue(amount.doubleValue());
        amountCell.setCellStyle(amountStyle);
    }
    
    // Style creation methods
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        style.setFont(font);
        return style;
    }
    
    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        return style;
    }
    
    private CellStyle createTableHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createTableCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        style.setFont(font);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        return style;
    }
    
    
    private String sanitizeSheetName(String name) {
        // Excel sheet names can't contain: / \ ? * [ ]
        String sanitized = name.replaceAll("[/\\\\?*\\[\\]]", "-");
        // Limit to 31 characters
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }
        return sanitized;
    }
    
   
    public interface ExportProgressListener {
        void onProgress(int current, int total, String message);
        void onComplete(String filePath);
        void onError(Exception e);
    }
}