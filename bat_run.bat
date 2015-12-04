@echo on

set var=mobile-2.3 wearable-2.3 wearable-2.3.2

for %%i in (%var%) do (

    call gradlew.bat -q -Pplatform=%%i vmTest
    call gradlew.bat -q -Pplatform=%%i certTest

    call gradlew.bat -q -Pplatform=%%i nativeTest
    call gradlew.bat -q -Pplatform=%%i installNativeTest
    call gradlew.bat -q -Pplatform=%%i reNativeTest

    call gradlew.bat -q -Pplatform=%%i webTest
    call gradlew.bat -q -Pplatform=%%i installWebTest

    call gradlew.bat -q -Pplatform=%%i sdbTest

    taskkill /f /im "emulator-x86.exe"
    taskkill /f /im "emulator-x86.exe"
    timeout /t 10
)
