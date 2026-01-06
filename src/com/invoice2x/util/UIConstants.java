package com.invoice2x.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class UIConstants {
    
    // CLEAN COLOR PALETTE - Carefully chosen for readability
    public static final Color PRIMARY_COLOR = new Color(99, 102, 241);       // Indigo
    public static final Color PRIMARY_DARK = new Color(79, 70, 229);         // Deep Indigo
    public static final Color PRIMARY_LIGHT = new Color(224, 231, 255);      // Very Light Indigo
    
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);       // Emerald
    public static final Color SUCCESS_DARK = new Color(5, 150, 105);         // Dark Emerald
    public static final Color WARNING_COLOR = new Color(251, 146, 60);       // Orange
    public static final Color DANGER_COLOR = new Color(239, 68, 68);         // Red
    
    // BACKGROUND COLORS - All light and clean
    public static final Color BG_WHITE = new Color(255, 255, 255);           // Pure White
    public static final Color BG_LIGHT = new Color(249, 250, 251);           // Very Light Gray
    public static final Color BG_CARD = new Color(255, 255, 255);            // White Cards
    public static final Color SIDEBAR_BG = new Color(248, 250, 252);         // Light Sidebar
    public static final Color HOVER_BG = new Color(241, 245, 249);           // Light Hover
    
    // TEXT COLORS - High contrast, always readable
    public static final Color TEXT_DARK = new Color(30, 41, 59);             // Dark Gray (readable)
    public static final Color TEXT_MEDIUM = new Color(100, 116, 139);        // Medium Gray
    public static final Color TEXT_LIGHT = new Color(148, 163, 184);         // Light Gray
    public static final Color TEXT_WHITE = new Color(255, 255, 255);         // White

    // Backwards-compatible aliases for older panel code
    public static final Color TEXT_PRIMARY = TEXT_DARK;
    
    // BORDER COLORS
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);
    public static final Color BORDER_MEDIUM = new Color(203, 213, 225);
    public static final Color BORDER_COLOR = BORDER_LIGHT;
    
    // FONTS - Clean and professional
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    
    // SPACING
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    
    // SIZES
    public static final Dimension BUTTON_SIZE = new Dimension(140, 40);
    public static final Dimension BUTTON_LARGE = new Dimension(180, 46);
    public static final Dimension INPUT_SIZE = new Dimension(300, 38);

    // Additional background alias used across panels
    public static final Color BG_SECONDARY = BG_LIGHT;
    
    
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
   
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SUCCESS_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SUCCESS_COLOR);
            }
        });
        
        return button;
    }
    
   
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BG_WHITE);
        button.setForeground(TEXT_DARK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER_MEDIUM, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_LIGHT);
                button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_WHITE);
                button.setBorder(BorderFactory.createLineBorder(BORDER_MEDIUM, 1));
            }
        });
        
        return button;
    }
    
    
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(DANGER_COLOR);
        button.setForeground(TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 38, 38));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DANGER_COLOR);
            }
        });
        
        return button;
    }
    
   
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(BODY_FONT);
        textField.setForeground(TEXT_DARK);  
        textField.setBackground(BG_WHITE);   
        textField.setCaretColor(TEXT_DARK);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(INPUT_SIZE);
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return textField;
    }
    
    
    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        textArea.setFont(BODY_FONT);
        textArea.setForeground(TEXT_DARK);    
        textArea.setBackground(BG_WHITE);      
        textArea.setCaretColor(TEXT_DARK);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textArea;
    }
    
   
    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, 
                                           PADDING_LARGE, PADDING_LARGE)
        ));
        return panel;
    }
    
   
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);  
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, 
                                           PADDING_LARGE, PADDING_LARGE)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK); 
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, PADDING_MEDIUM)));
        
        return panel;
    }
    
    
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_DARK);  
        return label;
    }
}