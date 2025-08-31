# Commerce Kata

An e-commerce system built as an architectural learning exercise, focusing on modular monolith design and gradual
evolution toward distributed systems.

## Project Goals

This project serves as a practical kata for understanding software architecture patterns and their trade-offs. The
primary objectives are:

- **Modular Monolith First**: Build a well-structured monolithic application with clear module boundaries before
  considering microservices
- **Pattern-Driven Architecture**: Apply different architectural patterns to each business context based on their
  specific requirements
- **Infrastructure from Day 0**: Integrate observability, deployment, and operational concerns from the beginning rather
  than as afterthoughts
- **Evolutionary Design**: Demonstrate how systems can evolve from simple CRUD operations to complex event-driven
  architectures

## Architecture Philosophy

Each module uses the architectural pattern best suited to its business domain:

- **Catalog Context**: Simple CRUD-based, data-centric model emphasizing straightforward operations
- **Orders Context**: Event sourcing and CQRS for complete audit trails and complex business logic
- **Other Contexts**: Additional patterns introduced as the system grows in complexity

## Technology Stack

- **Language**: Kotlin
- **Framework**: Spring Boot with Spring Data JDBC
- **Database**: PostgreSQL
- **Architecture**: Spring Modulith for modular monolith structure

## Development Stages

The project follows a staged approach, with each phase building upon the previous:

1. **Walking Skeleton**: Basic end-to-end buy flow
2. **Users & Inventory**: Authentication and stock management
3. **Pre-Purchase Flow**: Shopping cart and user experience
4. **Money & Notifications**: Payment integration and async processing
5. **Shipping & Search**: Complete e-commerce features

This is an architectural exercise designed to demonstrate the evolution of system complexity and the thoughtful
application of design patterns.
