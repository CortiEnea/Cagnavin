package org.vaadin.example.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


@Service
public class MailService {

    private final JavaMailSender emailSender;

    @Autowired
    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendRegisterEmail(String destinatario, String username) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true per supportare HTML

        helper.setTo(destinatario);
        helper.setSubject("Registrazione completata");

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "<div style='width: 100%; height: 100vh; display: flex; justify-content: center; align-items: center;'>" +
                "<div style='background: linear-gradient(to bottom right, #4a0e2c, #2c0735); padding: 40px; border-radius: 10px; width: 60%; color: white; text-align: center;'>" +
                "<h1 style='font-size: 24px;'>Ciao " + username + ",</h1>" +
                "<p>Grazie per esserti registrato! La tua registrazione è stata completata con successo.</p>" +
                "<p>Se hai bisogno di assistenza, non esitare a contattarci.</p>" +
                "<p>Cordiali saluti,<br>Gruppo Cagnavin</p>" +
                "<img src='cid:logoImage' alt='Immagine di benvenuto' style='margin-top: 20px; width: 300px; ' />" +
                "</div>" +
                "</div>" +
                "</body></html>";

        helper.setText(htmlContent, true);

        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.png");
        if (imageStream == null) {
            throw new RuntimeException("Immagine non trovata nelle risorse!");
        }

        byte[] imageBytes = imageStream.readAllBytes();
        DataSource dataSource = new ByteArrayDataSource(imageBytes, "image/png");

        helper.addInline("logoImage", dataSource);

        emailSender.send(message);
    }

    public void sendEmailInvoice(Integer quota, String destinatario, String username, String destinazione) throws MessagingException, IOException, WriterException {
        byte[] pdfBytes = generateSwissInvoicePDF(quota, username, destinazione);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(destinatario);
        helper.setSubject("Fattura per la tua gita a " + destinazione);
        helper.setText("Gentile " + username + ",\n\nIn allegato trovi la fattura per la tua gita a " + destinazione + ".\nGrazie per aver scelto i nostri servizi!\n\nCordiali saluti,\nIl team", false);

        DataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
        helper.addAttachment("Fattura_Gita_" + destinazione + ".pdf", dataSource);

        emailSender.send(message);
    }

    private byte[] generateSwissInvoicePDF(Integer quota, String username, String destinazione) throws IOException, WriterException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Sezione di pagamento");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Beneficiario: Mario Rossi SA");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Indirizzo: Via Fittizia 12, 8000 Zurigo, CH");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("IBAN: CH11 0023 6236 1573 1740 Q");
        contentStream.newLineAtOffset(0, -20);
        String text = "Importo: " + String.format("%.2f", quota.doubleValue()) + " CHF";
        contentStream.showText(text);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Riferimento: NON");
        contentStream.endText();

        BufferedImage qrImage = generateQRCodeImage(generateSwissPaymentQR(quota, destinazione));
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageToByteArray(qrImage), "QRCode");
        contentStream.drawImage(pdImage, 50, 400, 174, 174);

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 350);
        contentStream.showText("Sezione di ricevuta");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Beneficiario: Mario Rossi SA");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Indirizzo: Via Fittizia 12, 8000 Zurigo, CH");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("IBAN: CH11 0023 6236 1573 1740 Q");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Importo: " + String.format("%.2f", quota.doubleValue()) + " CHF");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Riferimento: NON");
        contentStream.endText();

        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    private String generateSwissPaymentQR(int amount, String destination) {
        String iban = "CH110023623615731740Q";
        String beneficiary = "Mario Rossi SA";
        String street = "";
        String houseNumber = "";
        String postalCode = "8000";
        String city = "Zurich";
        String country = "CH";
        String formattedAmount = String.format("%.2f", (double) amount); // e.g., "1.00"
        String currency = "CHF";
        String referenceType = "NON";

        String ucAddressType = "";
        String ucName = "";
        String ucStreet = "";
        String ucHouseNumber = "";
        String ucPostalCode = "";
        String ucCity = "";
        String ucCountry = "";

        String debtorAddressType = "S";
        String debtorName = "";
        String debtorStreet = "";
        String debtorHouseNumber = "";
        String debtorPostalCode = "";
        String debtorCity = "";
        String debtorCountry = "";

        String reference = "";
        String additionalInfo = "";

        return "SPC\n" +
                "0200\n" +
                "1\n" +
                iban + "\n" +
                "S\n" +
                beneficiary + "\n" +
                street + "\n" +
                houseNumber + "\n" +
                postalCode + "\n" +
                city + "\n" +
                country + "\n" +
                ucAddressType + "\n" +
                ucName + "\n" +
                ucStreet + "\n" +
                ucHouseNumber + "\n" +
                ucPostalCode + "\n" +
                ucCity + "\n" +
                ucCountry + "\n" +
                formattedAmount + "\n" +
                currency + "\n" +
                debtorAddressType + "\n" +
                debtorName + "\n" +
                debtorStreet + "\n" +
                debtorHouseNumber + "\n" +
                debtorPostalCode + "\n" +
                debtorCity + "\n" +
                debtorCountry + "\n" +
                referenceType + "\n" +
                reference + "\n" +
                additionalInfo + "\n" +
                "EPD";
    }


    private BufferedImage generateQRCodeImage(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        return image;
    }

    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
