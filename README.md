<img width="455" height="602" alt="image" src="https://github.com/user-attachments/assets/9378a008-abde-4538-9796-1d8786a30864" />##### Spring Core

### 1. Qu’est-ce que Spring Core et à quoi sert-il dans une application Java ?

Spring Core est le module fondamental du framework Spring. Il sert à gérer automatiquement les objets Java et leurs dépendances, en reposant principalement sur deux concepts : l’Inversion of Control (IoC) et l’Injection de Dépendances (DI).

### 2. Que signifie le principe d’Inversion de Contrôle (IoC) ?

IoC signifie que le contrôle de la création et de la gestion des objets est transféré de notre code vers un conteneur, comme celui de Spring.

### 3. Quelle est la différence entre IoC et Injection de Dépendances (DI) ?

La différence est que l’IoC est un principe de conception, tandis que la DI est une technique qui permet de mettre ce principe en pratique.

### 4. Qu’est-ce qu’un bean dans Spring ?

Un bean est un objet qui est instancié, assemblé et géré par le conteneur IoC de Spring.

### 5. Quel est le rôle du conteneur IoC ?

Son rôle principal est la création, la configuration et la gestion des beans et de leurs dépendances.

### 6. Quelle est la différence entre ApplicationContext et BeanFactory ?

Dans Spring, les deux sont des interfaces qui représentent le conteneur IoC.

Cependant, `ApplicationContext` prend en charge l’instanciation anticipée des beans (*Eager Loading*), l’internationalisation (gestion des fichiers de messages) et une intégration simplifiée de la programmation orientée aspect (AOP) avec Spring.

En revanche, `BeanFactory` prend en charge l’instanciation à la demande des beans (*Lazy Loading*), tandis que l’intégration de l’AOP nécessite une configuration plus complexe et manuelle.

### 7. Quelles sont les trois approches de configuration dans Spring (XML, annotations, Java Config) ?

Dans Spring, il existe trois approches de configuration :

- **XML :** La configuration est définie dans un fichier `.xml` séparé du code Java, en utilisant des balises comme `<beans>`.
- **Annotations :** La configuration est davantage liée au code, en utilisant des annotations directement dans les classes, comme `@Service`, etc.
- **Java Config :** La configuration est définie à l’aide de classes Java annotées avec `@Configuration` et de méthodes annotées avec `@Bean`.

### 8. À quoi servent les annotations suivantes ?

- **`@Configuration` :** Indique que la classe contient la configuration de Spring, notamment les méthodes qui déclarent les beans.
- **`@ComponentScan` :** Indique à Spring dans quels packages rechercher les classes annotées avec `@Component`, `@Service`, `@Repository`, etc.
- **`@Bean` :** Déclare explicitement qu’un objet retourné par une méthode doit être géré par le conteneur Spring.
- **`@Component` :** Désigne un composant générique géré par Spring.
- **`@Service` :** Désigne un composant de la couche métier, chargé des règles métier et des traitements de l’application.
- **`@Repository` :** Désigne un composant de la couche d’accès aux données.
- **`@Controller` :** Désigne un composant chargé de recevoir et de traiter les requêtes dans Spring MVC.
- **`@Autowired` :** Demande à Spring d’injecter automatiquement une dépendance.
- **`@Qualifier` :** Permet de choisir le bean à injecter lorsque plusieurs beans sont compatibles avec le même type.

### 9. Comment Spring détecte-t-il et crée-t-il automatiquement les composants ?

- **Scan des composants :** Spring parcourt les packages pour rechercher les classes annotées.
- **Détection :** Il identifie les classes annotées avec `@Component`, `@Service`, `@Repository`, etc.
- **Création des beans :** Il enregistre les composants détectés comme beans dans son conteneur IoC.
- **Injection des dépendances :** Spring fournit aux composants les objets dont ils ont besoin.

### 10. Quelles sont les étapes du cycle de vie d’un bean ?

- **Instanciation :** Spring crée l’objet.
- **Injection des dépendances (DI) :** Spring fournit les objets nécessaires.
- **Initialisation :** Spring prépare le bean à être utilisé.
- **Utilisation :** Le bean est disponible pour l’application.
- **Destruction :** Spring effectue le nettoyage prévu.

### 11. Quelle est la différence entre les scopes de bean ?

