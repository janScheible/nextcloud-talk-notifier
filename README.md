# nextcloud-talk-notifier

Currently Nextcloud Talk only supports notifications when using the Google Play Store version.
The Play Store version uses Google Firebase Notifications which does not work without Google Play Services.
There is some controversy going on about how to fix that in [Feature request: support notifications for f-droid version
#257](https://github.com/nextcloud/talk-android/issues/257).

To get notifications on devices without Google Play Store this repository contains a Docker Compose file with [Gotify](https://gotify.net/) as generic notification server and the Java application nextcloud-talk-notifier for polling Nextcloud Talk and sending notifications via Gotify.

Gotify is designed to be exposed to the Internet via a (sub) domain. Let's Encrypt certificate creation is configured in the Docker Compose file.

## Usage

1. build the nextcloud-talk-notifier image (see section [Building nextcloud-talk-notifier](#building-nextcloud-talk-notifier))
1. replace `notify.my-domain.com` of `GOTIFY_SERVER_SSL_LETSENCRYPT_HOSTS` environment variable in the `docker-compose.yml` with your (sub) domain
1. make sure that the host exposes port 80 and 443 to the Internet
1. `docker compose up -d`
1. log into Gotify via HTTPS on your (sub) domain
    1. change admin password
    1. create a new user with the same name as the Nextcloud Talk user
    1. create a new app called 'Nextcloud Talk'
    1. note down the token of the app
1. install the [Gotify Android app](https://github.com/gotify/android) and configure your Gotify URL with the created Gotify
1. create an `application.properties` file:
        
    ```properties
    # polling interval (every minute)
    #   e.g. every 10 seconds = '*/10 * * * * *'
    #   e.g. every 5 minutes = '0 */5 * ? * *'    
    notifier.poller.cron=0 */1 * ? * *

    notifier.client.talk.base-url=https://nextcloud.com
    notifier.client.talk.passwords.username=DEC(nextcloud-talk-user-password)

    notifier.client.gotify.app-tokens.username=DEC(nextcloud-talk-gotify-app-token)

    # if defaults have to be adjusted
    notifier.http.client.connect-timeout=5s
    notifier.http.client.read-write-timeout=5s
    ```
    The `username` of `notifier.client.talk.passwords` and `notifier.client.gotify.app-tokens` have to match. 
    Multiple users can be configured.
    The username has to be the one of Nextcloud Talk.
1. pick a password that will be use to encrypt the Nextcloud Talk user passwords and Gotify app tokens in the `application.properties`
    1. replace `password` of `JASYPT_ENCRYPTOR_PASSWORD` environment variable in the `docker-compose.yml` with your password
    1. run `mvn com.github.ulisesbocchio:jasypt-maven-plugin:3.0.4:encrypt -Djasypt.encryptor.password=your-password` to encrypt the properties enclosed by `DEC(...)` in the `application.properties`
1. finally `docker compose restart nextcloud-talk-notifier` to apply the `application.properties`

`docker compose logs nextcloud-talk-notifier -f -t` can be used to see if the polling and notification sending works as expected.


## Building nextcloud-talk-notifier

The application can either be used as regular containerized Java or a Native Image application.

### Regular containerized application

To build the regular Java container Java 17 needs to be installed.

```bash
bash ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName='janscheible/nextcloud-talk-notifier:1.0.4'
```

### Native Image application

Spring Boot 3 contains everything needed for Native Image applications.

```bash
docker build -t janscheible/nextcloud-talk-notifier:1.0.4 .
```

It works more or less with a standard Spring Boot application with a few additional things (that do not affect the application when executed regularly in a JVM):

1. `JacksonTypeHints` and `JasyptTypeHints` to make reflection work on the DTOs and the Jasypt classes
2. `reflect-config` that contains also [reflection config of the upcoming OkHttp 5.0](https://github.com/square/okhttp/blob/parent-5.0.0-alpha.10/okhttp/src/jvmMain/resources/META-INF/native-image/okhttp/okhttp/reflect-config.json) to make the used Kotlin classes work
3. light-weight drop-in replacement for `com.github.ulisesbocchio:jasypt-spring-boot-starter` in `JasyptDecryptEnvironmentPostProcessor` because the starter did not work with Native Image (all properties disappeared)