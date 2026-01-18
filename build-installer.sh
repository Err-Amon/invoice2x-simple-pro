#!/bin/bash

# Invoice2X Simple Pro - Installer Build Script for Linux
# Uses existing Maven build JAR

echo "================================================"
echo "Invoice2X Simple Pro - Installer Builder"
echo "================================================"
echo ""

# Check Java version (need 17+ for jpackage)
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java 17 or higher required for jpackage"
    echo "Current version: $JAVA_VERSION"
    echo "Install Java 17: sudo apt install openjdk-17-jdk"
    exit 1
fi

echo "✓ Java version OK: $JAVA_VERSION"
echo ""

# Skip Maven build, use existing JAR
JAR_PATH="target/invoice2x-simple-pro-1.0.0-jar-with-dependencies.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo "ERROR: JAR not found at $JAR_PATH"
    echo "Please build the project first: mvn clean package"
    exit 1
fi

echo "✓ Using existing JAR: $JAR_PATH"
echo ""

# Create installer directories
mkdir -p installer/input
mkdir -p installer/output

# Copy existing JAR to installer input
cp "$JAR_PATH" installer/input/invoice2x.jar

# Check for icon
if [ ! -f "resources/icons/invoice2x-icon.png" ]; then
    echo "WARNING: Icon file not found at resources/icons/invoice2x-icon.png"
    echo "Creating placeholder icon..."
    mkdir -p resources/icons
    
    if command -v convert &> /dev/null; then
        convert -size 256x256 xc:#6366F1 \
                -gravity center \
                -pointsize 100 \
                -fill white \
                -annotate +0+0 'i2X' \
                resources/icons/invoice2x-icon.png
        echo "✓ Placeholder icon created"
    else
        echo "Install ImageMagick to auto-create icon: sudo apt install imagemagick"
        echo "Or create resources/icons/invoice2x-icon.png manually"
        exit 1
    fi
fi

echo "✓ Files prepared!"
echo ""

# Build DEB package
echo "Step 1: Building .deb installer (Ubuntu/Debian)..."
jpackage \
    --input installer/input \
    --name "Invoice2X" \
    --main-jar invoice2x.jar \
    --main-class com.invoice2x.Main \
    --type deb \
    --dest installer/output \
    --app-version 1.0.0 \
    --vendor "Invoice2X" \
    --description "Professional invoice management with Excel export" \
    --icon resources/icons/invoice2x-icon.png \
    --linux-shortcut \
    --linux-menu-group "Office" \
    --java-options "-Xms256m" \
    --java-options "-Xmx1024m"

if [ $? -eq 0 ]; then
    echo "✓ DEB package created!"
else
    echo "WARNING: DEB package creation failed"
fi

echo ""

# Build AppImage
echo "Step 2: Creating AppImage (Universal Linux)..."

mkdir -p installer/appimage/Invoice2X.AppDir/usr/bin
mkdir -p installer/appimage/Invoice2X.AppDir/usr/share/applications
mkdir -p installer/appimage/Invoice2X.AppDir/usr/share/icons/hicolor/256x256/apps

cp installer/input/invoice2x.jar installer/appimage/Invoice2X.AppDir/usr/bin/
cp resources/icons/invoice2x-icon.png installer/appimage/Invoice2X.AppDir/usr/share/icons/hicolor/256x256/apps/invoice2x.png
cp resources/icons/invoice2x-icon.png installer/appimage/Invoice2X.AppDir/invoice2x.png

cat > installer/appimage/Invoice2X.AppDir/usr/bin/invoice2x << 'EOF'
#!/bin/bash
APPDIR="$(dirname "$(readlink -f "$0")")/../.."
java -Xms256m -Xmx1024m -jar "$APPDIR/usr/bin/invoice2x.jar"
EOF
chmod +x installer/appimage/Invoice2X.AppDir/usr/bin/invoice2x

cat > installer/appimage/Invoice2X.AppDir/usr/share/applications/invoice2x.desktop << EOF
[Desktop Entry]
Type=Application
Name=Invoice2X Simple Pro
Comment=Professional invoice management
Exec=invoice2x
Icon=invoice2x
Categories=Office;Finance;
Terminal=false
EOF

cat > installer/appimage/Invoice2X.AppDir/AppRun << 'EOF'
#!/bin/bash
APPDIR="$(dirname "$(readlink -f "$0")")"
java -Xms256m -Xmx1024m -jar "$APPDIR/usr/bin/invoice2x.jar"
EOF
chmod +x installer/appimage/Invoice2X.AppDir/AppRun

if [ ! -f "installer/appimagetool-x86_64.AppImage" ]; then
    echo "Downloading appimagetool..."
    wget -O installer/appimagetool-x86_64.AppImage \
        "https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
    chmod +x installer/appimagetool-x86_64.AppImage
fi

ARCH=x86_64 installer/appimagetool-x86_64.AppImage installer/appimage/Invoice2X.AppDir installer/output/Invoice2X-1.0.0-x86_64.AppImage

if [ $? -eq 0 ]; then
    echo "✓ AppImage created!"
else
    echo "WARNING: AppImage creation failed"
fi

echo ""
echo "================================================"
echo "Build Complete!"
echo "================================================"
echo ""
echo "Installer files created in: installer/output/"
ls -lh installer/output/
echo ""
echo "✓ All done!"
