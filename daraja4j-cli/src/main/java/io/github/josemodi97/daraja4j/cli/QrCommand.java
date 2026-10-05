package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.QrCodeRequest;
import io.github.josemodi97.daraja4j.model.QrCodeResult;
import io.github.josemodi97.daraja4j.model.QrTransactionType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Generates an M-Pesa Dynamic QR code from the command line, with optional PNG file saving.
 */
@Command(name = "qr", description = "Generate an M-Pesa Dynamic QR code.")
public final class QrCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--merchant-name", required = true, description = "Name of the business or merchant")
    String merchantName;

    @Option(names = "--ref-no", required = true, description = "Reference number or invoice ID")
    String refNo;

    @Option(names = "--amount", required = true, description = "Transaction amount")
    double amount;

    @Option(names = "--trx-code", description = "Transaction code: PB (Paybill), BG (Buy Goods), SM (Send Money), WA (Withdraw Agent), SB (Send Business). Default: PB")
    String trxCode = "PB";

    @Option(names = "--cpi", description = "Credit Party Identifier (shortcode / till / phone number). Falls back to --shortcode")
    String cpi;

    @Option(names = "--size", description = "Image dimensions in pixels (default 300)")
    int size = 300;

    @Option(names = "--output", description = "Optional file path to save the generated PNG image (e.g. qrcode.png)")
    String output;

    @Override
    public Integer call() throws IOException {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        QrCodeRequest.Builder builder = QrCodeRequest.builder()
                .merchantName(merchantName)
                .refNo(refNo)
                .amount(BigDecimal.valueOf(amount))
                .trxCode(QrTransactionType.fromWireValue(trxCode))
                .size(size);

        if (cpi != null) {
            builder.cpi(cpi);
        }

        QrCodeResult result = client.generateQrCode(builder.build());

        System.out.println("responseCode        = " + result.getResponseCode());
        System.out.println("responseDescription = " + result.getResponseDescription());
        if (result.getRequestId() != null && !result.getRequestId().isEmpty()) {
            System.out.println("requestId           = " + result.getRequestId());
        }

        if (result.isSuccess()) {
            byte[] pngBytes = result.toPngBytes();
            System.out.println("qrCode (base64)     = " + result.getQrCode());
            System.out.println("qrCodeImageSize     = " + pngBytes.length + " bytes");

            if (output != null) {
                File outputFile = new File(output);
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(pngBytes);
                }
                System.out.println("savedImageTo        = " + outputFile.getAbsolutePath());
            }
        }

        return result.isSuccess() ? 0 : 1;
    }
}
