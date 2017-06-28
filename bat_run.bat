@echo on

set sdk_path="C:/tizen-studio-2.0"
set var=mobile-3.0 wearable-3.0
set arch=x86

for %%i in (%var%) do (

for %%j in (%arch%) do (
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j vmTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j certTest

    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j nativeTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j installNativeTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j reNativeTest

    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j webTest
    call gradlew.bat -q -Psdk_path=%sdk_path% -Pplatform=%%i -Parch=%%j installWebTest

    taskkill /f /im "emulator-x86_64.exe"
    taskkill /f /im "emulator-x86_64.exe"
    timeout /t 10
)
)
