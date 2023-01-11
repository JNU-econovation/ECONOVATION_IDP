#/bin/sh
./gradlew build -x test
sudo docker build --build-arg  DEPENDENCY=build/dependency -t blackbean99/econovation_idp .