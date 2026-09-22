package io.github.josemodi97.daraja4j.jakarta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class Daraja4jStkCallbackServletHandlerTest {

    private static final String SUCCESS_BODY = "{\"Body\":{\"stkCallback\":{"
            + "\"MerchantRequestID\":\"29115-34620561-1\","
            + "\"CheckoutRequestID\":\"ws_CO_191220191020363925\","
            + "\"ResultCode\":0,"
            + "\"ResultDesc\":\"The service request is processed successfully.\","
            + "\"CallbackMetadata\":{\"Item\":[{\"Name\":\"MpesaReceiptNumber\",\"Value\":\"NLJ7RT61SV\"}]}"
            + "}}}";

    private static final String CANCELLED_BODY = "{\"Body\":{\"stkCallback\":{"
            + "\"MerchantRequestID\":\"f1e2-4b95-a71d-b30d3cdbb7a7942864\","
            + "\"CheckoutRequestID\":\"ws_CO_21072024125243250722943992\","
            + "\"ResultCode\":1032,"
            + "\"ResultDesc\":\"Request cancelled by user\""
            + "}}}";

    private static HttpServletRequest requestWithBody(String body) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
        return request;
    }

    @Test
    void invokesOnSuccessAndAcknowledgesAValidCallback() throws IOException {
        HttpServletRequest request = requestWithBody(SUCCESS_BODY);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        boolean[] successCalled = {false};
        new Daraja4jStkCallbackServletHandler()
                .onSuccess((result, req, res) -> successCalled[0] = true)
                .onFailure((result, req, res) -> {
                    throw new AssertionError("onFailure should not be called for a successful callback");
                })
                .handle(request, response);

        assertEquals(true, successCalled[0]);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void invokesOnFailureForACancelledCallback() throws IOException {
        HttpServletRequest request = requestWithBody(CANCELLED_BODY);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        boolean[] failureCalled = {false};
        new Daraja4jStkCallbackServletHandler()
                .onSuccess((result, req, res) -> {
                    throw new AssertionError("onSuccess should not be called for a cancelled callback");
                })
                .onFailure((result, req, res) -> failureCalled[0] = true)
                .handle(request, response);

        assertEquals(true, failureCalled[0]);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doesNotOverwriteAResponseTheCallbackAlreadyCommitted() throws IOException {
        HttpServletRequest request = requestWithBody(SUCCESS_BODY);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(true);

        new Daraja4jStkCallbackServletHandler()
                .onSuccess((result, req, res) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT))
                .handle(request, response);

        verify(response, never()).setStatus(HttpServletResponse.SC_OK);
        verify(response, never()).getWriter();
    }
}
