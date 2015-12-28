#!/bin/bash

#for arg in wearable-2.3.1 wearable-2.3.2 mobile-2.3 mobile-2.3.1 mobile-2.4
for arg in mobile-2.4
do
    ./gradlew -q -Pplatform=$arg vmTest

    ./gradlew -q -Pplatform=$arg certTest

    ./gradlew -q -Pplatform=$arg  nativeTest
    ./gradlew -q -Pplatform=$arg  installNativeTest
    ./gradlew -q -Pplatform=$arg  reNativeTest

    ./gradlew -q -Pplatform=$arg  webTest
    ./gradlew -q -Pplatform=$arg  installWebTest

    ./gradlew -q -Pplatform=$arg  sdbTest

    pkill -9 emulator-x86
    sleep 2
    pkill -9 emulator.sh
    sleep 2
    pkill -9 emulator
    sleep 3
done
exit 0

