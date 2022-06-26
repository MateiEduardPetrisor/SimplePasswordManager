# SimplePasswordManager(Proof Of Concept)

Changes in V1:
 - Project built on JDK 8
 - All the credentials are encrypted with the same IV
 - Opening the database with notepad++ you can figure out if the same passoword was used for multiple sites(same cipher text)
 - BouncyCastle used is 1.65
 - Jar file is built with Maven shade plugin. This cause a break of signature for BouncyCastle and as a workaround BouncyCastle is excluded from the big jar file.

Changes in V2:
 - BouncyCastle updated to v1.70
 - Maven dependencies updated
 - Jar file is built with Maven jar plugin and Maven dependency plugin(all dependencies are copied in a folder and the jar file contain only the project code and resources)
 - Each credential is now encrypted with different IV to fix the issue from version 1(same cipher text if same passwords were inserted in the database)
 - Credentials are now sorted in GUI
 - Project built on JDK version 17
