# CryptageVote



## Description

Classes permettant le crypage de messages afin d'envoyer des votes sécurisés.

Le projet est composé de 2 packages :

- Concept : contient les classes de fonctionnement du cryptage
- Cryptage : permet une utilisation simplifiée du modèle de cryptage


## Package Concept

Le Package Concept contient les classes suivantes:

- Agrege : permet l'agrégation de deux messages cryptés
- Decrypt : décrypte un message crypté 
- Encrypt : encrypte un message clair
- KeyGen : génère les clés pour crypter, décrypter et agréger

Ce Package permet de crypter un message, de l'agrégé et de le décrypter.

**KeyGen**

La classe KeyGen permet de générer les clés pour crypter, décrypter et agréger.
```java
//Générer une clé
KeyGen keyGen = new KeyGen(int taille);

//Obtenir les clés
HashMap<String, BigInteger> PublicKey = keyGen.getPublicKey();
BigInteger PrivateKey = keyGen.getPrivateKey();
```


**Encrypt**

La classe Encrypt permet de crypter un message.
```java
//Crypter un message
Encrypt encrypt = new Encrypt(String message, HashMap<String, BigInteger> PublicKey);

//Obtenir le message crypté
BigInteger messageCrypte = encrypt.getMessageCrypte();
```


**Decrypt**

La classe Decrypt permet de décrypter un message.
```java
//Décrypter un message
Decrypt decrypt = new Decrypt(BigInteger messageCrypte, BigInteger PrivateKey);

//Obtenir le message décrypté
BigInteger messageDecrypte = decrypt.getMessageDecrypte();
```

**Agrege**

La classe Agrege permet d'agréger deux messages cryptés.
```java
//Agréger deux messages cryptés
Agrege agrege = new Agrege(BigInteger messageCrypte1, BigInteger messageCrypte2, HashMap<String, BigInteger> PublicKey);

//Obtenir le message agrégé
BigInteger messageAgrege = agrege.getMessageAgrege();
```


## Package Cryptage

Le Package Cryptage contient les classes suivantes:

- KeyGenCryptage: permet la génération des clés pour crypter, décrypter et agréger
- MessageCryptage: permet l'encryptage, le décryptage et l'agrégation de messages
- PrivateKey: permet de stocker la clé privée
- PublicKey: permet de stocker la clé publique

Le Package Cryptage permet une utilisation simplifiée du modèle de cryptage, 
il permet de générer les clés, d'encrypter, décrypter et agréger des messages.
En effet, après avoir généré une paire de clé (Publique et Privée), on peut utiliser uniquement des 
MessageCrypte pour manipuler les classes précédentes (Encrypt, Decrypt, Agrege).

**KeyGenCryptage**

La classe KeyGenCryptage permet de générer les clés pour crypter, décrypter et agréger.
```java
//Générer une clé
KeyGenCryptage keyGenCryptage = new KeyGenCryptage(int taille);

//Obtenir les clés
PublicKey publicKey = keyGenCryptage.getPublicKey();
PrivateKey privateKey = keyGenCryptage.getPrivateKey();
```

**MessageCryptage**

La classe MessageCryptage permet de crypter, décrypter et agréger des messages.
```java
//Crypter un message
MessageCryptage messageCryptage = new MessageCryptage(int message, PublicKey publicKey);

//Decrypter le message
messageCryptage.decrypt(PrivateKey privateKey, PublicKey publicKey);
```

Agrégation de deux messages:
```java
//Agréger deux messages cryptés
MessageCryptage messageCryptage1 = new MessageCryptage(int message1, PublicKey publicKey);
MessageCryptage messageCryptage2 = new MessageCryptage(int message2, PublicKey publicKey);

messageCryptage1.agregate(messageCryptage2, publicKey);
```

## PrivateKey et PublicKey

Les classes PrivateKey et PublicKey permettent de stocker des clés privées et publiques.





