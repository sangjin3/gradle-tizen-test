ABOUT
=====

* Tizen SDK의 CLI ('tizen', 'sdb')를 자동으로 테스트할 수 있는 툴이다.
* Tizen SDK가 Profile별로 full 설치되어 있어야 한다 (Emulator, Native IDE, Web IDE)
* Gradle로 작성되었으며 SDK가 실행될 수 있느 환경이면 테스트가 가능하다


FEATURES
========

* 'tizen' CLI의 기본 기능을 테스트 후 결과를 화면에 출력한다
  * 프로파일별 모든 Template App의 생성/빌드/패키징/설치/실행/삭제를 테스트 한다
* 'sdb'의 기본 기능을 테스트 후 결과를 화면에 출력한다
* 테스트를 위해 'em-cli'를 이용해서 Emulator를 생성 후 실해한다.

USAGE
=====

아래 스크립트/배치 파일을 수정 후 실행하면된다
* Linux/Mac의 경우 아래 파일의 sdk_path 부분에 Tizen SDK의 설치 위치를 설정한다
  * bash_run.sh
* Windows의 경우 아래 파일의 sdk_path 부분에 Tizen SDK의 설치 위치를 설정한다
  * bat_run.bat
  * 참고로 윈도우즈에서 테스트를 편하게 하기 위해서 standalone git-bash를 설치 후 사용한다
  * https://git-for-windows.github.io/

EXAMPLE
=======

platform은 mobile-3.0/wearable-3.0 이고 target architecture가 x86 일 때

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
