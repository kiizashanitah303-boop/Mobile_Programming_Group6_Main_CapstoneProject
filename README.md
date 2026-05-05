# [WeatherWise App] + [https://youtu.be/9cCInAvCcs8]
Team Roster;
Kiiza K Shanitah 24/1/314/D/513 Documentation lead,UI/UX Specialist
Lugemwa Isaac 24/1/306/D/588 Lead developer,Git manager,project lead

Feature Set;
Real time wether data from openweather API
Offline-first caching with Room Database(30mins cache)
Animted splash screen(pulsing icon,rotation,fade-in-text)
Current local time and date with animated clock
Animated temperature counting up/down on refresh
Weather conditions icons with condition-based animations(sun rotates,clouds float)
Dark/light theme with smooth transition
Dynamic grdient background bsed on weather(sunny,cloudy,rainy,night)
Offline indicator with pulsing orange badge
save/delete favourite cities
pull to refresh with rotating icon
error handling with shake animation on search bar
friendly weather messages(eg "perfect day to go outside")
MVVM rchitecture with clean architecture principles
Dependecy injection using dagger hilt
Responsive UI with jetpack Compose Material 3.

techstack
cartegory                  Libraries/tools
Language                   Kotlin
UI Toolkit                 Jetpack Compose(material 3)
Architecture               MVVM + Clean Architecture
Networking                 Retrofit 2, OkHttp,Gson converter
database                   Room(SQLite) with Flow support
Asychronous                Kotlin Coroutines + Flow
Dependecy injection        Dagger Hilt
Image loading              Coil Compose
Navigation                 jetpack navigation Compose
Animations                 Compose animations API (AnimateFloatAsState,InfiniteTransition)
System UI                  Accompanist SystemUIController
Testing                    Junit,AndroidX Test

QA Summmary
| ID Range | Feature Area              | Summary of Tests                                               | Status |
| -------- | ------------------------- | -------------------------------------------------------------- | ------ |
| TC-01    | App Launch                | Splash screen displays with animations                         | ✅ PASS |
| TC-02–03 | Weather Loading & Search  | Default weather loads and updates correctly for valid searches | ✅ PASS |
| TC-04    | Error Handling            | Invalid city shows error message with animation                | ✅ PASS |
| TC-05–06 | UI Interaction            | Dark mode toggle and refresh animations work correctly         | ✅ PASS |
| TC-07–08 | Offline Functionality     | Cached data shown offline; proper error when no cache          | ✅ PASS |
| TC-09–10 | Favorites Management      | Cities can be saved and deleted successfully                   | ✅ PASS |
| TC-11    | Data Caching              | Cache expires correctly and refreshes after 30 minutes         | ✅ PASS |
| TC-12    | Dynamic UI                | Background changes based on weather conditions                 | ✅ PASS |
| TC-13–14 | Live Updates & Animations | Clock updates in real time; temperature animates smoothly      | ✅ PASS |
| TC-15    | Configuration Handling    | App retains state during screen rotation                       | ✅ PASS |



