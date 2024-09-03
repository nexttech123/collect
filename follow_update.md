Cahier des Charges Détailé pour le Développement d’une Application
Android Basée sur ODK Collect

1. Introduction
Ce cahier des charges a pour objectif de détailler les spécifications techniques et
fonctionnelles pour le développement d’une application Android basée sur ODK Collect.
L’application devra permettre aux utilisateurs de se connecter, de récupérer tous les
projets disponibles, et de choisir ceux qu’ils souhaitent. Elle doit également permettre
une personnalisation du design, y compris l’amélioration du logo et du thème, tout en
assurant une compatibilité avec l’application ODK Collect existante.

2. Objectifs

Mise à jour facile via GitHub : Lorsqu’une nouvelle version de ODK Collect est
disponible sur GitHub, l’application personnalisée doit pouvoir être mise à jour
facilement, sans nécessiter de modifications substantielles du code source. Il est
crucial de minimiser les retouches du code de base de ODK Collect.

Override des fichiers : Les personnalisations spécifiques doivent être
implémentées via des fichiers ou des modules qui surchargent (override) les
fichiers existants de ODK Collect, évitant ainsi des modifications directes dans le
code source principal.

Gestion simplifiée des projets : Permettre à l’utilisateur de se connecter à
l’application et de voir tous les projets disponibles pour son compte sans avoir à
scanner un code QR ou à ajouter des liens manuellement.

Personnalisation du design : Adapter l’apparence de l’application (logo,
couleurs, interface utilisateur) pour se distinguer de ODK Collect tout en offrant
une expérience utilisateur cohérente.

Compatibilité avec ODK Collect : Assurer qu’il n’y ait pas de conflit entre
l’application personnalisée et l’application ODK Collect standard si les deux sont
installées sur le même appareil.

Responsiveness : L’application doit être responsive, offrant une expérience
utilisateur optimale sur différents types d’écrans, y compris les smartphones, les
tablettes, et les appareils avec des tailles d’écran variées.


3. Spécifications Fonctionnelles
3.1. Connexion Utilisateur

Authentification : L’application doit permettre une connexion sécurisée via un
nom d’utilisateur et un mot de passe.

Récupération des projets : Une fois connecté, l’application doit récupérer tous
les projets disponibles pour l’utilisateur à partir d’un serveur central.

3.2. Gestion des Projets
Affichage des Projets : Après la connexion, une liste des projets disponibles doit
être
affichée.
L’utilisateur
peut
sélectionner
les
projets
qu’il
souhaite
synchroniser.

Synchronisation : L’application doit permettre de synchroniser les projets
sélectionnés avec l’appareil, téléchargeant les formulaires, les métadonnées et
autres fichiers nécessaires.

Mise à jour des Projets : L’application doit vérifier périodiquement (ou à la
demande) si des mises à jour sont disponibles pour les projets synchronisés et les
télécharger automatiquement.

3.3. Personnalisation de l’Interface Utilisateur
Logo et Branding : L’application doit inclure un logo personnalisé, un thème de
couleurs spécifique, et un style qui se distingue de ODK Collect.

Interface Utilisateur (UI) : Le design de l’interface utilisateur doit être moderne
et intuitif, en tenant compte des meilleures pratiques en matière de design
mobile.

Personnalisation des Écrans : Les écrans d’accueil, de connexion, et de sélection
de projet doivent être personnalisés selon les besoins du client.

Responsiveness : L’interface utilisateur doit être responsive, adaptant son
affichage de manière fluide et optimale selon la taille de l’écran, que ce soit sur un
smartphone, une tablette, ou tout autre appareil.

3.4. Compatibilité et Non-Interférence
Installation Concomitante : L’application doit pouvoir être installée en même
temps que ODK Collect sans conflit. Les deux applications doivent fonctionner de
manière indépendante.

Modification: 
    • ID du package d'une application Android 
L'ID du package est une chaîne de caractères unique qui identifie une application sur le système Android. Il est défini dans le fichier AndroidManifest.xml de l'application et est utilisé par le système pour gérer l'application.
 Fichier Modifier :
   applicationId('org.odk.CultivaSynca.android')  du  Builde.gradle(collect_app)
  "package_name": "org.odk.CultivaSynca.android" du google-service.json

    • Changement des fournisseurs de contenu
 Changement des fournisseurs de contenu (Content Providers) pour l'application. Les fournisseurs de contenu sont utilisés pour gérer l'accès aux données de l'application et permettre à d'autres applications d'accéder ou de manipuler ces données de manière sécurisée
 Fichier modifier:
      package org.odk.collect.android.external;
      class : InstancesContract & FormsContract :
       public static final String AUTHORITY = "org.odk.collect.android.provider.odk.instances";
