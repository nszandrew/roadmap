package br.com.nszandrew.roadmap.infra.email;

import br.com.nszandrew.roadmap.model.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.jdbc.UnsupportedDataSourcePropertyException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;
    private static final String FROM = "nszandrew10@gmail.com";
    private static final String TO = "nszandrew";
    public static final String URL_SITE = "http://localhost:8080";

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(String userMail, String subject, String body){
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try{
            helper.setFrom(FROM, TO);
            helper.setTo(userMail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);

        }catch (MessagingException | UnsupportedEncodingException e){
            throw new RuntimeException("Erro ao enviar email");
        }
        mailSender.send(message);
    }

    public void sendVerifyEmail(User user) {
        String assunto = "Link para verificar o seu email";
        String conteudo = genarateEmailContent("Olá [[name]],<br>"
                + "Por favor clique no link abaixo para verificar sua conta:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFICAR</a></h3>"
                + "Obrigado,<br>"
                + "Fórum Hub :).", user.getName(), URL_SITE + "/api/verify-account?code=" + user.getVerifyToken());

        sendEmail(user.getUsername(), assunto, conteudo);
    }

    private String genarateEmailContent(String template, String name, String url) {
        return template.replace("[[name]]", name).replace("[[URL]]", url);
    }
}
