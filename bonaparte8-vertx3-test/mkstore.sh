keytool -genseckey -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg HMacSHA256 -keysize 2048 -alias HS256 -keypass xyzzy5
keytool -genseckey -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg HMacSHA384 -keysize 2048 -alias HS384 -keypass xyzzy5
keytool -genseckey -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg HMacSHA512 -keysize 2048 -alias HS512 -keypass xyzzy5
keytool -genkey -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg RSA -keysize 2048 -alias RS256 -keypass xyzzy5 -sigalg SHA256withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
keytool -genkey -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg RSA -keysize 2048 -alias RS384 -keypass xyzzy5 -sigalg SHA384withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
keytool -genkey -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg RSA -keysize 2048 -alias RS512 -keypass xyzzy5 -sigalg SHA512withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
keytool -genkeypair -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg EC -keysize 256 -alias ES256 -keypass xyzzy5 -sigalg SHA256withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
keytool -genkeypair -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg EC -keysize 256 -alias ES384 -keypass xyzzy5 -sigalg SHA384withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
keytool -genkeypair -keystore mykeystore.jceks -storetype jceks -storepass xyzzy5 -keyalg EC -keysize 256 -alias ES512 -keypass xyzzy5 -sigalg SHA512withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
