package com.f1community.backend.util;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendPasswordResetEmail(String to, String subject, String resetLink) {
        // 이름이 없을 때 기본 인사로 처리
        sendPasswordResetEmail(to, subject, resetLink, (String) null);
    }

    @Override
    public void sendPasswordResetEmail(String to, String subject, String resetLink, String fullName) {
        try {
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setFrom(from);
            helper.setReplyTo(from);
            helper.setTo(to);
            helper.setSubject(subject);

            String greeting = (fullName != null && !fullName.isBlank())
                    ? fullName + " 님,"
                    : "안녕하세요,";

            String emailContent =
                    "<!DOCTYPE html>" +
                            "<html lang=\"ko\">" +
                            "<head>" +
                            "    <meta charset=\"UTF-8\">" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                            "    <style>" +
                            "        body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 0; }" +
                            "        .header { background-color: #fff; color: #000; padding: 15px; text-align: center; }" +
                            "        .container { padding: 20px; max-width: 600px; margin: auto; }" +
                            "        .footer { font-size: 12px; color: #777; padding: 20px; background: #f5f5f5; }" +
                            "    </style>" +
                            "</head>" +
                            "<body>" +
                            "    <div class=\"header\">" +
                            "        <img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/F1_%28registered_trademark%29.svg/256px-F1_%28registered_trademark%29.svg.png\" alt=\"F1 로고\" style=\"height:40px;\">" +
                            "    </div>" +
                            "    <div class=\"container\">" +
                            "        <p>" + greeting + "</p>" +
                            "        <p>이 계정과 연결된 비밀번호 재설정 요청을 접수하였습니다.</p>" +
                            "        <p>비밀번호를 재설정하고 안전하게 계정에 접속하려면 아래 링크를 클릭하세요.</p>" +
                            "        <p><a href=\"" + resetLink + "\">비밀번호 재설정 링크로 이동하기</a></p>" +
                            "        <p>추가 도움이 필요하신 경우 24시간 온라인 고객센터를 방문해 주세요.</p>" +
                            "    </div>" +
                            "    <div class=\"footer\">" +
                            "        <p>면책 고지 - 본 혜택은 한정 기간 동안만 제공되며, 일부 상품은 적용 대상에서 제외됩니다. 약관과 조건이 적용되며 사전 고지 없이 변경 또는 종료될 수 있습니다.</p>" +
                            "        <p>© 2024 F1 공식 스토어. 모든 권리 보유.</p>" +
                            "        <p>이메일 수신 설정 변경</p>" +
                            "    </div>" +
                            "</body>" +
                            "</html>";

            helper.setText(emailContent, true);

            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new MailPreparationException(e);
        }
    }
}