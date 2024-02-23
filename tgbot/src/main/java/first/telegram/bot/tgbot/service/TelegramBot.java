package first.telegram.bot.tgbot.service;

import first.telegram.bot.tgbot.config.BotConfig;
import first.telegram.bot.tgbot.entity.Product;
import first.telegram.bot.tgbot.entity.User;
import first.telegram.bot.tgbot.repository.ProductRepository;
import first.telegram.bot.tgbot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.regex.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private enum ConversationState {
        ASK_PIZZA_OR_PASTA,
        ASK_NAME,
        ASK_ADDRESS
    }

    @Autowired
    private ProductRepository product;

    @Autowired
    private UserRepository user;

    final BotConfig config;


    Map<Long, ConversationState> conversationStates = new HashMap<>();

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            switch (message_text) {
                case "/menu":
                    menu_function();
                    break;
                case "/start":
                    start_function(chat_id);
                    break;
                default:

                    handleIncomingMessage(update.getMessage());
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void menu_function() {
        SendMessage message = new SendMessage();
        message.setChatId(config.getMenu_id());
        message.setText("Click this button to order: ");

        InlineKeyboardButton orderButton = new InlineKeyboardButton();
        orderButton.setText("Order here!");
        orderButton.setUrl("https://t.me/poolboibot?start");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(List.of(orderButton)));

        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void start_function(long chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("Hello there, I will be handling your order info\n For starters click on the store you'd like to order from.");

        List<Product> product_objects = (List<Product>) product.findAll();

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Product product : product_objects) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton theButton = new InlineKeyboardButton();

            if (product.getEnabled() == 0) {
                theButton.setText(product.getName() + "\uD83D\uDD34");
            } else if (product.getEnabled() == 1) {
                theButton.setText(product.getName() + "\uD83D\uDFE1");
            } else theButton.setText(product.getName() + "\uD83D\uDFE2");

            theButton.setCallbackData(product.getName());

            rowInline.add(theButton);
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        executeMessage(message);
    }

    private void handleCallbackQuery(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String username = callbackQuery.getFrom().getUserName();
        ConversationState currentState = conversationStates.getOrDefault(chatId, null);

        user.findByChatId(chatId).ifPresentOrElse(

                user -> { },

                () -> {
                    User newUser = new User();
                    newUser.setChat_id(chatId);
                    if(username!=null){ newUser.setUsername(username); }
                    user.save(newUser);
                }
        );


        if (currentState == null) {
            conversationStates.put(chatId, ConversationState.ASK_PIZZA_OR_PASTA);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Great now choose one of the options:");

            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton pickupButton = new KeyboardButton("Pizza");
            KeyboardButton deliveryButton = new KeyboardButton("Pasta");
            keyboardRow.add(pickupButton);
            keyboardRow.add(deliveryButton);

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setKeyboard(List.of(keyboardRow));
            keyboardMarkup.setOneTimeKeyboard(true);
            keyboardMarkup.setResizeKeyboard(true);

            message.setReplyMarkup(keyboardMarkup);

            executeMessage(message);
        }
    }

    private void handleIncomingMessage(Message message) {
        long chatId = message.getChatId();
        String text = message.getText();
        Optional<User> optionalUser = user.findByChatId(chatId);

        ConversationState currentState = conversationStates.getOrDefault(chatId, null);

        if (currentState != null) {
            switch (currentState) {
                case ASK_PIZZA_OR_PASTA:
                    if (text.equalsIgnoreCase("Pizza") || text.equalsIgnoreCase("Pasta")) {

                        String type = text.equalsIgnoreCase("Pizza") ? "Pizza" : "Pasta";
                        conversationStates.put(chatId, ConversationState.ASK_NAME);

                        optionalUser.ifPresent(currentUser -> {
                            currentUser.setType(type);
                            user.save(currentUser);
                        });

                        SendMessage response = new SendMessage();
                        response.setChatId(chatId);
                        response.setText("You've selected " + type + ". Please enter your name:");
                        executeMessage(response);
                    } else {
                        SendMessage response = new SendMessage();
                        response.setChatId(chatId);
                        response.setText("Please choose either Pizza or Pasta.");
                        executeMessage(response);
                    }
                    break;
                case ASK_NAME:

                    optionalUser.ifPresent(currentUser -> {
                        currentUser.setName(text);
                        user.save(currentUser);
                    });

                    conversationStates.put(chatId, ConversationState.ASK_ADDRESS);
                    SendMessage response = new SendMessage();
                    response.setChatId(chatId);
                    response.setText("Thank you, " + text + ". Now, please enter your address:");
                    executeMessage(response);
                    break;
                case ASK_ADDRESS:

                    optionalUser.ifPresent(currentUser -> {
                        currentUser.setLocation(text);
                        user.save(currentUser);
                    });


                    sendOrderToGroup(chatId, optionalUser);

                    conversationStates.remove(chatId);
                    SendMessage endMessage = new SendMessage();
                    endMessage.setChatId(chatId);
                    endMessage.setText("Thank you for providing your address. Conversation ended.");
                    executeMessage(endMessage);
                    break;
            }
        }
    }

    private void sendOrderToGroup(long chatId, Optional<User> optionalUser) {
        optionalUser.ifPresent(currentUser -> {
            String orderDetails = "New order received!\n" +
                    "Type: " + currentUser.getType() + "\n" +
                    "Name: " + currentUser.getName() + "\n" +
                    "Address: " + currentUser.getLocation();

            SendMessage groupMessage = new SendMessage();
            groupMessage.setChatId(config.getGroup_id());
            groupMessage.setText(orderDetails);

            executeMessage(groupMessage);
        });
    }



    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
