# TestGraph

## Description

Your application consists of two screens:
1. **Input Number of Points**: Users can input the number of points that the application will request through a service. In the event that the service is unavailable, the application offers the option to randomly generate points.
2. **Table of Coordinates and Graph**: This screen displays a table with the coordinates of the retrieved points, alongside a graph where these points are visualized.

## Architecture

The project utilizes **Clean Architecture** and the **MVI** pattern, ensuring efficient and clean code, which facilitates maintenance and expansion.

## Technologies and Libraries

- **Kotlin**: Programming language
- **Jetpack Compose**: Framework for building the UI
- **Coroutines**: Library for asynchronous programming
- **Room**: Library for database management
- **Koin**: Dependency injection library
- **Retrofit**: Library for network requests
