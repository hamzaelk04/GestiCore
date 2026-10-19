##### Spring Core

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
