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

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class ProductController {

	@Autowired
	private TemplateEngine templateEngine;

	private String pdfSavePath = "/home/rapidosft/Desktop/Java-Training/Templates/pdf-template/src/main/java/data/";

	public ProductController(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@PostMapping(value = "/generate-pdf")
	@ResponseBody
	public String generatePdf(@RequestBody Product product) throws IOException, DocumentException {
		// Generate PDF from product data and return it as bytes
		generatePdfFromProduct(product);
		return "PDF Generated Successfully";
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

}
