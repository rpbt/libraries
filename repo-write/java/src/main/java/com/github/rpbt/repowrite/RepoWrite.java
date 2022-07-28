package com.github.rpbt.repowrite;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RepoWrite {
    private boolean writeOnly = false;
    private int port = 4136;
    private String host;

    public static void main(String[] args) throws IOException {
        RepoWrite repoWrite = new RepoWrite();
        repoWrite.parseCliArguments(args);
        repoWrite.start();
    }

    public void parseCliArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--help".equals(arg) || "-h".equals(arg)) {
                this.sendHelp();
                return;
            }
            if ("--writeOnly".equals(arg)) {
                this.writeOnly = true;
            }
            if ("--port".equals(arg)) {
                if (i + 1 >= args.length) {
                    System.err.println("Please specify the port after --port, for example --port 80");
                    return;
                }
                String nextArg = args[i + 1];
                try {
                    this.port = Integer.parseInt(nextArg);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port \"" + nextArg + "\"");
                    return;
                }
                i++;
            }
            if ("--host".equals(arg) || "--hostname".equals(arg)) {
                if (i + 1 >= args.length) {
                    System.err.println("Please specify the host after --host, for example --host 0.0.0.0");
                    return;
                }
                this.host = args[i + 1];
            }
        }
    }

    public void sendHelp() {
        System.out.println("repo-write");
        System.out.println("--help, -h     Show this help message.");
        System.out.println("--writeOnly    Set to make repo-write only allow PUT and DELETE requests.");
        System.out.println("--port <port>  Set the port to listen on.");
        System.out.println("--host <host>  Set the host to listen on.");
    }

    public void start() throws IOException {
        if (this.writeOnly) {
            System.out.println("Running as write only");
        }
        InetSocketAddress address;
        if (this.host != null) {
            address = new InetSocketAddress(this.host, this.port);
        } else {
            address = new InetSocketAddress(this.port);
        }

        HttpServer httpServer = HttpServer.create(address, 0);
        httpServer.createContext("/", new RequestHandler(this));
        httpServer.setExecutor(null);
        httpServer.start();

        System.out.println("Running on port " + this.port);
    }

    public boolean isWriteOnly() {
        return writeOnly;
    }
}
