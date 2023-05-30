# 开始构建
FROM ghcr.io/graalvm/graalvm-ce:ol9-java17 As builder
USER root
RUN microdnf install -y findutils
RUN mkdir -p /code
COPY ./ /code
WORKDIR /code
ARG maven_repo
RUN ./gradlew nativeCompile
#  构建完毕

# 开始运行
FROM ubuntu:latest as runner
#COPY ./target/app.jar /home/app.jar
COPY --from=builder /code/build/native/nativeCompile/vertx-server-push /home/app
RUN apt update &&  apt install dumb-init -y
ENTRYPOINT ["dumb-init", "--"]
CMD /home/app
