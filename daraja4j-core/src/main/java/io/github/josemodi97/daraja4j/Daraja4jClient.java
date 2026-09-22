package io.github.josemodi97.daraja4j;

import io.github.josemodi97.daraja4j.model.AccountBalanceRequest;
import io.github.josemodi97.daraja4j.model.AccountBalanceResult;
import io.github.josemodi97.daraja4j.model.B2PochiRequest;
import io.github.josemodi97.daraja4j.model.B2PochiResult;
import io.github.josemodi97.daraja4j.model.B2bRequest;
import io.github.josemodi97.daraja4j.model.B2bResult;
import io.github.josemodi97.daraja4j.model.B2cRequest;
import io.github.josemodi97.daraja4j.model.B2cResult;
import io.github.josemodi97.daraja4j.model.C2bSimulateRequest;
import io.github.josemodi97.daraja4j.model.C2bSimulateResult;
import io.github.josemodi97.daraja4j.model.PullTransactionsQueryRequest;
import io.github.josemodi97.daraja4j.model.PullTransactionsQueryResult;
import io.github.josemodi97.daraja4j.model.PullTransactionsRegisterRequest;
import io.github.josemodi97.daraja4j.model.PullTransactionsRegisterResult;
import io.github.josemodi97.daraja4j.model.RegisterC2bUrlsRequest;
import io.github.josemodi97.daraja4j.model.RegisterC2bUrlsResult;
import io.github.josemodi97.daraja4j.model.ReversalRequest;
import io.github.josemodi97.daraja4j.model.ReversalResult;
import io.github.josemodi97.daraja4j.model.StandingOrderRequest;
import io.github.josemodi97.daraja4j.model.StandingOrderResult;
import io.github.josemodi97.daraja4j.model.StkPushQueryRequest;
import io.github.josemodi97.daraja4j.model.StkPushQueryResult;
import io.github.josemodi97.daraja4j.model.StkPushRequest;
import io.github.josemodi97.daraja4j.model.StkPushResult;
import io.github.josemodi97.daraja4j.model.TransactionStatusRequest;
import io.github.josemodi97.daraja4j.model.TransactionStatusResult;
import java.util.concurrent.CompletableFuture;

/**
 * The main entry point of daraja4j: trigger an M-Pesa STK push, move money
 * with B2C/B2B/B2Pochi, reverse or query a transaction, check a balance, and
 * work with the Pull Transactions and M-Pesa Ratiba APIs - all with OAuth
 * token management handled transparently.
 *
 * <p>Thread-safe and cheap to hold as a singleton/bean for the lifetime of
 * the application, once constructed with a valid {@link Daraja4jConfig}.
 *
 * <pre>{@code
 * Daraja4jClient client = new Daraja4jClient(Daraja4jConfig.builder()
 *         .consumerKey("...")
 *         .consumerSecret("...")
 *         .environment(Daraja4jConfig.Environment.SANDBOX)
 *         .defaultShortcode("174379")
 *         .defaultPasskey("...")
 *         .defaultCallbackUrl("https://yourapp.example.com/daraja4j/stk-callback")
 *         .build());
 *
 * StkPushResult ack = client.stkPush(StkPushRequest.builder()
 *         .amount(500)
 *         .partyA("0712345678")
 *         .accountReference("INV-0001")
 *         .build());
 * }</pre>
 */
public final class Daraja4jClient {

    private final Daraja4jGateway gateway;

    public Daraja4jClient(Daraja4jConfig config) {
        this.gateway = new Daraja4jGateway(config);
    }

