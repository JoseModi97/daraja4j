package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.StkPushQueryRequest;
import io.github.josemodi97.daraja4j.model.StkPushQueryResult;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Polls the outcome of an STK Push from the command line using its CheckoutRequestID.
 */
@Command(name = "stk-query", description = "Query the status of an STK Push.")
public final class StkQueryCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--checkout-id", required = true, description = "CheckoutRequestID returned by stk-push")
    String checkoutRequestId;

    @Override
    public Integer call() {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        StkPushQueryRequest request = StkPushQueryRequest.builder()
                .checkoutRequestId(checkoutRequestId)
                .build();

        StkPushQueryResult result = client.stkPushQuery(request);

        System.out.println("responseCode        = " + result.getResponseCode());
        System.out.println("responseDescription = " + result.getResponseDescription());
        System.out.println("merchantRequestId   = " + result.getMerchantRequestId());
        System.out.println("checkoutRequestId   = " + result.getCheckoutRequestId());
        System.out.println("resultCode          = " + result.getResultCode());
        System.out.println("resultDesc          = " + result.getResultDesc());

        return result.isSuccess() ? 0 : 1;
    }
}
