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
- Запрашивать у сервера определённое количество координат точек (x, y)
- Отображать полученные данные в виде таблицы и интерактивного графика
- Сохранять историю запросов с возможностью просмотра и удаления
- Работать в портретной и ландшафтной ориентации

## Технологический стек

- **Jetpack Compose** - Современный UI framework для Android
- **Vico** - Библиотека для отображения интерактивных графиков
- **Hilt** - Dependency Injection для упрощения управления зависимостями
- **Room** - Локальная база данных для сохранения истории запросов
- **Retrofit + OkHttp** - Сетевые запросы с логированием
- **Kotlin Coroutines** - Асинхронное программирование
- **Navigation3** - Современная навигация для Compose
- **Kotlin Serialization** - Сериализация JSON

**Примечания:**

*В тестовом задании рекомендовалось использовать классическую View систему, однако выбор Compose обоснован современными требованиями разработки, лучшей производительностью, декларативностью, легкой поддержкой адаптивных интерфейсов и будущей поддержкой проекта.*

*Vico выбрана как библиотека для графиков благодаря нативной поддержке Compose, высокой производительности, встроенной интерактивности (зум, панорамирование) и отличной интеграции с Material Design 3.*

## Архитектура

Проект следует принципам **Clean Architecture** и **MVVM**:

```
app/
├── data/           # Слой данных
│   ├── api/        # API интерфейсы
│   ├── datasource/ # Источники данных (remote/local)
│   ├── db/         # База данных Room
│   ├── model/      # DTO модели
│   └── repository/ # Реализации репозиториев
├── domain/         # Бизнес-логика
│   ├── model/      # Domain модели
│   ├── repository/ # Интерфейсы репозиториев
│   ├── usecase/    # Use Cases
│   └── exception/  # Доменные исключения
└── presentation/   # UI слой
    ├── screen/     # Экраны и ViewModels
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
- `PointApi` - интерфейс для работы с REST API
- `PointLocalDataSource` - работа с локальной базой данных
- `PointRemoteDataSource` - работа с удаленным API
- `PointProviderRepositoryImpl` - реализация репозитория

#### Domain Layer
- `PointProviderRepository` - интерфейс репозитория
- Use Cases: `RequestPointsBatchUseCase`, `GetPointsBatchUseCase`, `DeletePointBatchUseCase`, `ObserveBatchesUseCase`
- Models: `PointBatch`, `BatchInfo`

#### Presentation Layer
- `MainViewModel` - управление состоянием главного экрана
- `ResultViewModel` - управление состоянием экрана результатов
- Compose UI компоненты для отображения интерфейса
