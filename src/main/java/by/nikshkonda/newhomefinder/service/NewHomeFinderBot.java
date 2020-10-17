package by.nikshkonda.newhomefinder.service;

import by.nikshkonda.newhomefinder.service.resource.SettingsService;
import by.nikshkonda.newhomefinder.service.rw.ApartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResponseDocType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewHomeFinderBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.chatId}")
    private String chatId;

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private ScheduledService scheduledService;


    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()){
            sendButtons();
            return;
        }

        String text = update.getMessage().getText();
//        Long chatId = update.getMessage().getChatId();
        if (text.startsWith("/setChatId")){
            chatId = update.getMessage().getChatId().toString();
        }

        if (text.startsWith("/set")) {
            String[] array = text.split(" ");
            if (array.length == 3) {
                settingsService.set(array[1], array[2]);
            } else {
                sendMessage("Некорректное свойство.");
            }
        }
        if (text.equals("/props")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                sendMessage(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(settingsService.read()));
            } catch (Exception ex) {
                sendMessage("Error :(");
            }
        }
        if (text.equals("/findAll")){
            scheduledService.loadApartments(true);
        }
        if (text.equals("/find")){
            scheduledService.loadApartments(false);
        }
        if ("/clear".equals(text)) {
//            deleteMessages();
        }
        if ("/help".equals(text)){
            StringBuilder sb = new StringBuilder();
            sb.append("/find - найти все навые вырианты или изменённые, соответствующие параметрам.\n")
                    .append("/findAll - найти все варианты, соответствующие параметрам.\n")
                    .append("/props - вывести параметры.\n")
                    .append("/set PARAM VALUE - установить параматр.\n")
                    .append("/setChatId - использовать текущий чат для отображения.\n");
            sendMessage(sb.toString());
        }

    }


    public void sendMessage(String text) {
        tryToSendMessage5Times(text);
    }

    private void tryToSendMessage5Times(String text) {
        for (int i =0; i<7; i++) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(text);
            try {
                execute(sendMessage);
                return;
            } catch (Exception ex) {
                try {
                    System.out.println("I try after 10 sec");
                    Thread.sleep(10000);
                } catch (Exception ex1) {
                    System.out.println("Error sleeping 10 sec");
                }
            }
        }
        System.out.println("SORRY, I CANT SEND MESSAGE (");
        System.out.println(text);
    }

    private void setButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("/findAll");
        keyboardRow.add("/find");
        keyboardRow.add("/props");
        keyboardRow.add("/help");
        rows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(rows);
    }

    private void sendButtons(){
        try{
            SendMessage sendMessage = new SendMessage();
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (Exception ex) {

        }
    }

    private void deleteMessages(){
        try {
            GetUpdates getUpdates = new GetUpdates();
            getUpdates.setLimit(100);
            getUpdates.setOffset(100);
            getUpdates.setTimeout(0);
            execute(getUpdates);
            System.out.println("qee");

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}