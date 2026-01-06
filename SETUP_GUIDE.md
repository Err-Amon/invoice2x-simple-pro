# Invoice2X Simple Pro - Complete Setup Guide

This guide will walk you through setting up Invoice2X Simple Pro from scratch.

## Table of Contents

1. [System Requirements](#system-requirements)
2. [Installing Java](#installing-java)
3. [Downloading Dependencies](#downloading-dependencies)
4. [Building the Application](#building-the-application)
5. [Running the Application](#running-the-application)
6. [First Time Configuration](#first-time-configuration)
7. [Troubleshooting](#troubleshooting)

---

## System Requirements

### Minimum Requirements
- **Operating System**: Windows 10/11 or Linux (Ubuntu 20.04+, Fedora 35+)
- **RAM**: 4GB
- **Disk Space**: 100MB for application + database
- **Java**: OpenJDK 11 or higher

### Recommended Requirements
- **RAM**: 8GB or more
- **Disk Space**: 500MB (for larger invoice databases)

---

## Installing Java

### Windows

1. Download OpenJDK 11 or higher from [Adoptium](https://adoptium.net/)
2. Run the installer (`.msi` file)
3. During installation, check "Add to PATH" option
4. Verify installation:
   ```cmd
   java -version
   javac -version
   ```
   Both should show version 11 or higher

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

Verify installation:
```bash
java -version
javac -version
```

### Linux (Fedora)

```bash
sudo dnf install java-11-openjdk-devel
```

Verify installation:
```bash
java -version
javac -version
```

---

## Downloading Dependencies

The application requires two main libraries:

### 1. SQLite JDBC Driver

**Download Location**: https://github.com/xerial/sqlite-jdbc/releases

**File to Download**: `sqlite-jdbc-3.43.0.0.jar` (or latest version)

**Where to Place**: `lib/sqlite-jdbc-3.43.0.0.jar`

### 2. Apache POI (for Excel generation)

**Download Location**: https://poi.apache.org/download.html

**Files to Download** (from POI Binary Distribution):
- `poi-5.2.3.jar`
- `poi-ooxml-5.2.3.jar`
- `poi-ooxml-lite-5.2.3.jar`
- `xmlbeans-5.1.1.jar`
- `commons-compress-1.21.jar`
- `commons-collections4-4.4.jar`

**Where to Place**: All files in `lib/` directory

### Directory Structure After Download

```
invoice2x/
├── src/
├── lib/
│   ├── sqlite-jdbc-3.43.0.0.jar
│   ├── poi-5.2.3.jar
│   ├── poi-ooxml-5.2.3.jar
│   ├── poi-ooxml-lite-5.2.3.jar
│   ├── xmlbeans-5.1.1.jar
│   ├── commons-compress-1.21.jar
│   └── commons-collections4-4.4.jar
├── build.sh
├── build.bat
└── README.md
```

### Method 3: With Custom Memory Settings

If you have more RAM and want better performance:

**Windows:**
```cmd
java -Xms512m -Xmx2048m -jar invoice2x.jar
```

**Linux:**
```bash
java -Xms512m -Xmx2048m -jar invoice2x.jar
```

Where:
- `-Xms512m` = Initial memory (512 MB)
- `-Xmx2048m` = Maximum memory (2 GB)

---

## First Time Configuration

### Step 1: Initial Launch

When you first launch the application, you'll see the dashboard with sample data (if included).

### Step 2: Configure Company Settings

1. Click **Settings** in the left navigation
2. Fill in your company information:
   - **Company Name**: Your business name
   - **Tax ID**: Your business tax identification number
   - **Address**: Your business address (will appear on invoices)
3. Set invoice defaults:
   - **Default Tax Rate**: e.g., 10 (for 10%)
   - **Currency Symbol**: e.g., $ or € or £
4. Click **Save**

### Step 3: Create Your First Invoice

1. Click **New Invoice** from the dashboard
2. Fill in customer details:
   - Customer Name (required)
   - Email (optional)
   - Address (optional)
3. Add items:
   - Click **Add Item**
   - Enter description, quantity, and unit price
   - Total calculates automatically
4. Review totals (subtotal, tax, total)
5. Add notes if needed
6. Click **Save**

### Step 4: Test Excel Export

1. Go to **Export Center**
2. Select "Export all invoices"
3. Choose where to save the file
4. Click **Export**
5. Open the file in LibreOffice Calc to verify

---

## Troubleshooting

### Build Errors

**Error: "javac not found"**
- Solution: Install Java JDK (see Installing Java section)
- Make sure `javac` is in your PATH

**Error: "Missing required libraries"**
- Solution: Download and place all JAR files in `lib/` directory
- Verify file names match exactly

**Error: "Compilation failed"**
- Check Java version: `javac -version` (must be 11+)
- Ensure all source files are present
- Check for syntax errors in console output

### Runtime Errors

**Error: "Could not find or load main class"**
- Solution: Make sure you're running from `dist/` directory
- Check that `lib/` folder is present alongside JAR

**Error: "ClassNotFoundException: org.sqlite.JDBC"**
- Solution: SQLite JDBC driver missing
- Place `sqlite-jdbc-3.43.0.0.jar` in `dist/lib/`

**Error: "NoClassDefFoundError: org/apache/poi"**
- Solution: Apache POI libraries missing
- Place all POI JAR files in `dist/lib/`

**Database Error: "Unable to open database"**
- Solution: Check write permissions in application directory
- The app creates `invoice2x.db` on first run

### Performance Issues

**Application is slow**
- Increase memory allocation:
  ```bash
  java -Xms512m -Xmx1024m -jar invoice2x.jar
  ```
- Close other applications
- Compact database (Settings → Compact Database)

**Export is slow**
- Export smaller date ranges
- Close LibreOffice if it's open
- Check available disk space

### LibreOffice Issues

**Excel file won't open in LibreOffice**
- Ensure file has `.xlsx` extension
- Try opening with File → Open instead of double-click
- Check LibreOffice version (7.0+ recommended)

**Formatting looks wrong**
- This is normal - formatting is simplified for compatibility
- Data and calculations should be correct
- Adjust column widths in LibreOffice if needed

---

## Advanced Configuration

### Custom Database Location

By default, the database is created in the application directory. To use a custom location:

```bash
java -Ddb.path=/path/to/invoice2x.db -jar invoice2x.jar
```

### Increase Font Size

For better readability on high-DPI displays, edit the UIConstants.java file before building and increase font sizes.

### Change Theme Colors

Edit UIConstants.java before building to customize colors:
- `PRIMARY_COLOR` - Main accent color
- `ACCENT_COLOR` - Success/positive actions
- `WARNING_COLOR` - Warnings
- `DANGER_COLOR` - Errors/delete actions

---

## Getting Help

If you encounter issues not covered in this guide:

1. Check the main README.md for additional information
2. Review console output for error messages
3. Try with a fresh database (backup and delete `invoice2x.db`)
4. Ensure all dependencies are correct versions

---

## Next Steps

After successful setup:

1. **Create invoices** for your business
2. **Set up regular backups** (Settings → Backup Database)
3. **Test export** to ensure LibreOffice compatibility
4. **Customize settings** to match your business needs

---

## Quick Reference

**Backup Database:**
- Use Settings → Backup Database in the application
- Or manually copy `invoice2x.db`

**Update Application:**
1. Backup your database
2. Rebuild from new source
3. Replace JAR file but keep your database

---

## Success Checklist

- [ ] Java 11+ installed and verified
- [ ] All JAR dependencies downloaded
- [ ] Project built successfully
- [ ] Application launches without errors
- [ ] Company settings configured
- [ ] First invoice created
- [ ] Excel export tested in LibreOffice
- [ ] Database backup created

---

**Congratulations!** You're now ready to use Invoice2X Simple Pro for your invoicing needs.
