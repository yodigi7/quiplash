# Quiplash Replacement
First order of business is to come up with a good name, any ideas?

## Project Explained
So as you guys should know by the current title this is meant to be a free open source version of quiplash. The overall architecture is
a backend that keeps track of all the game data. It then supplies that data to two frontends, one of which is the main/common screen that
all players use in the game. The other is the view for each individual contender to enter their specific data.
I currently have it setup in 3 sections:

### Backend
Purpose: supplies REST API services and keeps track of the game data
* written in Java
* Using SpringMVC and JPA
* Stores the data in an H2 in-memory database.

### GameMaster
Purpose: Showing the main/common screen
* written in Vue
* uses axios for HTTP REST API calls
* shows the lobby as people join
* shows the answers to vote on
* shows the voting results
* shows the final game results

### Contender
Purpose: The view for each of the contenders
* written in Vue
* uses axios for HTTP REST API calls
* shows the prompts for answers
* allows voting for answers

## Prerequisites
* Java
* npm
* node
* axios

## Running in the Dev environment
Currently the only available environment

### Backend
Run the Backend first to get it to run on port 8080 for easier setup with the two frontends. To run just load the project into an IDE
such as IntelliJ with the project SDK setup and hit run to run the main file 
(Backend/src/main/java/com/yodigi/quiplash/QuiplashApplication.java).

### GameMaster
Setup whatever access point the location will be for the backend. It will likely be `http://localhost:8080` and set the .env.dev file
`VUE_APP_BACKEND_BASE_URL` to that value.

From the main folder (QuiplashGameMaster) then run the the following command in the commandline:

`npm run serve -- --mode dev`

Then to access the resulting frontend it will display in the console the port but it will most likely be `http://localhost:8081`

### Contender
Setup whatever access point the location will be for the backend. It will likely be `http://localhost:8080` and set the .env.dev file
`VUE_APP_BACKEND_BASE_URL` to that value.

From the main folder (QuiplashGameMaster) then run the the following command in the commandline:

`npm run serve -- --mode dev`

Then to access the resulting frontend it will display in the console the port but it will most likely be `http://localhost:8082`

## Running the tests
Tests and this section are in TODO status.

## Contributing
Feel free to help contribute to this project in any way, be sure to check out open issue and contact me to help get you up and running
if you are a beginner.

## Authors
yodigi7 - Anthony Buchholz

## License
GPLv3
