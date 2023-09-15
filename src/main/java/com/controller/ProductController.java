package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.entity.Product;
import com.itextpdf.text.DocumentException;

import jakarta.activation.DataHandler;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

@Controller
public class ProductController {

	@Autowired
	private TemplateEngine templateEngine;

	private String pdfSavePath = "/home/rapidosft/Desktop/Java-Training/Templates/pdf-template/src/main/java/data/";


	@PostMapping(value = "/generate-pdf")
	@ResponseBody
	public String generatePdf(@RequestBody Product product) throws IOException, DocumentException {
		// Generate PDF from product data and return it as bytes
		byte[] pdfBytes = generatePdfFromProduct(product);
	    
	    if (pdfBytes != null) {
	        // Send the generated PDF in an email
	        boolean emailSent = sendPdfInEmail(pdfBytes);
	        
	        if (emailSent) {
	            return "PDF Generated and Sent Successfully";
	        } else {
	            return "PDF Generated, but Email Sending Failed";
	        }
	    } else {
	        return "PDF Generation Failed";
	    }
	}

	private byte[] generatePdfFromProduct(Product product) throws IOException, DocumentException {
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

		try {
			Context context = new Context(Locale.ENGLISH);
			context.setVariable("product", product);

			String htmlContent = templateEngine.process("template", context);

			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(htmlContent);
			renderer.layout();
			renderer.createPDF(pdfOutputStream);
			renderer.finishPDF();

			// Save the PDF to the specified path
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String pdfFilePath = pdfSavePath + "product-invoice_" + timestamp + ".pdf";
			try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFilePath)) {
				pdfOutputStream.writeTo(fileOutputStream);
			}

			pdfOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pdfOutputStream.toByteArray();
	}
	
	private boolean sendPdfInEmail(byte[] pdfBytes) {
	    // Modify these variables with your email details
	    String toEmail = "subscriptions.sandeep@gmail.com";
	    String fromEmail = "sandeepballi.rapidsoft@gmail.com";
	    String subject = "PDF Invoice";
	    String text = "Please find the attached PDF invoice.";
	    
	    return sendEmailWithAttachment(toEmail, fromEmail, subject, text, pdfBytes, text);
	   
	}
	
	public boolean sendEmailWithAttachment(String to, String from, String subject, String text, byte[] attachmentData, String attachmentName) {
	    boolean flag = false;

	    Properties properties = new Properties();
	    properties.put("mail.smtp.auth", true);
	    properties.put("mail.smtp.starttls.enable", true);
	    properties.put("mail.smtp.port", "587");
	    properties.put("mail.smtp.host", "smtp.gmail.com");

	    String username = "sandeepballi.rapidsoft@gmail.com";
	    String password = "uovgxsmzaxyyjytc";

	    Session session = Session.getInstance(properties, new Authenticator() {

	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
	    });

	    try {
	        Message message = new MimeMessage(session);

	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
	        message.setFrom(new InternetAddress(from));
	        message.setSubject(subject);

	        // Create a multipart message
	        Multipart multipart = new MimeMultipart();

	        // Create the text part of the message
	        MimeBodyPart textPart = new MimeBodyPart();
	        textPart.setText(text);

	        // Create the attachment part
	        MimeBodyPart attachmentPart = new MimeBodyPart();
	        attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentData, "application/pdf")));
	        attachmentPart.setFileName(attachmentName);

	        // Add parts to the multipart message
	        multipart.addBodyPart(textPart);
	        multipart.addBodyPart(attachmentPart);

	        // Set the content of the message
	        message.setContent(multipart);

	        // Send the email
	        Transport.send(message);
	        flag = true;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return flag;
	}



}
