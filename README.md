# Flipper Android App [![Flipper App Status](https://github.com/flipperdevices/Flipper-Android-App/actions/workflows/internal.yml/badge.svg)](https://github.com/Flipper-Zero/Flipper-Android-App/releases) [![Discord](https://img.shields.io/discord/740930220399525928.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](http://flipperzero.one/discord)

Mobile app to rule all Flipper's family

![dolphin-dark](.github/dark_theme_banner.png#gh-dark-mode-only)
![dolphin-light](.github/light_theme_banner.png#gh-light-mode-only)

## Download


[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=com.flipperdevices.app)
[<img height="80" alt='Get it on F-Droid' src='https://gitlab.com/fdroid/artwork/-/raw/master/badge/get-it-on-en.png'/>](https://f-droid.org/en/packages/com.flipperdevices.app/)

Or get the app from the [Releases Section](https://github.com/flipperdevices/Flipper-Android-App/releases/latest).

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
