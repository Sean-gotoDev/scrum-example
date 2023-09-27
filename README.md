# Example retro API
## Tech
- Spring boot - Quick and easy to get up a restful service up and running
- gradle - cleaner code than Maven
- lombok - less boilerplate code
## Running
### Requires
 - Java
 - Gradle
 - IDE
 - curl or a REST client
### Run
``./gradlew bootRun``

### Unit tests
``gradlew clean test --info``

## Adding/Viewing Data
### Add new retro items
```
POST http://localhost:8080/retro
```
```json
{
    "name":"Retro 1",
    "summary":"First retro for the project",
    "date":"2023-11-10",
    "participants":["Ben", "Bob", "Jen"]
}
```

```json
{
    "name":"Retro 2",
    "summary":"Second retro for the project",
    "date":"2023-11-24",
    "participants":["Ben", "Jen"]
}
```

```json
{
    "name":"Retro Alt 1",
    "summary":"First retro for the other project",
    "date":"2023-11-10",
    "participants":["Zeb", "Ken", "Ann"]
}
```
### Get paged list or date list
```
Accept: application/xml or application/json
GET http://localhost:8080/retro?pageSize=4
GET http://localhost:8080/retro/date?filter=2023-11-10
```
### Create new feedback
```json
{
    "name": "Ben",
    "body": "Ipsum lorem",
    "type": "good"
}
```
### Update existing feedback
```json
{
    "name": "Ben",
    "body": "Ipsum lorem",
    "type": "bad"
}
```