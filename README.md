# PointGrapher

Android приложение для запроса координат точек с сервера и их отображения в виде таблицы и графика.

<img width="210" alt="Screenshot 2025-06-24 at 15 24 09" src="https://github.com/user-attachments/assets/0e0ee5a0-d353-4970-83af-81213070998a" />
<img width="213" alt="Screenshot 2025-06-24 at 15 25 12" src="https://github.com/user-attachments/assets/3b745f0f-08c6-4adb-b1a0-b58a5083d117" />
<img width="212" alt="Screenshot 2025-06-24 at 15 26 29" src="https://github.com/user-attachments/assets/37450244-88ef-41b4-8eaa-b24df173d959" />
<br/><br/>
<img width="210" alt="Screenshot 2025-06-24 at 15 24 31" src="https://github.com/user-attachments/assets/af201ed7-5199-4ade-9834-2adc8a9851d4" />
<img width="212" alt="Screenshot 2025-06-24 at 15 26 52" src="https://github.com/user-attachments/assets/758a94fc-39fa-428f-a5ab-90e0db8d070b" />
<img width="209" alt="Screenshot 2025-06-24 at 15 27 10" src="https://github.com/user-attachments/assets/aea0e56f-871c-4eee-b29d-7fd24c7c31c2" />

## Описание

Приложение позволяет:
- **Выбирать UI фреймворк** - современный Jetpack Compose или классический XML/View система
- Запрашивать у сервера определённое количество координат точек (x, y) с валидацией ввода (1-1000 точек)
- Отображать полученные данные в виде **интерактивной таблицы** и **масштабируемого графика**
- **Сохранять графики в галерею** в формате PNG с автоматическим именованием
- **Управлять историей запросов** с возможностью просмотра, повторного открытия и удаления
- Работать в **портретной и ландшафтной ориентации** с адаптивными макетами
- **Swipe-to-delete** для быстрого удаления записей из истории
- **Автоматическую сортировку точек** по возрастанию координаты X

## Технологический стек

### UI Frameworks
- **Jetpack Compose** - Современный декларативный UI с Material Design 3
- **XML/View System** - Классический императивный подход для совместимости

### Core Libraries
- **Vico** - Интерактивные графики для Compose с поддержкой зума и панорамирования
- **MPAndroidChart** - Графики для XML с расширенными возможностями кастомизации
- **Hilt** - Dependency Injection для упрощения управления зависимостями
- **Room** - Локальная база данных для персистентного хранения истории
- **Retrofit + OkHttp** - Сетевые запросы с автоматическим логированием и обработкой ошибок
- **Kotlin Coroutines** - Асинхронное программирование и reactive streams
- **Navigation3** - Современная типобезопасная навигация для Compose
- **Kotlin Serialization** - Efficient JSON сериализация без reflection

### Testing
- **JUnit 5** - Современный testing framework
- **MockK** - Mocking library для Kotlin
- **Turbine** - Testing utilities для Flow
- **Google Truth** - Fluent assertion library

**Примечания по выбору технологий:**

*В тестовом задании рекомендовалось использовать классическую View систему, однако выбор Compose обоснован современными требованиями разработки, лучшей производительностью, декларативностью, легкой поддержкой адаптивных интерфейсов и будущей поддержкой проекта. Для совместимости реализованы оба подхода.*

*Vico выбрана как библиотека для графиков благодаря нативной поддержке Compose, высокой производительности, встроенной интерактивности (зум, панорамирование) и отличной интеграции с Material Design 3.*

## Архитектура

Проект следует принципам **Clean Architecture** и **MVVM**:

