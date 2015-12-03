@echo on

call gradlew.bat -q vmTest

call gradlew.bat -q certTest

call gradlew.bat -q nativeTest
call gradlew.bat -q installNativeTest
call gradlew.bat -q reNativeTest

call gradlew.bat -q webTest
call gradlew.bat -q installWebTest

call gradlew.bat -q sdbTest


