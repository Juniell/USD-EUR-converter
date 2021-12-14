# USD to EUR converter

![master_branch_testing](https://github.com/Juniell/USD-EUR-converter/actions/workflows/testingGradle.yml/badge.svg?branch=master)
![develop_branch_testing](https://github.com/Juniell/USD-EUR-converter/actions/workflows/testingGradle.yml/badge.svg?branch=develop)

## Description

This service allows you to convert USD to EUR.
Integration with the openexchangerates.org service was carried out to get the current exchange rate.

## Communication with the service

### Request format:
```
http://127.0.0.1:8888/api/convert?amount=your_amount
```
If the negative value `your_amount` is specified, the service will display the current USD / EUR rate.

### Examples of valid requests:
```
http://127.0.0.1:8888/api/convert?amount=25
http://127.0.0.1:8888/api/convert?amount=-25
```

## Before starting

For the service to work correctly, before starting the project, you need to get a token [here](https://openexchangerates.org/)
and place it in the `token.txt` file in the project folder.

## Service start
```
./gradlew build
java -jar USD-EUR-converter-1.0-SNAPSHOT.jar
```

## Running in Docker

Starting the build process:
```
docker build -t converter .
```
Starting the project:
```
docker run -d -p 8888:8888 converter
```