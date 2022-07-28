package com.github.rpbt.repowrite;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.net.HttpURLConnection.*;

public class RequestHandler implements HttpHandler {
    private final RepoWrite repoWrite;

    public RequestHandler(RepoWrite repoWrite) {
        this.repoWrite = repoWrite;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                this.handleGetRequest(exchange);
            } else if ("PUT".equals(method)) {
                this.handlePutRequest(exchange);
            } else if ("DELETE".equals(method)) {
                this.handleDeleteRequest(exchange);
            } else {
                exchange.sendResponseHeaders(HTTP_BAD_METHOD, 0);
                exchange.getRequestBody().close();
            }
        } catch (Throwable e) {
            // No response yet?
            if (exchange.getResponseCode() == -1) {
                // send internal server error
                exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, 0);
                exchange.getResponseBody().close();
            }
            e.printStackTrace();
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        if (this.repoWrite.isWriteOnly()) {
            this.send(exchange, HTTP_BAD_METHOD, "This repository is write only");
        } else {
            if (this.ensureAllowedRequest(exchange)) return;
            String path = exchange.getRequestURI().getPath();
            Path file = Paths.get(path.substring(1));
            if (Files.isRegularFile(file)) {
                long size = Files.size(file);
                try (InputStream inputStream = Files.newInputStream(file)) {
                    exchange.sendResponseHeaders(HTTP_OK, size);
                    OutputStream body = exchange.getResponseBody();
                    this.transfer(inputStream, body);
                    body.close();
                }
            } else {
                this.send(exchange, HTTP_NOT_FOUND, "Not Found");
            }
        }
    }

    private void handlePutRequest(HttpExchange exchange) throws IOException {
        if (this.ensureAllowedRequest(exchange)) return;
        String path = exchange.getRequestURI().getPath();
        Path file = Paths.get(path.substring(1));
        System.out.println("Uploading " + file);
        Files.createDirectories(file.getParent());
        try (OutputStream outputStream = Files.newOutputStream(file)) {
            this.transfer(exchange.getRequestBody(), outputStream);
        }
        this.send(exchange, HTTP_OK, "The file was written");
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        if (this.ensureAllowedRequest(exchange)) return;
        String path = exchange.getRequestURI().getPath();
        Path file = Paths.get(path.substring(1));
        if (Files.exists(file)) {
            if (Files.isRegularFile(file)) {
                System.out.println("Deleting " + file);
                Files.delete(file);
                this.send(exchange, HTTP_OK, "File deleted");
            } else if (Files.isDirectory(file)) {
                System.out.println("Deleting directory " + file + "...");
                Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        System.out.println("    Deleting " + file);
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Deleted directory " + file);
            } else {
                this.send(exchange, HTTP_INTERNAL_ERROR, "Invalid path");
            }
        } else {
            this.send(exchange, HTTP_OK, "Already deleted");
        }
    }

    /**
     * Ensure the request is permitted. If not send a response.
     *
     * @param exchange The http exchange.
     * @return Whether a response was sent.
     * @throws IOException If an I/O error occurs.
     */
    private boolean ensureAllowedRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!this.isAllowedPath(path)) {
            this.send(exchange, HTTP_BAD_REQUEST, "Invalid path");
            return true;
        }
        return false;
    }

    private boolean isAllowedPath(String path) {
        String[] parts = path.split("/", -1);
        // -1 to allow trailing empty strings, this ensures DELETE requests to a
        // resource folder is allowed.
        if (parts.length < 5) return false;
        if (path.contains("..")) return false;
        if (!path.startsWith("/plugin/") && !path.startsWith("/dependency/")) return false;
        return path.startsWith("/");
    }

    private void send(HttpExchange exchange, int code, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, response.length);
        OutputStream stream = exchange.getResponseBody();
        stream.write(response);
        stream.close();
    }

    private void transfer(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer, 0, 8192)) >= 0) {
            outputStream.write(buffer, 0, read);
        }
    }
}
