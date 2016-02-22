@echo on

set sdk_path="C:/tizen-sdk_24_rev4"
set var=mobile-2.4 mobile-2.3.1 mobile-2.3 wearable-2.3.1

for %%i in (%var%) do (

    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i vmTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i certTest

    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i nativeTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i installNativeTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i reNativeTest

    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i webTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i installWebTest

    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i sdbTest

    taskkill /f /im "emulator-x86.exe"
    taskkill /f /im "emulator-x86.exe"
    timeout /t 10
)
