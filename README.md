
PLEASE NOTE: This project is still in development.

## Compatibility

| jsonquery | Java | Persistence API        | QueryDSL                    | Spring Boot |
|-----------|------|------------------------|-----------------------------|-------------|
| 1.x       | 8+   | `javax.persistence`    | 4.x                         | 2.x         |
| 2.x       | 17+  | `jakarta.persistence`  | 5.1 with `jakarta` classifier | 3.x, 4.x  |

## Usage

```kotlin
implementation("com.lindar:jsonquery-querydsl-jpa:2.0.0")
implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
```

Always request `querydsl-jpa` with the `jakarta` classifier. Without it the javax jar lands next to the
jakarta one and both define the same classes.

## Upgrading from 1.x

- Entities must use `jakarta.persistence` annotations. `@Table`, `@Id`, `@OneToMany`, `@ManyToOne` and
  `@ManyToMany` are read reflectively, and javax ones are silently ignored.
- `jakarta.persistence-api` is `provided`: the JPA provider supplies it.
- `hibernate-jpa-2.1-api`, Lombok and slf4j are no longer transitive dependencies.
- Generated JPQL numbers every parameter separately (`?1`, `?2`, ...) rather than reusing a label for
  repeated values. This is QueryDSL 5 behaviour, so it matches 1.x running on QueryDSL 5.

## Building

```shell
./mvnw verify
```

The build fails if a javax persistence API or the javax `querydsl-jpa` jar reaches any module's dependency tree.
