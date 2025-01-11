## Tabiat
![Tabiat Hero Image](screenshots/tabiat-show-case.png)

Gym Tracker with more than 800 exercise template and you can create your own custom exercise. Available on Android and iOS.

### Download

<a href="https://play.google.com/store/apps/details?id=com.unwur.tabiatmu&pcampaignid=web_share" target="_blank">
<img src="https://play.google.com/intl/en_gb/badges/static/images/badges/en_badge_web_generic.png" width=200 />
</a>

Exercise source from [Github Repo](https://raw.githubusercontent.com/anwar-pasaribu/free-exercise-db/main/dist/exercises.json)

### Tech Stacks
| Feature | Tech |
| ----------- | ----------- |
| Multiplatform UI |[Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)|
| Dependency Injection |[Koin](https://insert-koin.io/)|
| Storage | [SQLDelight](https://sqldelight.github.io/sqldelight/2.0.2/multiplatform_sqlite/), [Android Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) |
|Chart|[Chart](https://github.com/TheChance101/AAY-chart)|
|Pipeline|[CircleCI](https://circleci.com/) (for CI/CD), [Fastlane](https://fastlane.tools/) (auto publish to Play Store)
|App Analytics|[Firebase](https://firebase.google.com/)|
|Image Loader|[Coil](https://coil-kt.github.io/coil/) (multiplatform image loader)|
|Network|[Ktor Client](https://ktor.io/docs/client-create-new-application.html)|