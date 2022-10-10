./gradlew build -x test
sudo docker login
sudo docker build --build-arg DEPENDENCY=build/dependency -t blackbean99/t_econo .
sudo docker push blackbean99/econovation_idp:zenbook
sudo docker run --cap-add=NET_ADMIN -it -p 8080:8080 blackbean99/econovation_idp:latest