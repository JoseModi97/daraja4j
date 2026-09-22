package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.model.C2bConfirmation;
import io.github.josemodi97.daraja4j.model.Daraja4jResultCallback;
import io.github.josemodi97.daraja4j.model.StandingOrderCallback;
import io.github.josemodi97.daraja4j.model.StkCallbackResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Parses a captured Daraja callback payload (STK, Result, C2B, or Standing
 * Order) from a file or stdin - useful for reproducing and debugging a
 * callback body reported in production, without a live gateway call.
 *
 * <p>Daraja sends no signature on callbacks, so this only structurally
 * parses the body; it does not "verify" anything cryptographically.
 */
@Command(name = "parse-callback", description = "Parse a captured Daraja callback payload (stk, result, c2b, or standing-order).")
public final class ParseCallbackCommand implements Callable<Integer> {

    @Option(names = "--type", required = true, description = "One of: stk, result, c2b, standing-order")
    String type;

    @Option(names = "--file", description = "Read the callback body from this file instead of stdin")
    String file;

    @Override
    public Integer call() throws IOException {
        String body = file != null ? new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8) : readStdin();

        switch (type.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "stk": {
                StkCallbackResult result = StkCallbackResult.parse(body);
                System.out.println("success             = " + result.isSuccess());
                System.out.println("resultCode          = " + result.getResultCode());
                System.out.println("resultDesc          = " + result.getResultDesc());
                System.out.println("checkoutRequestId   = " + result.getCheckoutRequestId());
                System.out.println("mpesaReceiptNumber  = " + result.getMpesaReceiptNumber());
                System.out.println("amount              = " + result.getAmount());
                System.out.println("phoneNumber         = " + result.getPhoneNumber());
                return result.isSuccess() ? 0 : 1;
            }
            case "result": {
                Daraja4jResultCallback result = Daraja4jResultCallback.parse(body);
                System.out.println("success             = " + result.isSuccess());
                System.out.println("resultCode          = " + result.getResultCode());
                System.out.println("resultDesc          = " + result.getResultDesc());
                System.out.println("transactionId       = " + result.getTransactionId());
                System.out.println("conversationId      = " + result.getConversationId());
                return result.isSuccess() ? 0 : 1;
            }
            case "c2b": {
                C2bConfirmation confirmation = C2bConfirmation.parse(body);
                System.out.println("transId             = " + confirmation.getTransId());
                System.out.println("transAmount         = " + confirmation.getTransAmount());
                System.out.println("billRefNumber       = " + confirmation.getBillRefNumber());
                System.out.println("msisdn              = " + confirmation.getMsisdn());
                System.out.println("firstName           = " + confirmation.getFirstName());
                return 0;
            }
            case "standing-order": {
                StandingOrderCallback callback = StandingOrderCallback.parse(body);
                System.out.println("success             = " + callback.isSuccess());
                System.out.println("responseCode        = " + callback.getResponseCode());
                System.out.println("responseDescription = " + callback.getResponseDescription());
                System.out.println("status              = " + callback.getDataValue("status"));
                System.out.println("transactionId       = " + callback.getDataValue("TransactionID"));
                return callback.isSuccess() ? 0 : 1;
            }
            default:
                System.err.println("Unknown --type '" + type + "': expected one of stk, result, c2b, standing-order");
                return 2;
        }
    }

    private static String readStdin() throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            char[] buffer = new char[4096];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                body.append(buffer, 0, read);
            }
        }
        return body.toString();
    }
}
