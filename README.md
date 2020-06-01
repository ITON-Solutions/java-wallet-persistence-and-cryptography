# Java Wallet Persistence and Cryptography

The present project is aimed at creating a unified bottom-level component for Java Wallet Management (JWM) that can be installed and used in both desktop applications and mobile apps. In the latter case, JWM is expected to be specially useful due to the dominant presence of Android devices in the mobile market.

The lifecycle management of digital assets can be realized through the Java wallet core that enables the persistence and cryptography operations for edge-agent/client-side Android apps or Java applications (external consumers).

Java Wallet Cryptography (JWC): All necessary cryptographic handling is implemented through the JNI method invocations of the well-known Sodium-NaCl library (https://github.com/jedisct1/libsodium). As a dependency, JWC includes the Jni-sodium project (https://github.com/ITON-Solutions/jni-sodium) which is a JNI adaptation of the Sodium-NaCl library for Windows x64 and Android OS. JWC is designed in a developer-friendly manner in order to ensure maximum simplicity while coding external consumers.

Java Wallet Persistence (JWP): Data records and cryptographic material stored in the persistence storage can be queried by external consumers using CRUD and Export/Import operations. The JWP API is implemented using JPA to allow plugging different types of storages: ORM, RDBMS, NoSQL DB, Graph DB, etc. Data model is actually implemented with a simple key-value mapping but can be eventually replaced by more sophisticated schemes. The records stored in a digital wallet are referenced by a public identifier if they are secret meanwhile the non-secret/non-sensitive records can be managed directly through the API query.
