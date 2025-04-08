# 📚 Formation Spring Boot – Par NGOUPAYE DJIO Thierry

Bienvenue dans ce dépôt dédié à ma **formation approfondie sur Spring Boot**, réalisée dans le cadre de ma montée en compétence en développement backend avec Java. Ce projet est structuré par **branches thématiques**, afin d’illustrer différents concepts clés du framework Spring.

---

## 🚀 À propos de Spring Boot

**Spring Boot** est un framework Java open-source qui facilite la création d'applications stand-alone, prêtes pour la production, avec une configuration minimale. Il repose sur le célèbre framework **Spring** tout en simplifiant son utilisation grâce à :
- Une configuration par convention
- L'intégration automatique des dépendances
- Des starters prêts à l’emploi
- Un serveur embarqué (Tomcat, Jetty…)

Il est aujourd’hui très utilisé pour développer des **API REST**, des **applications web sécurisées**, ou des **microservices**.

---

## 🌿 Branches du projet

| Branche            | Description |
|--------------------|-------------|
| `jpa`              | Implémentation de la couche de **persistance** avec **Spring Data JPA** |
| `spring-security`  | Mise en œuvre de **Spring Security** pour sécuriser l’application via JWT |

---

## 🔐 Spring Security (branche `spring-security`)

**Spring Security** est un module puissant du framework Spring pour gérer l’**authentification** et l’**autorisation**.

Dans cette branche, on explore :
- La sécurisation des endpoints REST
- L’authentification avec **JWT (JSON Web Token)**
- La création de filtres personnalisés pour intercepter les requêtes
- La gestion des rôles et des accès
- L'utilisation de `SecurityFilterChain` (au lieu de `WebSecurityConfigurerAdapter`)

⚠️ **Complexité** :  
Spring Security est un module **puissant mais complexe à configurer**, surtout depuis l’abandon de certaines classes comme `WebSecurityConfigurerAdapter`. La nouvelle approche nécessite une bonne maîtrise des **beans**, de la **chaîne des filtres**, et de la **configuration déclarative avec des lambdas**. Cette complexité est justement l’un des points forts de cette formation.

---

## 💾 Spring Data JPA (branche `jpa`)

Cette branche illustre l'utilisation de **Spring Data JPA**, une abstraction sur **Hibernate**, pour faciliter l'accès aux données.

On y trouve :
- La définition des entités (`@Entity`)
- La configuration des relations (`@OneToMany`, `@ManyToOne`, etc.)
- L’utilisation des interfaces `JpaRepository` pour créer des requêtes sans écrire de SQL
- La mise en place de services REST pour manipuler les entités

---


