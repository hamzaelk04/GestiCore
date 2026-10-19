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
