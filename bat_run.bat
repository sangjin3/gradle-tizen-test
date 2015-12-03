@echo on

START /wait gradlew.bat -q vmTest

START /wait gradlew.bat -q certTest

START /wait gradlew.bat -q nativeTest
START /wait gradlew.bat -q installNativeTest
START /wait gradlew.bat -q reinstallNativeTest

START /wait gradlew.bat -q webTest
START /wait gradlew.bat -q installWebTest

START /wait gradlew.bat -q sdbTest


