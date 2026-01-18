#!/bin/bash

# Invoice2X - Build Windows installer from Linux
# Uses jpackage + Wine + WiX Toolset

echo "================================================"
echo "Invoice2X - Cross-compile Windows Installer"
echo "Building .exe from Linux"
echo "================================================"
echo ""

# Check if running on Linux
if [[ "$OSTYPE" != "linux-gnu"* ]]; then
    echo "ERROR: This script must run on Linux"
    exit 1
fi

echo "Step 1: Installing prerequisites..."
echo ""

# Install Wine (to run Windows tools)
if ! command -v wine &> /dev/null; then
    echo "Installing Wine..."
    sudo dpkg --add-architecture i386
    sudo apt update
    sudo apt install -y wine wine32 wine64 winetricks
    
    # Configure Wine
    winecfg
    
    echo "✓ Wine installed"
else
    echo "✓ Wine already installed"
fi

# Install WiX Toolset in Wine
WIX_DIR="$HOME/.wine/drive_c/wix311"
if [ ! -d "$WIX_DIR" ]; then
    echo ""
    echo "Downloading WiX Toolset..."
    
    mkdir -p /tmp/wix-download
    cd /tmp/wix-download
    
    # Download WiX binaries
    wget https://github.com/wixtoolset/wix3/releases/download/wix3112rtm/wix311-binaries.zip
    
    # Extract to Wine C: drive
    mkdir -p "$WIX_DIR"
    unzip -q wix311-binaries.zip -d "$WIX_DIR"
    
    echo "✓ WiX Toolset installed to Wine"
    cd -
else
    echo "✓ WiX Toolset already installed"
fi

# Set WiX path for jpackage
export WIX="$WIX_DIR"

echo ""
echo "Step 2: Building Java application..."
echo ""

# Build with Maven
mvn clean package

if [ $? -ne 0 ]; then
    echo "ERROR: Maven build failed!"
    exit 1
fi

echo "✓ Application built"
echo ""

# Prepare installer files
echo "Step 3: Preparing installer files..."
mkdir -p installer/input
mkdir -p installer/output

# Copy JAR
cp target/invoice2x-simple-pro-1.0.0-jar-with-dependencies.jar installer/input/invoice2x.jar

# Check for Windows icon
if [ ! -f "resources/icons/invoice2x-icon.ico" ]; then
    echo "WARNING: Windows icon not found"
    echo "Creating placeholder..."
    
    mkdir -p resources/icons
    
    # Convert PNG to ICO if available
    if [ -f "resources/icons/invoice2x-icon.png" ]; then
        if command -v convert &> /dev/null; then
            convert resources/icons/invoice2x-icon.png \
                    -define icon:auto-resize=256,128,64,48,32,16 \
                    resources/icons/invoice2x-icon.ico
            echo "✓ Icon converted"
        fi
    fi
fi

echo "✓ Files prepared"
echo ""

# Build Windows installer
echo "Step 4: Building Windows .exe installer..."
echo "This may take 5-10 minutes..."
echo ""

jpackage \
    --input installer/input \
    --name "Invoice2X" \
    --main-jar invoice2x.jar \
    --main-class com.invoice2x.Main \
    --type exe \
    --dest installer/output \
    --app-version 1.0.0 \
    --vendor "Invoice2X" \
    --description "Professional invoice management with Excel export" \
    --icon resources/icons/invoice2x-icon.ico \
    --win-dir-chooser \
    --win-menu \
    --win-menu-group "Invoice2X" \
    --win-shortcut \
    --win-shortcut-prompt \
    --java-options "-Xms256m" \
    --java-options "-Xmx1024m" \
    --verbose

if [ $? -eq 0 ]; then
    echo ""
    echo "================================================"
    echo "✓ SUCCESS! Windows installer created!"
    echo "================================================"
    echo ""
    echo "Installer location:"
    ls -lh installer/output/*.exe
    echo ""
    echo "You can now:"
    echo "1. Copy installer/output/Invoice2X-1.0.0.exe to Windows"
    echo "2. Test it on a Windows machine"
    echo "3. Distribute to users"
    echo ""
else
    echo ""
    echo "================================================"
    echo "❌ Build failed"
    echo "================================================"
    echo ""
    echo "Common issues:"
    echo "1. Wine not configured properly - run: winecfg"
    echo "2. WiX not found - check: $WIX_DIR"
    echo "3. Java 17+ required - check: java -version"
    echo ""
    echo "Try Method 2 or Method 3 instead (see guide)"
fi
