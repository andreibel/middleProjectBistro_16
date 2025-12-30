package com.andreibel.server.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;

public class SimpleInvoicePdf {

    public record InvoiceData(
            String invoiceNumber,
            String confirmationCode,
            String restaurantName,
            String restaurantAddress,
            String customerName,
            String customerPhone,
            String customerEmail,
            int guests,
            BigDecimal unitPrice,   // price per guest
            boolean isSubscriber
    ) {}

    public static Path generate(Path htmlTemplate, Path outPdf, Path hebrewFontTtf, InvoiceData d) throws Exception {
        String template = Files.readString(htmlTemplate, StandardCharsets.UTF_8);

        BigDecimal subtotal = d.unitPrice().multiply(BigDecimal.valueOf(d.guests()));
        BigDecimal discount = d.isSubscriber()
                ? subtotal.multiply(new BigDecimal("0.10"))
                : BigDecimal.ZERO;

        // rounding to 2 decimals
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        discount = discount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal grandTotal = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        String subscriberBadge = d.isSubscriber() ? "מנוי פעיל" : "לא מנוי";

        String html = template
                .replace("{invoiceNumber}", esc(d.invoiceNumber()))
                .replace("{confirmationCode}", esc(d.confirmationCode()))
                .replace("{date}", LocalDate.now().toString())
                .replace("{restaurantName}", esc(d.restaurantName()))
                .replace("{restaurantAddress}", esc(d.restaurantAddress()))
                .replace("{subscriberBadge}", subscriberBadge)
                .replace("{customerName}", esc(d.customerName()))
                .replace("{customerPhone}", esc(nvl(d.customerPhone())))
                .replace("{customerEmail}", esc(nvl(d.customerEmail())))
                .replace("{guests}", Integer.toString(d.guests()))
                .replace("{unitPrice}", money(d.unitPrice()))
                .replace("{subtotal}", money(subtotal))
                .replace("{discount}", money(discount))
                .replace("{grandTotal}", money(grandTotal));

        Files.createDirectories(outPdf.getParent());

        try (OutputStream os = Files.newOutputStream(outPdf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // Hebrew support (recommended)
            if (hebrewFontTtf != null) {
                builder.useFont(hebrewFontTtf.toFile(), "Noto Sans Hebrew");
            }

            builder.toStream(os);
            builder.run();
        }

        return outPdf;
    }

    private static String money(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
