FROM ghcr.io/graalvm/graalvm-ce:ol9-java17-22 as build-stage
WORKDIR /app/
COPY . /app
RUN bash ./mvnw -Pnative native:compile

FROM ubuntu:22.04
WORKDIR /app/
COPY --from=build-stage /app/target/nextcloud-talk-notifier ./
ENTRYPOINT ["./nextcloud-talk-notifier"]
