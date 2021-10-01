# Flipper Android App [![Flipper App Status](https://github.com/Flipper-Zero/Flipper-Android-App/workflows/release/badge.svg)](https://github.com/Flipper-Zero/Flipper-Android-App/releases) [![Discord](https://img.shields.io/discord/740930220399525928.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](http://flipperzero.one/discord)

Mobile app to rule all Flipper's family

![ecco](https://user-images.githubusercontent.com/5871715/90445233-04748100-e0e8-11ea-9e74-19390b0acfb4.png)
## Module arch

```
├── instances
│   ├── app
├── components
│   ├── core
│   ├── bridge
│   ├── feature1
│   ├── feature2
```

- `app` - Main application module with UI
- `components/core` - Core library with deps and utils
- `components/bridge` - Communication between android and Flipper
- `components/*` - Features modules, which connect to root application
