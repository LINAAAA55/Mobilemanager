# MiniShopManager 🛒📱

MiniShopManager est une application Android native développée en **Kotlin** dans le cadre du module **Programmation Mobile (Licence GTIC - ISET'COM)**.

L'application permet la gestion d'un catalogue de produits informatiques et d'accessoires, l'authentification avec mémorisation de session, la gestion d'un panier d'achat persistant via SQLite, l'historique des ventes, le filtrage par catégories, la recherche en temps réel, ainsi que le support du Mode Sombre et du multilinguisme (Français / العربيّة).

---

## 🎯 Fonctionnalités principales

### 🔑 TP 1 & TP 3 : Authentification & Session
- **Écran de Connexion (`LoginActivity`)** : Saisie du nom d'utilisateur / étudiant.
- **Pattern "Se souvenir de moi"** : Sauvegarde et pré-remplissage de session grâce à `SharedPreferences`.
- **Navigation & Cycle de vie** : Gestion explicite des `Intent` et traçabilité des méthodes du cycle de vie (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`).

### 📦 TP 2 & TP 3 : Catalogue de Produits Statique & Dynamique
- **Chargement JSON (`produits.json`)** : Lecture et parsing dynamique depuis le dossier `assets`.
- **Cartes produits personnalisées (`ProductAdapter`)** : Affichage d'icônes vectorielles dédiées, titre, prix en TND et catégorie.
- **Section "Arrives Soon" (🔥 Prochains Smartphones)** : Section réservée aux futures nouveautés (iPhone 16 Pro, Galaxy S25 Ultra, Pixel 9 Pro, Xiaomi 15 Ultra).

### 🛒 TP 3 : Panier d'Achat & Base de Données SQLite
- **Gestionnaire de panier (`DBHelper` & `CartManager`)** : Ajout instantané des produits sélectionnés dans la base SQLite locale `minishop.db`.
- **Écran Panier (`CartActivity`)** : Modification des quantités (incrément / décrément), suppression d'articles par glissement (swipe-to-delete) et calcul automatique du montant total.
- **Validation de Commande & Historique (`SalesActivity`)** : Enregistrement atomique des ventes avec horodatage et calcul du chiffre d'affaires total.

### 🎨 Thème, Filtres & Multilinguisme
- **Filtre par Catégories** : Boutons de filtrage rapide (*Tous*, *Smartphones*, *High-Tech*, *Accessoires*, *Cosmétiques*).
- **Recherche en temps réel** : Filtrage dynamique des produits dès la saisie dans la barre de recherche.
- **Mode Sombre (Dark Mode 🌙/☀️)** : Basculement de thème jour/nuit avec mémorisation des préférences utilisateur.
- **Multilinguisme (🌐 Français / العربيّة)** : Support complet de la langue arabe avec gestion du système d'affichage de droite à gauche (RTL).

---

## 🛠️ Technologies & Outils

- **Langage** : Kotlin (JVM 17)
- **Framework UI** : Android SDK (API 24 à 34), Material Design 3, Vector Drawables
- **Stockage Local** : SQLite (`SQLiteOpenHelper`), `SharedPreferences`
- **Architecture & Formats** : Parsing JSON (`JSONObject`), Adapters (`ArrayAdapter`, `RecyclerView.Adapter`)
- **Outil de Build** : Gradle 8.13 (AGP 8.13.2)

---

## 📂 Structure du Projet

```text
untitled/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── produits.json          # Catalogue dynamique des produits
│   │   ├── java/com/example/minishopmanager/
│   │   │   ├── LoginActivity.kt       # Écran de connexion & SharedPreferences
│   │   │   ├── MainActivity.kt        # Écran principal, catalogue & recherche
│   │   │   ├── ProfileActivity.kt     # Profil utilisateur & modification
│   │   │   ├── CartActivity.kt        # Gestion du panier d'achat
│   │   │   ├── SalesActivity.kt       # Historique des ventes SQLite
│   │   │   ├── Product.kt             # Modèle de données produit
│   │   │   ├── ProductAdapter.kt      # Adaptateur personnalisé pour la ListView
│   │   │   ├── CartManager.kt         # Gestionnaire d'état du panier
│   │   │   └── DBHelper.kt            # Helper SQLite (tables panier, users, sales)
│   │   └── res/
│   │       ├── layout/                # Layouts XML des écrans
│   │       ├── drawable/              # Icônes vectorielles et logo ShopCart
│   │       ├── values/                # Thème clair & chaînes de caractères (FR)
│   │       ├── values-night/          # Thème sombre (Dark Mode)
│   │       └── values-ar/             # Traduction arabe (العربيّة)
│   └── build.gradle.kts               # Configuration du module app
├── build.gradle.kts                   # Configuration racine du projet
├── gradle.properties                  # Propriétés Gradle & options JVM
└── settings.gradle.kts                # Inclusion des modules
```

---

## 🚀 Installation & Exécution

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/LINAAAA55/Mobilemanager.git
   ```

2. **Ouvrir le projet dans Android Studio :**
   - Sélectionner `File -> Open` et choisir le dossier du projet.
   - Laisser Android Studio effectuer la synchronisation Gradle.

3. **Lancer l'application :**
   - Connecter un smartphone Android en mode *Débogage USB* ou démarrer un émulateur (API 24+).
   - Cliquer sur le bouton **Run (▶)** (`Shift + F10`).

---

## 🎓 Auteurs & Encadrement

- **Étudiante** : Lina Barhoumi (Licence GTIC - 3ème année)
- **Établissement** : Institut Supérieur des Études Technologies en Communications de Tunis (ISET'COM)
- **Module** : Développement Mobile / Programmation Mobile
- **Enseignantes** : Asma Ayari & Amani Chouikhi
