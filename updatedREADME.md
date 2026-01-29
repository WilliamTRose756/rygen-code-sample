# Project Runbook

## Prereqs
- Node.js (for the Vue frontend)
- Java 17 (for the Spring Boot backend)

## Install
Frontend dependencies:
```sh
cd light-controller-ui
npm install
```

Backend dependencies:
```sh
./gradlew build
```

## Run the app
Run frontend + backend together:
```sh
cd light-controller-ui
npm run dev:all
```

Run frontend only:
```sh
cd light-controller-ui
npm run dev
```

Run backend only:
```sh
./gradlew bootRun
```

## Tests
Frontend tests:
```sh
cd light-controller-ui
npm test
```

Backend tests:
```sh
./gradlew test
```

