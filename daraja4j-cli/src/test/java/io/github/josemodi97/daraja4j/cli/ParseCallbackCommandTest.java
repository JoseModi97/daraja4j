package io.github.josemodi97.daraja4j.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ParseCallbackCommandTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void captureStdout() throws UnsupportedEncodingException {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, "UTF-8"));
    }

    @AfterEach
    void restoreStdio() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void parsesASuccessfulStkCallbackFromStdin() throws UnsupportedEncodingException {
        String body = "{\"Body\":{\"stkCallback\":{"
                + "\"MerchantRequestID\":\"29115-34620561-1\","
                + "\"CheckoutRequestID\":\"ws_CO_191220191020363925\","
                + "\"ResultCode\":0,"
                + "\"ResultDesc\":\"The service request is processed successfully.\","
                + "\"CallbackMetadata\":{\"Item\":[{\"Name\":\"MpesaReceiptNumber\",\"Value\":\"NLJ7RT61SV\"}]}"
                + "}}}";
        System.setIn(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

        int exitCode = new CommandLine(new Daraja4jCli()).execute("parse-callback", "--type", "stk");

        String output = capturedOut.toString("UTF-8");
        assertEquals(0, exitCode);
        assertTrue(output.contains("success             = true"));
        assertTrue(output.contains("mpesaReceiptNumber  = NLJ7RT61SV"));
    }

    @Test
    void parsesAFailedResultCallbackFromStdin() throws UnsupportedEncodingException {
        String body = "{\"Result\":{"
                + "\"ResultType\":0,"
                + "\"ResultCode\":\"R000002\","
                + "\"ResultDesc\":\"The OriginalTransactionID is invalid.\""
                + "}}";
        System.setIn(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

        int exitCode = new CommandLine(new Daraja4jCli()).execute("parse-callback", "--type", "result");

        String output = capturedOut.toString("UTF-8");
        assertTrue(exitCode != 0);
        assertTrue(output.contains("success             = false"));
        assertTrue(output.contains("R000002"));
    }

    @Test
    void rejectsAnUnknownType() throws UnsupportedEncodingException {
        System.setIn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));

        int exitCode = new CommandLine(new Daraja4jCli()).execute("parse-callback", "--type", "bogus");

        assertEquals(2, exitCode);
    }
}
