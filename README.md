# Flipper Android App [![Flipper App Status](https://github.com/flipperdevices/Flipper-Android-App/actions/workflows/internal.yml/badge.svg)](https://github.com/Flipper-Zero/Flipper-Android-App/releases) [![Discord](https://img.shields.io/discord/740930220399525928.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](http://flipperzero.one/discord)

[<img width="300" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/>](https://play.google.com/store/apps/details?id=com.flipperdevices.app)

Mobile app to rule all Flipper's family

![dolphin-dark](https://user-images.githubusercontent.com/5871715/163869541-33904d20-7684-4891-abf9-be0a0c1afe71.png#gh-dark-mode-only)
![dolphin-light](https://user-images.githubusercontent.com/5871715/163869555-fe5b029d-c4bd-4a29-92b7-fc9c79505106.png#gh-light-mode-only)


## Module arch

```
├── instances
│   ├── app
├── components
│   ├── core
│   ├── bridge
│   ├── feature1
│   ├── feature2
```

- `app` - Main application module with UI
- `components/core` - Core library with deps and utils
- `components/bridge` - Communication between android and Flipper
- `components/*` - Features modules, which connect to root application
