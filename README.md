# Flipper Android App [![Flipper App Status](https://github.com/Flipper-Zero/Flipper-Android-App/workflows/release/badge.svg)](https://github.com/Flipper-Zero/Flipper-Android-App/actions?query=workflow%3A%22release%22)

Mobile app to rule all Flipper's family

![ecco](https://user-images.githubusercontent.com/5871715/90445233-04748100-e0e8-11ea-9e74-19390b0acfb4.png)
## Module arch

```
├── app
├── core
├── bridge
├── modules
│   ├── sample1
│   ├── sample2
```

- `app` - Main application module with UI
- `core` - Core library with deps and utils
- `bridge` - Communication between android and Flipper
- `modules` - Community widget module

## Layer arch

```
├── app
│   ├── feature1
│   │   ├── di
│   │   ├── ui
│   │   │   ├── view <-- custom views
│   │   │   ├── data <-- screen specific data classes
│   │   ├── interactor
│   │   │   ├── data <-- layer specific data classes
│   │   ├── repository
│   │   │   ├── data <-- layer specific data classes
```

- `data` - Entity layer
- `view` - Presentation layer
- `interactor` - Domain layer
- `repository` - Data layer
