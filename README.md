# PointGrapher

> **Note:** This project was implemented as a **test assignment**.

Android application for requesting point coordinates from a server and displaying them in a table and an interactive chart.

<img width="210" alt="Screenshot 2025-06-24 at 15 24 09" src="https://github.com/user-attachments/assets/0e0ee5a0-d353-4970-83af-81213070998a" />
<img width="213" alt="Screenshot 2025-06-24 at 15 25 12" src="https://github.com/user-attachments/assets/3b745f0f-08c6-4adb-b1a0-b58a5083d117" />
<img width="212" alt="Screenshot 2025-06-24 at 15 26 29" src="https://github.com/user-attachments/assets/37450244-88ef-41b4-8eaa-b24df173d959" />
<br/><br/>
<img width="210" alt="Screenshot 2025-06-24 at 15 24 31" src="https://github.com/user-attachments/assets/af201ed7-5199-4ade-9834-2adc8a9851d4" />
<img width="212" alt="Screenshot 2025-06-24 at 15 26 52" src="https://github.com/user-attachments/assets/758a94fc-39fa-428f-a5ab-90e0db8d070b" />
<img width="209" alt="Screenshot 2025-06-24 at 15 27 10" src="https://github.com/user-attachments/assets/aea0e56f-871c-4eee-b29d-7fd24c7c31c2" />


## Description

The application allows users to:
- Request a specified number of point coordinates (x, y) from a server
- Display the received data in a **table** and an **interactive chart**
- Save request history with the ability to view and delete it
- Work in both portrait and landscape orientations

## Tech Stack

- **Jetpack Compose** – Modern UI framework for Android
- **Vico** – Interactive charting library for Compose
- **Hilt** – Dependency Injection
- **Room** – Local database for storing history
- **Retrofit + OkHttp** – Networking with logging
- **Kotlin Coroutines** – Asynchronous programming
- **Navigation 3** – Navigation for Compose
- **Kotlin Serialization** – JSON serialization

## Architecture

The project follows **Clean Architecture** and **MVVM** principles:

```
app/
├── data/           # Data layer
│   ├── api/        # API interfaces
│   ├── datasource/ # Remote & local sources
│   ├── db/         # Room database
│   ├── model/      # DTO models
│   └── repository/ # Repository implementations
├── domain/         # Business logic
│   ├── model/      # Domain models
│   ├── repository/ # Repository interfaces
│   ├── usecase/    # Use Cases
│   └── exception/  # Domain exceptions
└── presentation/   # UI layer
    ├── screen/     # Screens & ViewModels
    ├── theme/      # Theme & styles
    └── MainActivity
```

## Testing

The project includes **unit tests** for:
- Data sources (Remote / Local)
- Repository layer
- Use Cases
- Core scenarios and edge cases

Libraries used: **MockK** (mocks) and **Google Truth** (assertions).

## Key Features

- **Adaptive design** – Works in portrait and landscape modes
- **Interactive charts** – User can zoom and pan
- **Data sorting** – Points are ordered by X coordinate
- **Error handling** – Input validation, network and server error handling
- **Clean Architecture** – Clear separation into data/domain/presentation layers
- **Local caching** – Request history stored in Room DB
- **History management** – View and delete requests
- **Swipe-to-delete** – Remove entries with a left swipe
- **Animations** – Smooth UI transitions
- **Unit testing** – Core components covered by tests