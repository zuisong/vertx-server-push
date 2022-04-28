# 开始构建
FROM openjdk:11-slim As builder
USER root
RUN mkdir -p /code
COPY ./ /code
WORKDIR /code
ARG maven_repo
RUN ./gradlew shadowjar
#  构建完毕

# 开始运行
FROM openjdk:11-slim as runner
#COPY ./target/app.jar /home/app.jar
COPY --from=builder /code/build/libs/*-all.jar /home/app.jar
RUN apt update &&  apt install dumb-init -y
ENTRYPOINT ["dumb-init", "--"]
CMD  "java" $JAVA_OPTS -jar /home/app.jar
