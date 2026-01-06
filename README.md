# Invoice2X Simple Pro

Professional invoice management system with LibreOffice-optimized Excel export. Built for low-spec systems (4GB RAM) with offline-first design.

## Features

- ✅ **Professional Invoice Creation** - Easy-to-use forms with real-time calculations
- ✅ **Excel Export** - LibreOffice Calc compatible XLSX files
- ✅ **SQLite Database** - Lightweight, no server required
- ✅ **Offline Operation** - No internet connection needed
- ✅ **Low Memory Footprint** - Optimized for 4GB RAM systems
- ✅ **Cross-Platform** - Works on Windows and Linux

## System Requirements

- **Operating System**: Windows 10/11 or Linux (Ubuntu 20.04+, Fedora 35+, Linux Mint 20+)
- **RAM**: Minimum 4GB
- **Java**: OpenJDK 11 or higher
- **Disk Space**: 100MB for application + database storage

## Installation

### Prerequisites

1. Install Java 11 or higher:

**Windows:**
```bash
# Download and install from: https://adoptium.net/
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

**Linux (Fedora):**
```bash
sudo dnf install java-11-openjdk
```

2. Install SQLite JDBC Driver (included in dependencies)

### Build from Source

1. Clone or download the source code
2. Navigate to the project directory
3. Compile using provided build script

**Windows:**
```bash
build.bat
```

**Linux:**
```bash
chmod +x build.sh
./build.sh
```

## Project Structure

```
invoice2x/
├── src/
│   └── com/
│       └── invoice2x/
│           ├── Main.java
│           ├── model/
│           │   ├── Invoice.java
│           │   └── InvoiceItem.java
│           ├── service/
│           │   ├── DatabaseService.java
│           │   └── ExcelExportService.java
│           ├── ui/
│           │   ├── MainFrame.java
│           │   └── panels/
│           │       ├── DashboardPanel.java
│           │       ├── InvoiceFormPanel.java
│           │       ├── InvoiceListPanel.java
│           │       ├── ExportPanel.java
│           │       └── SettingsPanel.java
│           └── util/
│               ├── UIConstants.java
│               └── ConfigManager.java
├── lib/
│   ├── sqlite-jdbc-3.43.0.0.jar
│   └── poi-5.2.3.jar (Apache POI for Excel)
├── README.md
└── build.sh / build.bat
```

## Dependencies

The application requires the following libraries:

1. **SQLite JDBC** (3.43.0.0 or higher)
   - Download: https://github.com/xerial/sqlite-jdbc/releases
   - File: `sqlite-jdbc-3.43.0.0.jar`

2. **Apache POI** (5.2.3 or higher) - for Excel generation
   - Download: https://poi.apache.org/download.html
   - Required JARs:
     - `poi-5.2.3.jar`
     - `poi-ooxml-5.2.3.jar`
     - `poi-ooxml-lite-5.2.3.jar`
     - `xmlbeans-5.1.1.jar`
     - `commons-compress-1.21.jar`
     - `commons-collections4-4.4.jar`

Place all JAR files in the `lib/` directory.

## Running the Application

### From JAR file:

```bash
java -jar invoice2x.jar
```

### From source:

```bash
java -cp "lib/*:." com.invoice2x.Main
```

**Windows:**
```bash
java -cp "lib/*;." com.invoice2x.Main
```

## Usage Guide

### First Time Setup

1. Launch the application
2. Go to **Settings** from the navigation menu
3. Enter your company information:
   - Company Name
   - Address
   - Tax ID
   - Default Tax Rate
   - Currency Symbol
4. Click **Save**

### Creating an Invoice

1. Click **New Invoice** from the dashboard or navigation
2. Fill in customer details:
   - Customer Name (required)
   - Email
   - Address
3. Add invoice items:
   - Click **Add Item**
   - Enter description, quantity, and unit price
   - Total is calculated automatically
4. Review the totals (subtotal, tax, total)
5. Add notes if needed
6. Click **Save**

### Viewing Invoices

1. Go to **Invoice List**
2. Use the search bar to find specific invoices
3. Use the filter dropdown to filter by status
4. Double-click an invoice to edit it

### Exporting to Excel

1. Go to **Export Center**
2. Choose export option:
   - Export all invoices
   - Export by date range
3. Select output file location
4. Click **Export**
5. The progress bar will show export status
6. Open the folder when complete

### Managing Settings

1. Go to **Settings**
2. Update company information as needed
3. Adjust default tax rate
4. Use **Backup Database** to create backups
5. Use **Compact Database** to optimize storage

## LibreOffice Compatibility

The Excel export is specifically optimized for LibreOffice Calc with:

- Simple cell formatting (no complex styles)
- Standard fonts (Arial, Times New Roman)
- Basic borders and shading
- Currency formatting that LibreOffice recognizes
- No Excel-specific features or macros

## Performance Optimization

The application is designed for 4GB RAM systems with:

- Efficient memory management
- Connection pooling for database
- Streaming Excel export (processes one invoice at a time)
- Lazy loading of invoice lists
- Minimal UI updates

## Database Information

- **Type**: SQLite
- **File**: `invoice2x.db` (created in application directory)
- **Backup**: Use Settings → Backup Database
- **Location**: Same directory as the application

## Troubleshooting

### Application won't start

1. Check Java version: `java -version` (should be 11+)
2. Ensure all JAR files are in `lib/` directory
3. Check for error messages in console

### Database errors

1. Ensure write permissions in application directory
2. Try compacting database (Settings → Compact Database)
3. Restore from backup if corrupted

### Excel export fails

1. Ensure output directory has write permissions
2. Close the output file if it's already open
3. Check disk space availability

### Memory issues

1. Increase Java heap size:
   ```bash
   java -Xms512m -Xmx1024m -jar invoice2x.jar
   ```
2. Close other applications
3. Export invoices in smaller batches

## License

This is open-source software. Use freely for personal or commercial purposes.

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review the usage guide
3. Check application logs in console

## Version History

**Version 1.0** (Initial Release)
- Invoice creation and management
- Excel export with LibreOffice optimization
- SQLite database storage
- Cross-platform support (Windows/Linux)
- Low-memory optimization

## Credits

- Built with Java Swing
- Uses Apache POI for Excel generation
- Uses SQLite for data storage
- Optimized for low-spec systems