static final String AUTHORITY = "org.odk.CultivaSynca.android.provider.CultivaSynca.forms";


public static final String AUTHORITY = "org.odk.CultivaSynca.android.provider.CultivaSynca.forms";

       manifeste : 
    • android:authorities="org.odk.CultivaSynca.android.provider.CultivaSynca.forms"
    • android:authorities="org.odk.CultivaSynca.android.provider.odk.instances"
Exception: 

Si je change les classse  il me genere cette erreur(URI introuvable) mais si je laisse inchange en changant dans le manifeste seulement ca marche correctement. !!!!! A note qu’i n ‘y aura pas des probleme dans le serveur.

 
Non-Interférence : Les fichiers de configuration, bases de données, et autres
ressources utilisées par l’application personnalisée doivent être séparés de ceux
de ODK Collect pour éviter toute interférence.


4. Mise à Jour Facile via GitHub
4.1. Suivi des Mises à Jour d’ODK Collect

Notification de Mise à Jour : L’équipe de développement doit surveiller les
nouvelles versions de ODK Collect publiées sur GitHub.

Intégration Simplifiée : Lorsqu’une nouvelle version de ODK Collect est
disponible, elle doit pouvoir être intégrée dans l’application personnalisée en
limitant au maximum les retouches du code source.

Override des Fichiers : Pour garantir la facilité de mise à jour, les
personnalisations spécifiques doivent être faites par le biais de fichiers de
surcharge (override) qui remplacent ou étendent les fichiers originaux de ODK
Collect. Cela permet d’incorporer de nouvelles versions de ODK Collect sans
toucher au cœur du code personnalisé.
4.2. Structure Modulaire

Modularité du Code : L’application doit être conçue de manière modulaire,
permettant aux nouvelles fonctionnalités ou correctifs d’ODK Collect d’être
intégrés sans compromettre les personnalisations spécifiques. Les modules
personnalisés doivent être isolés pour éviter les conflits lors des mises à jour.

Documentation : Une documentation claire doit être fournie pour indiquer
comment les mises à jour d’ODK Collect peuvent être intégrées dans l’application
personnalisée, y compris une liste des fichiers overridés.


5. Spécifications Techniques
5.1. Langage de Programmation
Langage : L’application sera développée en Java ou Kotlin, suivant les standards
de développement pour Android.

Compatibilité : L’application doit être compatible avec les versions Android 7.0
(Nougat) et ultérieures.
5.2. Architecture

Modularité : Utilisation d’une architecture modulaire pour faciliter les mises à
jour et la maintenance.

API de Communication : L’application doit interagir avec un serveur central via
des APIs REST pour la gestion des utilisateurs et la récupération des projets.5.3. Gestion des Données

Stockage Local : Utilisation de la base de données SQLite pour stocker les
données des projets synchronisés.

Sécurisation des Données : Toutes les données sensibles doivent être chiffrées
sur l’appareil. Utilisation de Secure Storage pour les informations d’identification.

6. Délais et Livrables
6.1. Développement

Phase de Développement : 3 mois, incluant les phases de design, de
développement et de tests.

Livrables :
o Design de l’interface utilisateur et du logo.
o Version beta de l’application pour les tests.
o Version finale de l’application.
o Documentation technique et guide utilisateur.

6.2. Tests

Tests Unitaires : Couverture des fonctionnalités principales à travers des tests
unitaires.

Tests d’Intégration : Assurer que l’intégration des modules fonctionne
correctement.

Tests de Compatibilité : Vérification de la compatibilité de l’application avec
différentes versions d’Android et avec ODK Collect installé simultanément.

7. Maintenance et Support
Maintenance : Une période de maintenance de 6 mois après le déploiement pour
corriger les bugs et assurer les mises à jour nécessaires.

Support : Un support technique sera disponible pour assister les utilisateurs
finaux pendant la phase de déploiement et de maintenance.
8. Conclusion
Ce cahier des charges définit les spécifications complètes pour le développement d’une
application Android basée sur ODK Collect, avec des fonctionnalités avancées de gestion
de projets, une personnalisation complète du design, et une interface responsive.
L’application doit être conçue de manière modulaire, permettant une mise à jour facile,
en particulier lors de la publication de nouvelles versions d’ODK Collect sur GitHub, tout
en garantissant une compatibilité totale avec l’application ODK Collect existante.
