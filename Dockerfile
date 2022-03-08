FROM openjdk:16-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/com.example.avito-test/ /app/
WORKDIR /app/bin
CMD ["./avito-test"]