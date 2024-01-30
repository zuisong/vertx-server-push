# 开始构建
FROM vegardit/graalvm-maven:21.0.2 As builder
USER root
RUN mkdir -p /code
COPY ./ /code
WORKDIR /code
ARG maven_repo
RUN mvn clean compile native:build -Pnative
#  构建完毕

# 开始运行
FROM ubuntu:latest as runner
#COPY ./target/app.jar /home/app.jar
COPY --from=builder /code/target/vertx-server-push /home/app
RUN apt update &&  apt install dumb-init -y
ENTRYPOINT ["dumb-init", "--"]
CMD /home/app
