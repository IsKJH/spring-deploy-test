# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 application (`eddi_home`) that provides social login functionality with multiple OAuth providers (Kakao, Google, GitHub) and Redis-based caching. The application uses Java 17, Gradle build system, and follows a layered architecture pattern.

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
2. **Kakao Authentication Module** (`/kakao_authentication/`): OAuth integration with Kakao
3. **Bread Module** (`/bread/`): Business domain module (replaces the removed Book module)
4. **Redis Cache Module** (`/redis_cache/`): Distributed caching and token management

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

### Database Schema
- **Account Table**: `social_login_account` stores user data from OAuth providers
- **Redis**: Token storage and caching layer
- **JPA Configuration**: `ddl-auto=update` for schema management

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