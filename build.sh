#!/bin/bash

# Invoice2X Simple Pro Build Script for Linux
# This script compiles the Java source code and creates an executable JAR

echo "================================================"
echo "Invoice2X Simple Pro - Build Script"
echo "================================================"
echo ""

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo "ERROR: Java compiler (javac) not found!"
    echo "Please install Java JDK 11 or higher"
    echo ""
    echo "Ubuntu/Debian:"
    echo "  sudo apt install openjdk-11-jdk"
    echo ""
    echo "Fedora:"
    echo "  sudo dnf install java-11-openjdk-devel"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(javac -version 2>&1 | awk '{print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "ERROR: Java 11 or higher is required"
    echo "Current version: $(javac -version 2>&1)"
    exit 1
fi

echo "Java compiler found: $(javac -version 2>&1)"
echo ""

# Create directories
echo "Creating build directories..."
mkdir -p build/classes
mkdir -p dist
mkdir -p lib

# Check for required libraries
echo ""
echo "Checking for required libraries..."
MISSING_LIBS=0

if [ ! -f "lib/sqlite-jdbc-3.43.0.0.jar" ]; then
    echo "WARNING: sqlite-jdbc-3.43.0.0.jar not found in lib/"
    MISSING_LIBS=1
fi

if [ ! -f "lib/poi-5.2.3.jar" ]; then
    echo "WARNING: Apache POI libraries not found in lib/"
    echo "Required: poi-5.2.3.jar, poi-ooxml-5.2.3.jar, etc."
    MISSING_LIBS=1
fi

if [ $MISSING_LIBS -eq 1 ]; then
    echo ""
    echo "ERROR: Missing required libraries!"
    echo "Please download and place in lib/ directory:"
    echo "  - SQLite JDBC: https://github.com/xerial/sqlite-jdbc/releases"
    echo "  - Apache POI: https://poi.apache.org/download.html"
    exit 1
fi

echo "All libraries found!"
echo ""

# Compile source files
echo "Compiling source files..."
javac -d build/classes -cp "lib/*" \
    src/com/invoice2x/*.java \
    src/com/invoice2x/model/*.java \
    src/com/invoice2x/service/*.java \
    src/com/invoice2x/ui/*.java \
    src/com/invoice2x/ui/panels/*.java \
    src/com/invoice2x/util/*.java

if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed!"
    exit 1
fi

echo "Compilation successful!"
echo ""

# Create manifest file
echo "Creating manifest..."
cat > build/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: com.invoice2x.Main
Class-Path: lib/sqlite-jdbc-3.43.0.0.jar lib/poi-5.2.3.jar lib/poi-ooxml-5.2.3.jar lib/poi-ooxml-lite-5.2.3.jar lib/xmlbeans-5.1.1.jar lib/commons-compress-1.21.jar lib/commons-collections4-4.4.jar
EOF

# Create JAR file
echo "Creating JAR file..."
cd build/classes
jar cfm ../../dist/invoice2x.jar ../MANIFEST.MF com/
cd ../..

if [ $? -ne 0 ]; then
    echo "ERROR: JAR creation failed!"
    exit 1
fi

# Copy libraries to dist
echo "Copying libraries..."
cp -r lib dist/

# Create run script
echo "Creating run script..."
cat > dist/run.sh << 'EOF'
#!/bin/bash
java -Xms256m -Xmx1024m -jar invoice2x.jar
EOF
chmod +x dist/run.sh

echo ""
echo "================================================"
echo "Build completed successfully!"
echo "================================================"
echo ""
echo "Output files in: dist/"
echo "  - invoice2x.jar"
echo "  - lib/ (dependencies)"
echo "  - run.sh (launch script)"
echo ""
echo "To run the application:"
echo "  cd dist"
echo "  ./run.sh"
echo ""
echo "Or directly:"
echo "  java -jar dist/invoice2x.jar"
echo ""