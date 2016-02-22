#!/bin/bash

sdk_path=/home/dkyun77/tizen-sdk_24_rev4
#sdk_path=/Users/

#for arg in wearable-2.3.1 mobile-2.3 mobile-2.3.1 mobile-2.4
for arg in mobile-2.4
do
    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg vmTest

    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg certTest

    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  nativeTest
    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  installNativeTest
    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  reNativeTest

    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  webTest
    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  installWebTest

    ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  sdbTest

    pkill -9 emulator-x86
    sleep 2
    pkill -9 emulator.sh
    sleep 2
    pkill -9 emulator
    sleep 3
done
exit 0

