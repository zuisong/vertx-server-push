# 开始构建
FROM zuisong-docker.pkg.coding.net/mirrors/docker/maven:3-jdk-11-slim As builder
MAINTAINER zuisong
USER root
RUN mkdir -p /code
COPY . /code
WORKDIR /code
RUN  mvn clean package -D finalName=app
#  构建完毕

# 开始运行
FROM zuisong-docker.pkg.coding.net/mirrors/docker/adoptopenjdk/openjdk11:slim
MAINTAINER zuisong
VOLUME /data
WORKDIR /data
COPY --from=builder /code/target/app.jar /home/app.jar
EXPOSE 13000
CMD ["java","-jar","/home/app.jar"]
