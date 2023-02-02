# Referendum Online Server

Ce programme est le serveur du système de referendums en ligne. Il gère la connexion et l'authentification des utilisateurs, la gestion des référendums et le traitement des votes.

## Prérequis

Pour que ce programme fonctionne, vous devez avoir installé la librairie de chiffrement. De plus, le scrutateur doit être lancé.

## Package server

Ce package contient les classes qui correspondent à toute la logique du serveur. Une fois le serveur démarré, 
il est possible de se connecter au serveur qui va gérer les requêtes des clients. Afin de pouvoir gérer les requêtes, chaque Handler gère des actions spécifiques
les flux d'entrée sorties sont donc redirigés sur chaque handlers ou est traité la requete du  client.

### Systeme
Cette classe est la classe principale du programme. Elle lance le serveur, accepte
les clients et les envoie vers la classe LoginHandler. Elle possède également une
liste de référendums actuels.

### ChangePwdHandler
Cette classe hérite de la classe `Thread` et permet à l'utilisateur de changer son mot de passe
une fois qu'il a reçu son code par mail. Elle possède une méthode `run()` qui gère la logique de
changement de mot de passe.

### ClientHandler
Cette classe hérite également de la classe `Thread` et gère le client une fois connecté. Si le
client est un administrateur, il peut ajouter ou supprimer des référendums. Si c'est un client,
il peut choisir un vote qui le redirigera vers la classe `VoteHandler`. 
Elle possède une méthode `run()` qui gère la logique de gestion du client.

### ForgottenPwdHandler
Cette classe hérite également de la classe `Thread` et permet à l'utilisateur de recevoir 
un code par mail s'il a oublié son mot de passe. Elle possède une méthode `run()` qui gère
l'envoi du code par mail.

### LoginHandler
Cette classe gère le login de l'utilisateur et le redirige vers la classe ClientHandler
si le login est correct. Elle possède une méthode `run()` qui gère la logique de login.

### VoteHandler
Cette classe gère les choix de l'utilisateur une fois qu'il a choisi pour quel 
référendum il souhaite voter. Elle possède une méthode `run()` qui gère 
la logique de vote.



## Package dependencies

Ce package contient les classes suivantes permettant le bon fonctionnement du serveur:

- **ConnexionBD**: Singleton pour la connexion à la base de données.
- **ConnexionScrutateur**: Connexion au scrutin qui gère les clés de chiffrement (génère une clé publique pour un nom de référendum donné et décrypte le résultat de ce dernier une fois terminé).
- **Email**: Classe utilisant l'API Google pour l'envoi de mails, pour envoyer un mail aux utilisateurs lorsqu'un vote est créé ou qu'ils ont oublié leur mot de passe.
- **Hachage**: Hachage SHA256 pour l'envoi de code dans la base de données.
- **Referendum**: Contient toutes les informations sur un référendum, comme les choix, la liste des votes, la clé publique, les questions et la date d'expiration.

