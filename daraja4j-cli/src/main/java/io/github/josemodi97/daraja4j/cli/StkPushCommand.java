package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.StkPushRequest;
import io.github.josemodi97.daraja4j.model.StkPushResult;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Triggers an M-Pesa Express (STK Push) request from the command line - for
 * smoke-testing credentials/signing without writing any application code.
 */
@Command(name = "stk-push", description = "Trigger an M-Pesa Express (STK Push) USSD prompt.")
public final class StkPushCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--amount", required = true, description = "Amount to charge, e.g. 500")
    double amount;

    @Option(names = "--phone", required = true, description = "Payer's phone number, any Kenyan format")
    String phone;

    @Option(names = "--account-reference", required = true, description = "Shown to the payer in the USSD prompt, max 12 characters")
    String accountReference;

    @Option(names = "--transaction-desc", description = "Additional description, max 13 characters")
    String transactionDesc;

    @Override
    public Integer call() {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        StkPushRequest.Builder requestBuilder = StkPushRequest.builder()
                .amount(amount)
                .partyA(phone)
                .accountReference(accountReference);
        if (transactionDesc != null) {
            requestBuilder.transactionDesc(transactionDesc);
        }

        StkPushResult result = client.stkPush(requestBuilder.build());

        System.out.println("responseCode        = " + result.getResponseCode());
        System.out.println("responseDescription = " + result.getResponseDescription());
        System.out.println("merchantRequestId   = " + result.getMerchantRequestId());
        System.out.println("checkoutRequestId   = " + result.getCheckoutRequestId());
        System.out.println("customerMessage     = " + result.getCustomerMessage());

        return result.isAccepted() ? 0 : 1;
    }
}
