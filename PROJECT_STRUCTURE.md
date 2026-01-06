# Invoice2X Simple Pro - Complete Project Structure

## Directory Layout

```
invoice2x-simple-pro/
│
├── src/
│   └── com/
│       └── invoice2x/
│           ├── Main.java                          [✓ PROVIDED]
│           │
│           ├── model/
│           │   ├── Invoice.java                   [✓ PROVIDED]
│           │   └── InvoiceItem.java               [✓ PROVIDED]
│           │
│           ├── service/
│           │   ├── DatabaseService.java           [✓ PROVIDED]
│           │   └── ExcelExportService.java        [✓ PROVIDED]
│           │
│           ├── ui/
│           │   ├── MainFrame.java                 [✓ PROVIDED]
│           │   └── panels/
│           │       ├── DashboardPanel.java        [✓ PROVIDED]
│           │       ├── InvoiceFormPanel.java      [✓ PROVIDED]
│           │       ├── InvoiceListPanel.java      [✓ PROVIDED]
│           │       ├── ExportPanel.java           [✓ PROVIDED]
│           │       └── SettingsPanel.java         [✓ PROVIDED]
│           │
│           └── util/
│               ├── UIConstants.java               [✓ PROVIDED]
│               └── ConfigManager.java             [✓ PROVIDED]
│
├── lib/                                           [CREATE THIS FOLDER]
│   ├── sqlite-jdbc-3.43.0.0.jar                  [DOWNLOAD]
│   ├── poi-5.2.3.jar                             [DOWNLOAD]
│   ├── poi-ooxml-5.2.3.jar                       [DOWNLOAD]
│   ├── poi-ooxml-lite-5.2.3.jar                  [DOWNLOAD]
│   ├── xmlbeans-5.1.1.jar                        [DOWNLOAD]
│   ├── commons-compress-1.21.jar                 [DOWNLOAD]
│   └── commons-collections4-4.4.jar              [DOWNLOAD]
│
├── build/                                         [AUTO-GENERATED]
│   ├── classes/                                   [Compiled .class files]
│   └── MANIFEST.MF                                [Auto-created]
│
├── dist/                                          [AUTO-GENERATED]
│   ├── invoice2x.jar                             [Built application]
│   ├── lib/                                      [Copied dependencies]
│   └── run.sh / run.bat                          [Launch scripts]
│
├── docs/                                          [DOCUMENTATION]
│   ├── README.md                                 [✓ PROVIDED]
│   ├── SETUP_GUIDE.md                            [✓ PROVIDED]
│   └── PROJECT_STRUCTURE.md                      [✓ THIS FILE]
│
├── resources/                                     [CREATE IF NEEDED]
│   └── icons/                                    [Optional app icons]
│       └── app-icon.png                          [64x64 PNG icon]
│
├── pom.xml                                        [✓ PROVIDED - Maven]
├── build.sh                                       [✓ PROVIDED - Linux]
├── build.bat                                      [✓ PROVIDED - Windows]
├── .gitignore                                     [RECOMMENDED]
└── LICENSE                                        [OPTIONAL]
```

---

## File Status Legend

- **[✓ PROVIDED]** - Complete source code provided
- **[DOWNLOAD]** - Must download from external source
- **[CREATE THIS FOLDER]** - Create empty folder
- **[AUTO-GENERATED]** - Created during build process
- **[OPTIONAL]** - Not required but recommended

---

## Step-by-Step Setup Checklist

### 1. Create Project Structure

```bash
# Create main project directory
mkdir invoice2x-simple-pro
cd invoice2x-simple-pro

# Create source directories
mkdir -p src/com/invoice2x/model
mkdir -p src/com/invoice2x/service
mkdir -p src/com/invoice2x/ui/panels
mkdir -p src/com/invoice2x/util

# Create lib directory
mkdir lib

# Create docs directory (optional)
mkdir docs
```

### 2. Copy Source Files

Copy all the provided Java files into their respective directories:

**Main Application:**
- `Main.java` → `src/com/invoice2x/`

**Models:**
- `Invoice.java` → `src/com/invoice2x/model/`
- `InvoiceItem.java` → `src/com/invoice2x/model/`

**Services:**
- `DatabaseService.java` → `src/com/invoice2x/service/`
- `ExcelExportService.java` → `src/com/invoice2x/service/`

