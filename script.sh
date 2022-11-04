#!bin/bash
./gradlew build -x test
sudo docker login
sudo bash ./redisOn.sh
sudo docker build --build-arg DEPENDENCY=build/dependency -t blackbean99/econovation_idp .
sudo docker push blackbean99/econovation_idp
sudo nohup docker run --rm --cap-add=NET_ADMIN -i -p 8080:8080 blackbean99/econovation_idp:latest > ./idp.log 2>&1 &