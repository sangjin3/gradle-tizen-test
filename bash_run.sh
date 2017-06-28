#!/bin/bash

sdk_path=/home/rla1957/work/tizen-studio-2.0

for arg in wearable-3.0 mobile-3.0
do
    for arch in x86
    do
        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg -Parch=$arch vmTest

        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg -Parch=$arch certTest

        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  -Parch=$arch nativeTest
        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  -Parch=$arch installNativeTest
        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  -Parch=$arch reNativeTest

        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  -Parch=$arch webTest
        ./gradlew -q -Psdk_path=$sdk_path -Pplatform=$arg  -Parch=$arch installWebTest

        pkill -9 emulator-x86
        sleep 2
        pkill -9 emulator.sh
        sleep 2
        pkill -9 emulator
        sleep 3
    done
done
exit 0

