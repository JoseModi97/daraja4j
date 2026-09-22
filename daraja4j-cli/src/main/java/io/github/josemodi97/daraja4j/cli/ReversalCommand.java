package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.ReversalRequest;
import io.github.josemodi97.daraja4j.model.ReversalResult;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/** Reverses a completed C2B transaction, refunding the payer. */
@Command(name = "reverse", description = "Reverse a completed C2B transaction.")
public final class ReversalCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--transaction-id", required = true, description = "M-Pesa receipt number of the transaction to reverse")
    String transactionId;

    @Option(names = "--amount", required = true, description = "Must match the original transaction amount")
    double amount;

    @Option(names = "--remarks", required = true, description = "2-100 characters describing the reversal")
    String remarks;

    @Override
    public Integer call() {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        ReversalRequest request = ReversalRequest.builder()
                .transactionId(transactionId)
                .amount(amount)
                .remarks(remarks)
                .build();

        ReversalResult result = client.reverseTransaction(request);

        System.out.println("responseCode          = " + result.getResponseCode());
        System.out.println("responseDescription   = " + result.getResponseDescription());
        System.out.println("conversationId        = " + result.getConversationId());
        System.out.println("originatorConversationId = " + result.getOriginatorConversationId());
        System.out.println("(the actual reversal outcome arrives later at your resultUrl)");

        return result.isAccepted() ? 0 : 1;
    }
}
