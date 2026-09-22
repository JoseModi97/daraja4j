package io.github.josemodi97.daraja4j.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Entry point for the daraja4j command-line tool: trigger an STK push, poll
 * transaction status, disburse with B2C, reverse a transaction, check a
 * balance, and parse a captured callback payload without writing any
 * application code.
 */
@Command(
        name = "daraja4j",
        mixinStandardHelpOptions = true,
        version = "daraja4j CLI",
        description = "Command-line companion to the daraja4j Java SDK.",
        subcommands = {StkPushCommand.class, StatusCommand.class, B2cCommand.class,
                ReversalCommand.class, BalanceCommand.class, ParseCallbackCommand.class})
public final class Daraja4jCli implements Runnable {

    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Daraja4jCli()).execute(args);
        System.exit(exitCode);
    }
}
