#!/bin/bash

# same as "./gradlew vT"
./gradlew -q vmTest

./gradlew -q certTest

./gradlew -q nativeTest
./gradlew -q installNativeTest
./gradlew -q reNativeTest

./gradlew -q webTest
./gradlew -q installWebTest



./gradlew -q sdbTest

exit 0
