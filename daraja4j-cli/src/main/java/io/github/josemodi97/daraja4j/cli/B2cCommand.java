package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.B2cRequest;
import io.github.josemodi97.daraja4j.model.B2cResult;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/** Sends money from a business shortcode to a registered M-Pesa customer. */
@Command(name = "b2c", description = "Disburse money to a registered M-Pesa customer.")
public final class B2cCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--amount", required = true, description = "Amount to disburse")
    double amount;

    @Option(names = "--phone", required = true, description = "Recipient's phone number")
    String phone;

    @Option(names = "--remarks", required = true, description = "2-100 characters describing the payment")
    String remarks;

    @Override
    public Integer call() {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        B2cRequest request = B2cRequest.builder()
                .amount(amount)
                .partyB(phone)
                .remarks(remarks)
                .build();

        B2cResult result = client.b2c(request);

        System.out.println("responseCode          = " + result.getResponseCode());
        System.out.println("responseDescription   = " + result.getResponseDescription());
        System.out.println("conversationId        = " + result.getConversationId());
        System.out.println("originatorConversationId = " + result.getOriginatorConversationId());
        System.out.println("(the actual disbursement outcome arrives later at your resultUrl)");

        return result.isAccepted() ? 0 : 1;
    }
}
