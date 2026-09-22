package io.github.josemodi97.daraja4j.cli;

import io.github.josemodi97.daraja4j.Daraja4jClient;
import io.github.josemodi97.daraja4j.model.AccountBalanceRequest;
import io.github.josemodi97.daraja4j.model.AccountBalanceResult;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/** Queries the current M-Pesa account balance of this shortcode. */
@Command(name = "balance", description = "Query the current M-Pesa account balance.")
public final class BalanceCommand implements Callable<Integer> {

    @Mixin
    CredentialsMixin credentials;

    @Option(names = "--remarks", defaultValue = "balance check", description = "Comment sent with the request")
    String remarks;

    @Override
    public Integer call() {
        Daraja4jClient client = new Daraja4jClient(credentials.toConfig());

        AccountBalanceRequest request = AccountBalanceRequest.builder()
                .remarks(remarks)
                .build();

        AccountBalanceResult result = client.queryAccountBalance(request);

        System.out.println("responseCode          = " + result.getResponseCode());
        System.out.println("responseDescription   = " + result.getResponseDescription());
        System.out.println("conversationId        = " + result.getConversationId());
        System.out.println("originatorConversationId = " + result.getOriginatorConversationId());
        System.out.println("(the actual balance arrives later at your resultUrl)");

        return result.isAccepted() ? 0 : 1;
    }
}
