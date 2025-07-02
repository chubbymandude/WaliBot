Project for Masjid; make a ChatBot that can make automated answers to possible questions that people might need to get from the Masjid

Gameplan:

Get the Data --> Determine possible questions people may need answers to --> Gather the Data into a spreadsheet/database --> Find a way to continuously update data over time without needing to perform rigorous manual updates

Create the application --> Integrate OpenAI to Java --> Integrate data to Java --> Find a way to get the AI to only parse through our data, rather than simply output whatever is in its database --> Find a way to get ChatGPT to provide a formal response so that it can be used as a natural voice model

Integrate to Phone Number --> Find a way to connect Java backend with the frontend (phone line) --> Find a way to convert text to speech in a natural voice --> Find a way to maintain this connection without requiring too much computer power

UPDATE 7/1/2025: Have finished ChatBot functionality

Considering Twilio for phone call functionality.

Need to use Speech-To-Text & Text-To-Speech functionality in order to properly make the application.

Need to run a server with SpringBoot (have already converted project to a Maven project).
