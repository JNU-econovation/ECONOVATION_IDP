#!bin/bash
./gradlew build -x test
sudo docker login
sudo bash ./redisOn.sh
sudo docker build --build-arg DEPENDENCY=build/dependency -t blackbean99/econovation_idp .
sudo docker push blackbean99/econovation_idp
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=dltjgus119 --name root mysql:8.0.17 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci > ./mysql.log 2>&1 &
sudo nohup docker run --rm --cap-add=NET_ADMIN -i -p 8080:8080 blackbean99/econovation_idp:latest > ./idp.log 2>&1 &