package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.TransactionStatusRequest;
import io.github.josemodi97.daraja4j.model.TransactionStatusResult;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/** Checks the status of a prior transaction - a secondary reconciliation mechanism when a callback was missed. */
@Command(name = "status", description = "Check the status of a prior transaction.")
public final class StatusCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--transaction-id", description = "The M-Pesa receipt number of the transaction to check")
    String transactionId;

    @Option(names = "--original-conversation-id", description = "The OriginatorConversationID of the transaction to check")
    String originalConversationId;

    @Option(names = "--remarks", defaultValue = "status check", description = "Comment sent with the request")
    String remarks;

    @Override
    public Integer call() {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        TransactionStatusRequest.Builder requestBuilder = TransactionStatusRequest.builder().remarks(remarks);
        if (transactionId != null) {
            requestBuilder.transactionId(transactionId);
        }
        if (originalConversationId != null) {
            requestBuilder.originalConversationId(originalConversationId);
        }

        TransactionStatusResult result = client.queryTransactionStatus(requestBuilder.build());

        System.out.println("responseCode          = " + result.getResponseCode());
        System.out.println("responseDescription   = " + result.getResponseDescription());
        System.out.println("conversationId        = " + result.getConversationId());
        System.out.println("originatorConversationId = " + result.getOriginatorConversationId());
        System.out.println("(the actual status arrives later at your resultUrl - this only confirms the query was accepted)");

        return result.isAccepted() ? 0 : 1;
    }
}
