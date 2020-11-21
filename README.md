# SunPKCS11Wrapper

Starting from OpenJDK 9, the `sun.security.pkcs11.SunPKCS11` class no longer provides the constructor accepting a configuration file, and instead, a new `configure()` method is added to the `java.security.Provider` class to support the same functionality. However, the `apksigner` tool from Android SDK doesn't support this new interface and will crash when trying to instantiate the `SunPKCS11` class.

This project provides a wrapper class with the old constructor interface, making `apksigner` compatible with OpenJDK 9 and later releases.

## Usage

1. [Download `SunPKCS11Wrapper.jar`](https://ddosolitary-builds.sourceforge.io/SunPKCS11Wrapper.jar) and copy it to `<sdk-path>/build-tools/<version>/lib/`.

2. **Linux**:  
   In the last line of the file `<sdk-path>/build-tools/<version>/apksigner` (which should be an `exec` command), replace `-jar "$jarpath"` with `-cp "$jarpath:$libdir/SunPKCS11Wrapper.jar" com.android.apksigner.ApkSignerTool`.

   **Windows**:  
   In the last line of the file `<sdk-path>/build-tools/<version>/apksigner.bat` (which should be a `call` command), replace `-jar "%jarpath%"` with `-cp "%jarpath%;%frameworkdir%\SunPKCS11Wrapper.jar" com.android.apksigner.ApkSignerTool`.

3. Run apksigner as usual, but pass `org.ddosolitary.pkcs11.SunPKCS11Wrapper` instead of `sun.security.pkcs11.SunPKCS11` to the `--provider-class` option.  
   For example:  
   ```bash
   apksigner sign \
       --ks NONE \
       --ks-key-alias <YOUR_KEY_ALIAS> \
       --ks-type PKCS11
       --provider-class org.ddosolitary.pkcs11.SunPKCS11Wrapper \
       --provider-arg <PATH_TO_CONFIG_FILE> \
       <PATH_TO_APK_FILE>
   ```

## See also

http://mail.openjdk.java.net/pipermail/jep-changes/2015-November/000219.html

https://issuetracker.google.com/issues/132333137
