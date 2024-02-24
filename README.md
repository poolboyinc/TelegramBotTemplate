# Telegram Bot Template with Spring and MySQL
This is a template project for building a Telegram bot using Java with the TelegramBots library, Spring framework, and MySQL database. The project is designed to provide a foundation for creating a simple Telegram bot with features like responding to button presses, sending collected data to a group chat, and storing user answers in a MySQL database.

## Features
 - Button Responses: The bot can respond to button presses by users.
 - Group Chat Messaging: It can send collected data to a group chat.
 - Database Interaction: Memorizes user answers and stores them in a MySQL database.

## Getting Started
 To get started with this template, follow these steps:

1.**Clone the Repository**: Clone this repository to your local machine using git clone https://github.com/poolboyinc/TelegramBotTemplate

2.**Set Up MySQL Database**: Create a MySQL database. You can use the provided schema.sql file in the src/main/resources directory to create the necessary tables. Make sure to configure the database connection in application.properties.

3.**Set Up Bot Token**: Obtain a bot token from the BotFather on Telegram and add it to application.properties.

4.**Build and Run**: Build the project using Maven (mvn clean install) and run it.

5.**Test Your Bot**: Start a conversation with your bot on Telegram and test its functionality.