    /** Triggers an M-Pesa STK push (USSD PIN prompt) to the payer's phone. */
    public StkPushResult stkPush(StkPushRequest request) {
        return gateway.execute("/mpesa/stkpush/v1/processrequest", request.toJson(gateway.getConfig()),
                StkPushResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #stkPush(StkPushRequest)}. */
    public CompletableFuture<StkPushResult> stkPushAsync(StkPushRequest request) {
        return CompletableFuture.supplyAsync(() -> stkPush(request));
    }

    /** Polls the outcome of a previously-submitted STK push. */
    public StkPushQueryResult stkPushQuery(StkPushQueryRequest request) {
        return gateway.execute("/mpesa/stkpushquery/v1/query", request.toJson(gateway.getConfig()),
                StkPushQueryResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #stkPushQuery(StkPushQueryRequest)}. */
    public CompletableFuture<StkPushQueryResult> stkPushQueryAsync(StkPushQueryRequest request) {
        return CompletableFuture.supplyAsync(() -> stkPushQuery(request));
    }

    /** Registers the validation/confirmation URLs Daraja calls for C2B payments to a shortcode. */
    public RegisterC2bUrlsResult registerC2bUrls(RegisterC2bUrlsRequest request) {
        return gateway.execute("/mpesa/c2b/v2/registerurl", request.toJson(gateway.getConfig()),
                RegisterC2bUrlsResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #registerC2bUrls(RegisterC2bUrlsRequest)}. */
    public CompletableFuture<RegisterC2bUrlsResult> registerC2bUrlsAsync(RegisterC2bUrlsRequest request) {
        return CompletableFuture.supplyAsync(() -> registerC2bUrls(request));
    }

    /** Simulates an inbound C2B payment - sandbox only. */
    public C2bSimulateResult simulateC2b(C2bSimulateRequest request) {
        return gateway.execute("/mpesa/c2b/v1/simulate", request.toJson(gateway.getConfig()),
                C2bSimulateResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #simulateC2b(C2bSimulateRequest)}. */
    public CompletableFuture<C2bSimulateResult> simulateC2bAsync(C2bSimulateRequest request) {
        return CompletableFuture.supplyAsync(() -> simulateC2b(request));
    }

    /** Sends money from a business shortcode to a registered M-Pesa customer. */
    public B2cResult b2c(B2cRequest request) {
        return gateway.execute("/mpesa/b2c/v3/paymentrequest", request.toJson(gateway.getConfig()),
                B2cResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #b2c(B2cRequest)}. */
    public CompletableFuture<B2cResult> b2cAsync(B2cRequest request) {
        return CompletableFuture.supplyAsync(() -> b2c(request));
    }

    /** Pays another organization's pay bill or till (Business Buy Goods) directly from this shortcode. */
    public B2bResult b2b(B2bRequest request) {
        return gateway.execute("/mpesa/b2b/v1/paymentrequest", request.toJson(gateway.getConfig()),
                B2bResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #b2b(B2bRequest)}. */
    public CompletableFuture<B2bResult> b2bAsync(B2bRequest request) {
        return CompletableFuture.supplyAsync(() -> b2b(request));
    }

    /** Pays a Pochi la Biashara-enabled personal till. */
    public B2PochiResult b2Pochi(B2PochiRequest request) {
        return gateway.execute("/mpesa/b2c/v3/paymentrequest", request.toJson(gateway.getConfig()),
                B2PochiResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #b2Pochi(B2PochiRequest)}. */
    public CompletableFuture<B2PochiResult> b2PochiAsync(B2PochiRequest request) {
        return CompletableFuture.supplyAsync(() -> b2Pochi(request));
    }

    /** Reverses a completed C2B transaction, refunding the payer. */
    public ReversalResult reverseTransaction(ReversalRequest request) {
        return gateway.execute("/mpesa/reversal/v1/request", request.toJson(gateway.getConfig()),
                ReversalResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #reverseTransaction(ReversalRequest)}. */
    public CompletableFuture<ReversalResult> reverseTransactionAsync(ReversalRequest request) {
        return CompletableFuture.supplyAsync(() -> reverseTransaction(request));
    }

    /** Checks the status of a prior transaction - a secondary reconciliation mechanism when a callback was missed. */
    public TransactionStatusResult queryTransactionStatus(TransactionStatusRequest request) {
        return gateway.execute("/mpesa/transactionstatus/v1/query", request.toJson(gateway.getConfig()),
                TransactionStatusResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #queryTransactionStatus(TransactionStatusRequest)}. */
    public CompletableFuture<TransactionStatusResult> queryTransactionStatusAsync(TransactionStatusRequest request) {
        return CompletableFuture.supplyAsync(() -> queryTransactionStatus(request));
    }

    /** Queries the current M-Pesa account balance of this shortcode. */
    public AccountBalanceResult queryAccountBalance(AccountBalanceRequest request) {
        return gateway.execute("/mpesa/accountbalance/v1/query", request.toJson(gateway.getConfig()),
                AccountBalanceResult::fromJson, "ResponseCode");
    }

    /** Asynchronous variant of {@link #queryAccountBalance(AccountBalanceRequest)}. */
    public CompletableFuture<AccountBalanceResult> queryAccountBalanceAsync(AccountBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> queryAccountBalance(request));
    }

    /** One-time registration of a shortcode for the Pull Transactions API. */
    public PullTransactionsRegisterResult registerPullTransactions(PullTransactionsRegisterRequest request) {
        return gateway.execute("POST", "/pulltransactions/v1/register", request.toJson(gateway.getConfig()),
                PullTransactionsRegisterResult::fromJson, null, null);
    }

    /** Asynchronous variant of {@link #registerPullTransactions(PullTransactionsRegisterRequest)}. */
    public CompletableFuture<PullTransactionsRegisterResult> registerPullTransactionsAsync(PullTransactionsRegisterRequest request) {
        return CompletableFuture.supplyAsync(() -> registerPullTransactions(request));
    }

    /**
     * Retrieves C2B transactions for a period, once registered with
     * {@link #registerPullTransactions(PullTransactionsRegisterRequest)}.
     * Daraja documents this endpoint as {@code GET} with a JSON body.
     */
    public PullTransactionsQueryResult queryPullTransactions(PullTransactionsQueryRequest request) {
        return gateway.execute("GET", "/pulltransactions/v1/query", request.toJson(gateway.getConfig()),
                PullTransactionsQueryResult::fromJson, null, null);
    }

    /** Asynchronous variant of {@link #queryPullTransactions(PullTransactionsQueryRequest)}. */
    public CompletableFuture<PullTransactionsQueryResult> queryPullTransactionsAsync(PullTransactionsQueryRequest request) {
        return CompletableFuture.supplyAsync(() -> queryPullTransactions(request));
    }

    /** Creates an M-Pesa Ratiba standing order - a commercial API, see Safaricom's M-Pesa Ratiba documentation for onboarding. */
    public StandingOrderResult createStandingOrder(StandingOrderRequest request) {
        return gateway.execute("POST", "/standingorder/v1/createStandingOrderExternal", request.toJson(gateway.getConfig()),
                StandingOrderResult::fromJson, null, null);
    }

    /** Asynchronous variant of {@link #createStandingOrder(StandingOrderRequest)}. */
    public CompletableFuture<StandingOrderResult> createStandingOrderAsync(StandingOrderRequest request) {
        return CompletableFuture.supplyAsync(() -> createStandingOrder(request));
    }

    /** Forces a fresh OAuth token fetch, discarding any cached one. Most applications never need to call this. */
    public String refreshToken() {
        return gateway.refreshToken();
    }

    /** Access the underlying low-level execution engine. */
    public Daraja4jGateway getGateway() {
        return gateway;
    }
}
