
# my-spring-data

## Provides
reference implementation for [spring-data](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.core-concepts)
- useful to clone when  starting a new project
- spring-boot-starter-web
- spring-boot-starter-validation
- spring-boot-starter-data-jpa
- spring-boot-starter-log4j2, jackson-dataformat-yaml
- spring-boot-starter-thymeleaf



### Configuration

- settings in spring boot application.yml
- refer to: [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)


### Desktop development:

- dependencies defined using maven, so any IDE works  


### Unit tests
- tiered testing:
  - performance: JMeter
  - full: @SpringBootTest
  - web: @WebMvcTest
  - data: @DataJpaTest
  - pojo 