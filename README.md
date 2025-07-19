# NaTour21 🥾 (Social Hiking & Route Exploration App)

> **NaTour21** is a *Bachelor’s degree academic project*: a social hiking & route exploration Android application enabling users to discover routes, create & filter hiking posts, manage favorite collections, review content and report inappropriate items. The mobile client follows an **MVVM architecture** and integrates with a **Spring Boot backend** deployed on **AWS EC2**, persisting data in **Amazon RDS (MySQL)**, storing media assets in **Amazon S3** and geographic data from **Google Maps API**. PlantUML diagrams document the domain & high‑level design.

<p align="center">
  <img alt="License MIT" src="https://img.shields.io/badge/license-MIT-blue" />
  <img alt="Status" src="https://img.shields.io/badge/status-beta-orange" />
  <img alt="Last Commit" src="https://img.shields.io/github/last-commit/Klinker195/NaTour21" />
  <img alt="Code Size" src="https://img.shields.io/github/languages/code-size/Klinker195/NaTour21" />
</p>

---

## Table of Contents
1. [Overview](#overview)
2. [Key Features](#key-features)
3. [Architecture](#architecture)
4. [Project Structure](#project-structure)
5. [Domain Model](#domain-model)
6. [API Layer](#api-layer)
7. [Adapters & UI Lists](#adapters--ui-lists)
8. [Error Handling & Logging](#error-handling--logging)
9. [Diagrams (PlantUML)](#diagrams-plantuml)
10. [License](#license)
11. [Academic Citation](#academic-citation)

---

## Overview
NaTour21 provides an end‑to‑end experience for hikers: sign up & verification, authentication (including Google sign‑in flow), route exploration & search, post creation (with optional geo context), post browsing via recycler feed, favorites (collections), reviews, and reporting. The backend (separate repository / private) exposes REST endpoints consumed by the Android client.

## Key Features
- **User Onboarding & Auth**: Sign up, email / code verification, login, password reset, logout (with callback activities), Google sign‑in integration.
- **Route Exploration**: Dedicated activity + fragments for exploring & filtering hiking routes (search + map variants).
- **Post Lifecycle**: Create, persist and view posts (e.g., hike experiences, route snapshots, notes).
- **Content Feed**: RecyclerView feed for posts with card adapters supporting efficient view binding.
- **Favorites Management**: Create and manage favorite collections of routes/posts.
- **Reviews & Ratings**: Users can review or rate content (domain objects `Review`, `Post`).
- **Reporting / Moderation**: Submit reports (`Report`) for inappropriate content.
- **User Profiles**: View profile details and related user content.
- **Cloud Persistence**: Backend persistence layer uses MySQL on Amazon RDS; media / binary assets in Amazon S3 (e.g., images, attachments).
- **Scalable Backend**: Spring Boot service deployed on AWS EC2 instance (independent scaling from the client).
- **Documentation Diagrams**: PlantUML .puml files for domain & architectural visualization.

## Architecture
**Client Layer (Android)** employs **MVVM**: Activities / Fragments (View) observe ViewModels exposing immutable UI state & functions; ViewModels mediate between the UI and a data layer wrapping API interfaces. Data Transfer Objects (DTOs) map backend JSON to in‑app models. Adapters decouple list rendering.  
**Backend Layer** (Spring Boot) exposes REST endpoints for auth, posts, routes, collections, reviews, reports.  
**Cloud Infrastructure**: EC2 for the Java web server, RDS MySQL for relational persistence (referential integrity, structured queries), and S3 for object storage (user images / media). Security groups, IAM roles & least‑privilege S3 bucket policies (recommended) protect resources.  

## Project Structure
_Simplified high‑level view (focus on core classes); adjust to actual package paths as needed:_
```
NaTour21/
  app/ (Android module)
    java/com/.../natour/
      activities/
        AuthenticationActivity.java
        SignUpActivity.java
        VerificationActivity.java
        ResetPasswordActivity.java
        GoogleLoginCallbackActivity.java
        LogoutCallbackActivity.java
        IntroductionActivity.java
        PostCreationActivity.java
        PostCreationMapsActivity.java
        PostDetailsActivity.java
        RouteExplorationActivity.java
      fragments/
        RouteExplorationFragment.java
        RouteSearchFragment.java
        PostRecyclerFragment.java
        ProfileDetailsFragment.java
      viewmodel/
        ViewModelBase.java
        AuthenticationViewModel.java
        SignUpViewModel.java
        VerificationViewModel.java
        RegistrationFormViewModel.java
        ResetPasswordViewModel.java
        IntroductionViewModel.java
        PostCreationViewModel.java
        PostDetailsViewModel.java
        RouteExplorationViewModel.java
      adapters/
        PostCardAdapter.java
        UserCardAdapter.java
        FavCollectionAdapter.java
      api/
        IUserAPI.java
        IPostAPI.java
        IReportAPI.java
        IFavCollectionAPI.java
        AmazonAPI.java
      model/       # Domain entities
        User.java
        Post.java
        Review.java
        Report.java
        FavCollection.java
      dto/
        UserDTO.java
        PostDTO.java
        ReviewDTO.java
        ReportDTO.java
        FavCollectionDTO.java
      NatourApplication.java
```

## Domain Model
Core entities (simplified overview):
- **User**: identity, profile attributes, relationships to posts / reviews / favorites.
- **Post**: hiking content (text, media references, route metadata).
- **Review**: user feedback on content or routes.
- **FavCollection**: grouping of favorites (posts / routes) for quick access.
- **Report**: moderation object describing flagged content (reason, reporter, target).
DTO classes provide transport representations decoupled from internal domain logic (serialization boundaries).

## API Layer
`IUserAPI`, `IPostAPI`, `IReportAPI`, `IFavCollectionAPI`, plus `AmazonAPI` encapsulate server endpoints (auth, CRUD for social objects, media operations). A repository pattern (if added) can coordinate request orchestration, caching & mapping DTO ↔ domain. Each ViewModel depends only on these interfaces (inversion for testability).

## Adapters & UI Lists
Adapters (`PostCardAdapter`, `UserCardAdapter`, `FavCollectionAdapter`) bind domain/DTO data to RecyclerView items for performant scrolling. ViewHolder pattern minimizes layout inflation and enables smooth feeds. Consider adding DiffUtil for granular update efficiency.

## Error Handling & Logging
- Centralize network errors (timeouts, auth failures) in API layer.
- Emit user‑friendly messages in ViewModels; Views observe error state.
- Structured logging (JSON) server‑side for traceability.

## Diagrams (PlantUML)
The `*.puml` files define domain & architecture diagrams. To render (CLI example):
```bash
# Or with Java JAR (if plantuml.jar downloaded)
java -jar plantuml.jar diagrams/*.puml
```
Generated `.png` / `.svg` artifacts can be committed under `docs/` for documentation portals.

---

*Happy Hiking & Coding!* 🥾
