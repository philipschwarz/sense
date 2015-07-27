package com.mechanitis.demo.sense.twitter;

import com.mechanitis.demo.sense.infrastructure.BroadcastingServerEndpoint;
import com.mechanitis.demo.sense.infrastructure.DaemonThreadFactory;
import com.mechanitis.demo.sense.infrastructure.WebSocketServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static java.lang.ClassLoader.getSystemResource;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Reads tweets from a file and sends them to the Twitter Service endpoint.
 */
public class CannedTweetsService implements Runnable {
    private final ExecutorService executor = newSingleThreadExecutor(new DaemonThreadFactory());
    private final BroadcastingServerEndpoint<String> tweetsEndpoint = new BroadcastingServerEndpoint<>();
    private final WebSocketServer server = new WebSocketServer("/tweets/", 8081, tweetsEndpoint);
    private final Path filePath;

    public CannedTweetsService(Path filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) throws URISyntaxException {
        new CannedTweetsService(Paths.get(getSystemResource("./tweetdata60-mins.txt").toURI())).run();
    }

    @Override
    public void run() {
        executor.submit(server);
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.filter(s -> !s.equals("OK"))
                 .forEach(tweetsEndpoint::onMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws Exception {
        server.stop();
        executor.shutdownNow();
    }
}
