# Setting up securing connection via HTTPS

# Generate certificate
`keytool` is located at JDK/bin folder
```
keytool -genkeypair -alias accountant_service -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

# Save certificate
Move generated certificate to the `resources/keystore/account.p12`
