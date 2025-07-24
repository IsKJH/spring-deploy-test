# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Language Preference
**IMPORTANT**: This is a Korean-focused application. When users ask questions in Korean (한글), always respond in Korean. When users ask questions in English, respond in English. The application contains Korean messages and is primarily targeted at Korean users.

## Project Overview

This is a Spring Boot 3.5.3 application (`eddi_home`) that provides an e-commerce platform with social login functionality, shopping cart, order management, and Redis-based caching. The application uses Java 17, Gradle build system, and follows a layered architecture pattern with multiple OAuth providers (Kakao, Google, GitHub).

## Build and Development Commands

### Core Commands
- **Build**: `./gradlew build`
- **Run application**: `./gradlew bootRun`
- **Run tests**: `./gradlew test`
- **Clean build**: `./gradlew clean build`

### Development Setup
- **Server runs on**: Port 7777 (configured in .env)
- **Database**: MySQL (`backend_db`) with H2 console available at `/h2-console` for testing
- **Redis**: Local instance on port 6379 for caching and token storage

## Architecture Overview

### Modular Structure
The application follows a domain-driven design with these main modules:

1. **Account Module** (`/account/`): User account management for social login users
2. **Kakao Authentication Module** (`/kakaoAuthentication/`): OAuth integration with Kakao (package renamed to camelCase)
3. **Bread Module** (`/bread/`): Product catalog for bakery items with enum-based categorization (Sort)
4. **Cart Module** (`/cart/`): Shopping cart functionality with user-specific cart items
5. **Order Module** (`/order/`): Order processing and management system
6. **Redis Cache Module** (`/redis_cache/`): Distributed caching and token management
7. **Response Module** (`/response/`): Standardized API response wrapper

### Layer Architecture
Each module follows the same pattern:
- **Controller**: REST endpoints with request/response DTOs
- **Service**: Business logic interface and implementation
- **Repository**: Data access layer extending JpaRepository
- **Entity**: JPA entities with Lombok annotations

### Configuration Patterns
- **Environment Variables**: All configuration externalized to `.env` file
- **YAML Configuration**: `application.yaml` uses environment variable placeholders
- **CORS**: Configured for multiple frontend origins
- **API Response**: Standardized response wrapper (`ApiResponse<T>`) for all endpoints

### Key Dependencies
- **Spring Data JPA**: Database operations with MySQL and H2
- **Spring Data Redis**: Caching and session management  
- **Lombok**: Reduces boilerplate code
- **Jackson CBOR**: Binary JSON serialization
- **Spring Dotenv**: Environment variable management

### Database Schema & Entity Relationships
- **Account Table**: `social_login_account` stores user data from OAuth providers
- **Bread Table**: Product catalog with categorization via Sort enum
- **Cart Table**: Shopping cart items linking Account and Bread with quantities
- **Order Table**: Order records with reservation capabilities
- **Redis**: Token storage and caching layer
- **JPA Configuration**: `ddl-auto=update` for schema management

**Key Entity Relationships**:
- Account ↔ Cart (One-to-Many)
- Account ↔ Order (One-to-Many)  
- Bread ↔ Cart (One-to-Many)
- Bread ↔ Order (One-to-Many)

### OAuth Flow Architecture
The application implements a multi-provider OAuth system:
1. Frontend redirects to provider login URLs
2. OAuth callbacks handled by dedicated controllers
3. User info fetched and stored in Account entities
4. Tokens cached in Redis for session management
5. CORS configured for seamless frontend integration

## Development Notes

### Database Switching
The application supports both MySQL (production) and H2 (testing) through environment configuration. Schema is auto-managed via JPA.

### OAuth Provider Integration
Each OAuth provider (Kakao/Google/GitHub) has its own module with consistent patterns for token exchange and user info retrieval.

### Redis Integration
Used for both caching business data and storing authentication tokens. Connection configured via environment variables.

### E-commerce Workflow
The application supports a complete e-commerce flow:
1. Users authenticate via OAuth (Kakao/Google/GitHub)
2. Browse bread products with category filtering (Sort enum)
3. Add items to shopping cart with quantities
4. Convert cart items to orders
5. Manage order history and reservations

### Korean Language Context
The application includes Korean error messages (e.g., "토큰이 올바르지 않습니다.") and is designed for Korean users. All user-facing messages should maintain Korean language consistency.