- **Singleton :** Une seule instance par conteneur Spring.
- **Prototype :** Une nouvelle instance à chaque demande au conteneur.
- **Request :** Une instance par requête HTTP.
- **Session :** Une instance par session HTTP.
- **Application :** Une instance par `ServletContext` Web.
- **WebSocket :** Une instance par session WebSocket.

### 12. Pourquoi est-il important de comprendre la configuration manuelle ?

Il est important de comprendre la configuration manuelle, car elle permet de comprendre le fonctionnement interne de Spring au lieu de simplement utiliser ses automatismes.

**____________________________________________________________________________________________________________________________________________________________**

##### Spring Data JPA

### 1. Qu’est-ce que Spring Data et quel problème résout-il ?

Spring Data JPA est un module de Spring qui facilite l’accès aux données en fournissant des repositories et des méthodes CRUD prêtes à l’emploi.

Il résout le problème de la création manuelle des requêtes SQL et réduit le code répétitif nécessaire à la persistance des données.

### 2. Quelle est la différence entre JPA et Hibernate ?

La différence principale est que JPA est une spécification qui définit les règles et les interfaces permettant de gérer la persistance des objets Java dans une base de données.

Tandis qu’Hibernate est un framework ORM qui implémente cette spécification et réalise les opérations correspondantes.

### 3. Qu’est-ce qu’une entité JPA ?

Une entité JPA est une classe Java annotée avec `@Entity` qui représente une table dans une base de données. Chaque attribut correspond à une colonne et chaque instance représente un enregistrement.

### 4. À quoi sert le DataSource ?

C’est une interface JDBC qui permet d’obtenir des connexions à une base de données sans devoir gérer manuellement tous les détails de chaque connexion.

### 5. Que fait l’EntityManager ?

C’est une interface JPA qui est responsable de la gestion du cycle de vie des entités persistantes, généralement dans le cadre d’une transaction.

### 6. Quelle est la responsabilité du TransactionManager ?

Il est responsable de la gestion des transactions dans une application. Il démarre une nouvelle transaction, puis la valide avec un *commit* si tout se passe correctement. Sinon, il l’annule avec un *rollback*.

### 7. À quoi sert `@EnableJpaRepositories` ?

Cette annotation active la détection des interfaces Repository de Spring Data JPA et permet à Spring de générer automatiquement leurs implémentations.

### 8. Qu’est-ce qu’un repository Spring Data ?

C’est une interface qui permet d’accéder aux données d’une base de données sans avoir à écrire manuellement toutes les méthodes CRUD.

### 9. Quelles sont les méthodes génériques fournies par `JpaRepository` ?

- **`save(entity)` :** Crée ou met à jour une entité.
- **`saveAll(entities)` :** Enregistre plusieurs entités.
- **`findById(id)` :** Recherche une entité par son identifiant.
- **`findAllById(ids)` :** Recherche plusieurs entités par leurs identifiants.
- **`findAll()` :** Récupère toutes les entités.
- **`existsById(id)` :** Vérifie si une entité existe à partir de son identifiant.
- **`count()` :** Compte le nombre d’entités.
- **`deleteById(id)` :** Supprime une entité à partir de son identifiant.
- **`delete(entity)` :** Supprime une entité.
- **`deleteAll()` :** Supprime toutes les entités.
- **`flush()` :** Synchronise les modifications en attente avec la base de données.

### 10. Comment gérer les transactions avec Spring ?

1. Activer la gestion des transactions avec `@Transactional`.
2. Le *commit* valide les changements ; le *rollback* les annule.
3. Choisir un mode de propagation adapté.
4. Définir le niveau d’isolation des transactions si nécessaire.

### 11. Pourquoi définir manuellement la connexion à la base de données ?

Pour permettre de contrôler explicitement les composants responsables de la connexion à la base de données.

### 12. Que doit contenir une configuration complète ?

1. **DataSource :** Fournit les connexions à la base de données.
2. **EntityManagerFactory :** Crée les `EntityManager` et configure la persistance JPA.
3. **TransactionManager :** Gère les transactions.

### 13. Qu’est-ce que la validation des contraintes dans le modèle ?

La validation des contraintes permet de garantir la cohérence des données. Spring utilise notamment Jakarta Bean Validation avec des annotations comme `@Size`, `@Email`, etc.

### 14. Quelle est la différence entre le soft delete et le hard delete ?

Le **hard delete** (suppression physique) efface réellement l’enregistrement de la base de données, tandis que le **soft delete** (suppression logique) le conserve dans la base de données en le marquant comme supprimé.
