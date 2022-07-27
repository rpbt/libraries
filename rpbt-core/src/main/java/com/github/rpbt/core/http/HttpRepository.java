package com.github.rpbt.core.http;

import com.github.rpbt.core.Resource;
import com.github.rpbt.core.ResourceNotFoundException;
import com.github.rpbt.core.ResourceType;
import com.github.rpbt.core.RpbtRepository;
import com.github.rpbt.core.RpbtUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * An {@link RpbtRepository} on an HTTP server.
 */
public class HttpRepository implements RpbtRepository {
    public static final String USER_AGENT = "rpbt-core java/" + System.getProperty("java.version");

    private final String url;

    /**
     * Create a new instance.
     *
     * @param url The url to root of the repository.
     * @throws IllegalArgumentException If the url is invalid or does not use http(s).
     */
    public HttpRepository(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        this.url = url;
        this.validateUrl();
    }

    private void validateUrl() {
        try {
            URL parsedUrl = new URL(this.url);
            if (!parsedUrl.getProtocol().startsWith("http")) {
                throw new IllegalArgumentException(
                    "RemoteRpbtRepository URL must use the http(s) protocol, " +
                        "got protocol: " + parsedUrl.getProtocol()
                );
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url: " + url);
        }
    }

    public HttpResource getResource(ResourceType type, String id, String version) throws IOException, ResourceNotFoundException {
        String baseUrl = this.url + type.getFolder() + "/" + id + "/" + version + "/";

        try {
            URL url = URI.create(baseUrl + "resource.json").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONParser parser = new JSONParser();
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JSONObject json = (JSONObject) parser.parse(reader);
                JSONObject hash = (JSONObject) json.get("hash");
                if (hash == null || !hash.containsKey("sha1")) {
                    throw new IOException("Outdated resource. It does not contain a SHA-1 hash.");
                }
                String sha1 = hash.get("sha1").toString();
                return new HttpResource(type, id, version, sha1, baseUrl);
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new ResourceNotFoundException(
                    "The resource "+ id + ':' + version +" of type " + type
                        + " was not found in HttpRepository with url " + this.url
                );
            } else {
                throw new IOException(
                    "Unexpected HTTP code: " + responseCode + ": " + connection.getResponseMessage()
                );
            }
        } catch (ProtocolException | MalformedURLException | ParseException | ClassCastException | NullPointerException e) {
            throw new IOException(e);
        }
    }

    private HttpResource getHttpResource(Resource resource) {
        if (resource instanceof HttpResource) {
            return (HttpResource) resource;
        } else {
            throw new IllegalArgumentException(
                "A HttpRepository can only handle HttpResource resources."
            );
        }
    }

    /**
     * Get the URL to the zip file of the specified resource.
     *
     * @param resource The HttpResource.
     * @return The URL to the zip file.
     */
    public String getUrl(Resource resource) {
        HttpResource httpResource = this.getHttpResource(resource);
        String shortId = RpbtUtils.getShortId(resource.getId());
        return httpResource.getBaseUrl() + shortId + '-' + resource.getVersion() + ".zip";
    }

    @Override
    public InputStream readResource(Resource resource) throws IOException {
        URL url;
        try {
            url = new URI(this.getUrl(resource)).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException("Failed to parse resource url", e);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        return connection.getInputStream();
    }
}
