//package EMLtoPST.EMLtoPST;
//
//import com.aspose.email.*;
//import com.aspose.email.system.exceptions.ArgumentNullException;
//import com.aspose.email.system.exceptions.InvalidOperationException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.mail.Address;
//import javax.mail.Message;
//import javax.mail.Session;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//import javax.mail.internet.MimePart;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//import java.util.Date;
//import java.util.Properties;
//
//@Controller
//public class copy {
//    @Value("${upload.directory}") // Define the desired upload directory in application.properties
//    private String uploadDirectory;
//
//    @PostMapping("/upload")
//    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
//        if (file.isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
//            return "redirect:/";
//        }
//        try {
//            MailMessage message = new MailMessage();
//            String name = file.getOriginalFilename();
//
//            // Define the destination path where you want to save the file
//            Path destination = Path.of(uploadDirectory, name);
//
//            // Copy the uploaded file to the destination directory
//            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//
//            // Use the same uploadDirectory for opening the EML file
//            String emlFilePath = uploadDirectory + name;
//
//            // Load properties for the JavaMail session
//            Properties properties = new Properties();
//            Session session = Session.getDefaultInstance(properties);
//
//            try (FileInputStream emlFileInputStream = new FileInputStream(new File(emlFilePath))) {
//                // Create a MimeMessage from the EML file input stream
//                Message message2 = new MimeMessage(session, emlFileInputStream);
//
//                // Extract and process the EML content (subject, sender, etc.)
//                String subject = message2.getSubject();
//                model.addAttribute("subject", subject);
//                message.setSubject(subject);
//
//                Address[] fromAddresses = message2.getFrom();
//                if (fromAddresses.length > 0) {
//                    String sender = ((InternetAddress) fromAddresses[0]).getAddress();
//                    model.addAttribute("sender", sender);
//                    message.setFrom(new MailAddress(sender));
//                }
//
//                Date sentDate = message2.getSentDate();
//                model.addAttribute("date", sentDate);
//                message.setDate(sentDate);
//
//                Address[] toAddresses = message2.getRecipients(Message.RecipientType.TO);
//                if (toAddresses != null) {
//                    for (Address to : toAddresses) {
//                        model.addAttribute("to", ((InternetAddress) to).getAddress());
//                        message.getTo().add(String.valueOf(to));
//                    }
//                }
//
//                Object content = message2.getContent();
//
//                if (content instanceof String) {
//                    // This part is the message content (text or HTML)
//                    String text = (String) content;
//                    model.addAttribute("text", text);
//                    message.setHtmlBody(text);
//                    message.setBody(text);
//                } else if (content instanceof MimeMultipart) {
//                    MimeMultipart multipart = (MimeMultipart) content;
//                    int partCount = multipart.getCount();
//
//                    for (int i = 0; i < partCount; i++) {
//                        MimePart part = (MimePart) multipart.getBodyPart(i);
//                        String disposition = part.getDisposition();
//
//                        if (disposition == null || disposition.equalsIgnoreCase(javax.mail.Part.ATTACHMENT)) {
//                            // This part is an attachment
//                            String fileName = part.getFileName();
//                            model.addAttribute("attachment", fileName);
//                        } else {
//                            // This part is the message content (text or HTML)
//                            String html = part.getContent().toString();
//                            model.addAttribute("html", html);
//                            message.setHtmlBody("<html><body><p>This is the HTML body text.</p></body></html>");
//                        }
//                    }
//                }
//
//                try {
//                    int index = 0;
//                    // Create a new PST file
//                    for (int i = 0; i < file.getOriginalFilename().length(); i++) {
//                        if (file.getOriginalFilename().charAt(i) == '.') {
//                            index = i;
//                            break;
//                        }
//                    }
//                    String fileName = file.getOriginalFilename().substring(0, index);
//                    PersonalStorage pst = PersonalStorage.create(fileName + ".pst", FileFormatVersion.Unicode);
//
//                    // Create a root folder
//                    FolderInfo rootFolder = pst.getRootFolder();
//
//                    // Create an inbox folder
//                    FolderInfo inboxFolder = rootFolder.addSubFolder("Inbox");
//
//                    // Add the message to the inbox folder
//                    inboxFolder.addMessage(MapiMessage.fromMailMessage(message));
//
//                    // Save the PST file
//                    pst.dispose();
//
//                } catch (ArgumentNullException | InvalidOperationException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                Files.delete(destination);
//            } catch (IOException e) {
//                // Handle the exception (e.g., log an error message)
//                e.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "index";
//    }
//}
