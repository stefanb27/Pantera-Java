package com.example.pantera.printers;

import com.example.pantera.controller.MenuButtonsController;
import com.example.pantera.domain.Connection;
import com.example.pantera.domain.Message;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.GroupDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.ControllerService;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class PdfPrinter {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    MenuButtonsController menuButtonsController;
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    GroupDBRepository groupDBRepository = new GroupDBRepository(connection);

    public void printStatisticsForLoggedUser(User user, List<User> users, List<Message> privateMessages,
                                             List<Message> groupMessages, LocalDate startDate, LocalDate endDate) {
        PDDocument document = new PDDocument();
        String filename = "src/main/statistics/" + user.getFirstName() + "_" + user.getLastName() + "_statistics.pdf";
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF file","*.pdf"));
            fileChooser.setTitle("Save to PDF");
            fileChooser.setInitialFileName("untitled.pdf");
            File file = fileChooser.showSaveDialog(null);
            PDPage page = new PDPage();
            document.addPage(page);
            PDDocumentInformation pdd = document.getDocumentInformation();
            pdd.setAuthor(user.toString());
            pdd.setTitle(user + "'s " + "statistics");
            Calendar date = new GregorianCalendar();
            date.set(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
            pdd.setCreationDate(date);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(10, 700);
            contentStream.showText("Here are some statistics about your new friendships and the messages" +
                    " you received between "
                    + startDate.toString() + " and " + endDate.toString());
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            if (users.size() == 0) contentStream.showText("Sadly, you have not made any new friends :(");
            else {
                contentStream.showText("You have made some new friends:");
                contentStream.newLine();
                for (User user1 : users) {
                    contentStream.showText(user1.toString());
                    contentStream.newLine();
                }
            }
            contentStream.newLine();
            if (privateMessages.size() == 0) contentStream.showText("You do not have any private messages.");
            else {
                contentStream.showText("Also, you received the following private messages:");
                contentStream.newLine();
                for (Message message : privateMessages) {
                    contentStream.showText("(from: " + userDBRepository.findOne(message.getFrom()) + ") " +
                            message.getMessage());
                    contentStream.newLine();
                }
            }
            contentStream.newLine();
            if (groupMessages.size() == 0) contentStream.showText("You do not have any group chats.");
            else {
                contentStream.showText("Here are the group chats:");
                contentStream.newLine();
                List<Message> copy = new ArrayList<>(groupMessages);
                List<String> groupNames = copy.stream().map(x -> groupDBRepository.findOne(x.getGroup()).getName())
                        .collect(Collectors.toList());
                int n = 0;
                contentStream.showText(groupNames.get(0));
                contentStream.newLine();
                for (Message message : groupMessages) {
                    System.out.println(message.getGroup());
                    if (!groupDBRepository.findOne(message.getGroup()).getName().equals(groupNames.get(n))) {
                        n++;
                        contentStream.showText(groupNames.get(n));
                        contentStream.newLine();
                    }
                    contentStream.showText("(from: " + userDBRepository.findOne(message.getFrom()) + ")" +
                            message.getMessage());
                    contentStream.newLine();
                }
            }
            contentStream.endText();
            contentStream.close();
            document.save(file.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printStatisticsForFriendsMessages(User user, User user1, List<Message> messages,
                                                  LocalDate startDate, LocalDate endDate) {
        PDDocument document = new PDDocument();
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF file","*.pdf"));
            fileChooser.setTitle("Save to PDF");
            fileChooser.setInitialFileName("untitled.pdf");
            File file = fileChooser.showSaveDialog(null);
            PDPage page = new PDPage();
            document.addPage(page);
            PDDocumentInformation pdd = document.getDocumentInformation();
            pdd.setAuthor(user.toString());
            pdd.setTitle(user.toString() + "'s " + "conversations statistics");
            Calendar date = new GregorianCalendar();
            date.set(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
            pdd.setCreationDate(date);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setLeading(14.5f);
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(10, 700);
            if (messages.size() == 0) contentStream.showText("You and " + user1 + " have not chatted.");
            else {
                contentStream.showText("Here is the conversation between you and " + user1 + "on "
                        + startDate.toString() + " - " + endDate.toString());
                contentStream.newLine();
                for (Message message : messages) {
                    contentStream.showText("(from: " + userDBRepository.findOne(message.getFrom()) + ")"
                            + message.getMessage());
                    contentStream.newLine();
                }
            }
            contentStream.endText();
            contentStream.close();
            document.save(file.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportAsPdfMess() {


//        if(file!=null){
//            String str = file.getAbsolutePath();
//
//            try {
//                FileWriter fw = new FileWriter(str);
//                for(Message m:lst2){
//                    fw.write(m.toString());
//                    fw.write("\n");
//                }
//                fw.flush();
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
    }
}