```
app/
├── data/           # Слой данных
│   ├── api/        # API интерфейсы (Retrofit)
│   ├── datasource/ # Источники данных (remote/local)
│   ├── db/         # База данных Room (entities, dao)
│   ├── model/      # DTO модели для сериализации
│   └── repository/ # Реализации репозиториев
├── domain/         # Бизнес-логика
│   ├── model/      # Domain модели (чистые от фреймворков)
│   ├── repository/ # Интерфейсы репозиториев
│   ├── usecase/    # Use Cases для бизнес-операций
│   └── exception/  # Доменные исключения
└── presentation/   # UI слой
    ├── onboarding/ # Выбор UI фреймворка
    ├── main/       # Главный экран (Compose + XML)
    ├── result/     # Экран результатов (Compose + XML)
    ├── composeui/  # Compose-specific компоненты
    ├── theme/      # Темы и стили
    └── MainActivity
```

## Тестирование

Проект включает unit тесты для:
- Data Sources (Remote/Local)
- Repository
- Use Cases
- Покрытие основных сценариев и edge cases
- Использование MockK для моков и Google Truth для ассертов

## Особенности реализации

- **Адаптивный дизайн** - Работа в портретной и ландшафтной ориентации экрана
- **Интерактивный график** - Поддержка изменения масштаба пользователем
- **Сортировка данных** - Точки следуют по возрастанию координаты X
- **Обработка ошибок** - Валидация ввода, обработка сетевых ошибок и ошибок сервера
- **Clean Architecture** - Разделение на слои data/domain/presentation для лучшей тестируемости
- **Локальное кэширование** - Сохранение истории всех запросов в Room базе данных
- **Управление историей** - Просмотр и удаление предыдущих запросов
- **Swipe-to-delete** - Удаление записей из истории свайпом влево
- **Анимации** - Плавные переходы и анимированные элементы интерфейса
- **Unit тесты** - Покрытие тестами основных компонентов приложения

## Структура проекта

#### Data Layer
- `PointApi` - интерфейс для работы с REST API endpoints
- `PointLocalDataSource` - работа с локальной базой данных Room
- `PointRemoteDataSource` - работа с удаленным API и обработка HTTP ошибок
- `PointProviderRepositoryImpl` - реализация репозитория, координирующая local/remote источники
- `UIFrameworkDataSource` - управление пользовательскими предпочтениями UI
- `ImageSaverRepositoryImpl` - сохранение изображений графиков в галерею

#### Domain Layer
- `PointProviderRepository` - интерфейс репозитория для работы с данными точек
- `UIFrameworkRepository` - интерфейс для управления выбором UI фреймворка
- `ImageSaverRepository` - интерфейс для сохранения изображений
- Use Cases: `RequestPointsBatchUseCase`, `GetPointsBatchUseCase`, `DeletePointBatchUseCase`, `ObserveBatchesUseCase`, `ValidatePointCountUseCase`, `SaveChartImageUseCase`, `SetUIFrameworkUseCase`, `GetUIFrameworkUseCase`, `ClearUIFrameworkUseCase`
- Models: `PointBatch`, `BatchInfo`, `ValidationResult`, `UIFramework`
- Exceptions: `BatchNotFoundException`, `NetworkError`

#### Presentation Layer
- **App** - Application класс с Hilt и Timber инициализацией
- **Onboarding**:
    - `OnboardingActivity` - выбор UI стиля при первом запуске
    - `OnboardingViewModel` - логика навигации между UI фреймворками
- **Main Screen**:
    - `MainViewModel` - управление состоянием, запросами и историей батчей
    - **Compose UI**: `MainScreen`, `MainScreenTextField`, `MainScreenBatchList`, `MainScreenErrorSection`, `MainScreenGoButton`, `BounceArrowIcon`
    - **XML UI**: `MainActivityXml`, `BatchListAdapter` с поддержкой swipe-to-delete
- **Result Screen**:
    - `ResultViewModel` - управление состоянием результатов и сохранением графиков
    - **Compose UI**: `ResultScreen`, `ResultGraph` с Vico charts, `ResultScrollableTable`, `ResultErrorState`, `ResultLoadingState`
    - **XML UI**: `ResultActivityXml`, `GraphManager` с MPAndroidChart, `PointsTableAdapter`
- **Navigation**: `NavScreen` sealed interface для типобезопасной навигации в Compose
- **Theme**: Material Design 3 темы для светлого/темного режимов