**UI:**
- `MainFrame.java` → `src/com/invoice2x/ui/`
- `DashboardPanel.java` → `src/com/invoice2x/ui/panels/`
- `InvoiceFormPanel.java` → `src/com/invoice2x/ui/panels/`
- `InvoiceListPanel.java` → `src/com/invoice2x/ui/panels/`
- `ExportPanel.java` → `src/com/invoice2x/ui/panels/`
- `SettingsPanel.java` → `src/com/invoice2x/ui/panels/`

**Utilities:**
- `UIConstants.java` → `src/com/invoice2x/util/`
- `ConfigManager.java` → `src/com/invoice2x/util/`

**Build Files:**
- `pom.xml` → project root
- `build.sh` → project root
- `build.bat` → project root

**Documentation:**
- `README.md` → `docs/` or project root
- `SETUP_GUIDE.md` → `docs/`

### 3. Download Dependencies

**Option A: Using Maven (Recommended)**
```bash
mvn clean package
```
Maven will automatically download all dependencies.

**Option B: Manual Download**

Download these JARs and place in `lib/` folder:

1. **SQLite JDBC Driver**
   - URL: https://github.com/xerial/sqlite-jdbc/releases
   - File: `sqlite-jdbc-3.43.0.0.jar`
   - Direct: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.43.0.0/

2. **Apache POI**
   - URL: https://poi.apache.org/download.html
   - Download ZIP, extract these files:
     - `poi-5.2.3.jar`
     - `poi-ooxml-5.2.3.jar`
     - `poi-ooxml-lite-5.2.3.jar`

3. **Supporting Libraries**
   - `xmlbeans-5.1.1.jar` (from POI package)
   - `commons-compress-1.21.jar` (from POI package)
   - `commons-collections4-4.4.jar` (from POI package)

### 4. Build the Application

**Using Maven:**
```bash
mvn clean package
```
Output: `target/invoice2x-simple-pro-1.0.0.jar`

### 5. Run the Application

**From Maven build:**
```bash
cd target
java -jar invoice2x-simple-pro-1.0.0-jar-with-dependencies.jar
```


## Build Output Files

After successful build, you'll have:

```
dist/  (or target/ with Maven)
├── invoice2x.jar                    # Main application JAR
├── lib/                             # All dependencies
│   ├── sqlite-jdbc-3.43.0.0.jar
│   ├── poi-5.2.3.jar
│   └── ... (other JARs)
└── run.sh / run.bat                 # Convenience launchers
```

---

## Runtime Generated Files

When you run the application, it will create:

```
invoice2x.db                         # SQLite database file
invoice2x.properties                 # Configuration file
```

These files are created in the same directory where you run the application.

---

## Recommended .gitignore

Create a `.gitignore` file with:

```gitignore
# Build outputs
build/
dist/
target/

# IDE files
.idea/
.vscode/
*.iml
.classpath
.project
.settings/

# OS files
.DS_Store
Thumbs.db

# Runtime files
invoice2x.db
invoice2x.properties

# Backup files
*.db.backup
*.bak

# Logs
*.log
```

---


## Verification Checklist

Before running, verify:

- [ ] All 13 Java source files copied to correct locations
- [ ] All 7 JAR dependencies in `lib/` folder
- [ ] `pom.xml` in project root (if using Maven)
- [ ] Build script (`build.sh` or `build.bat`) in project root
- [ ] Java 11+ installed and in PATH
- [ ] Write permissions in project directory

---

## Quick Start Commands

### Full Setup (Maven)
```bash
# Clone/extract project
cd invoice2x-simple-pro

# Build with Maven (downloads dependencies automatically)
mvn clean package

# Run
java -jar target/invoice2x-simple-pro-1.0.0-jar-with-dependencies.jar
```

### Full Setup (Manual)
```bash
# Create structure
mkdir -p src/com/invoice2x/{model,service,ui/panels,util}
mkdir lib

# Copy source files to respective directories
# Download JARs to lib/


## Troubleshooting Common Issues

**Issue: "package does not exist"**
- Solution: Check all Java files are in correct directories
- Verify package declarations match folder structure

**Issue: "Cannot find symbol"**
- Solution: All 13 source files must be present
- Check for typos in class names

**Issue: "ClassNotFoundException"**
- Solution: All JARs must be in `lib/` or Maven must download them
- Check JAR file names match exactly

**Issue: Build succeeds but app won't run**
- Solution: Check Java version: `java -version` (must be 11+)
- Verify all dependencies copied to output directory

---

## Next Steps

1. ✅ Set up project structure
2. ✅ Copy all source files
3. ✅ Download dependencies
4. ✅ Build the application
5. ✅ Run and test
6. 📝 Read README.md for usage
7. 📝 Follow SETUP_GUIDE.md for configuration

