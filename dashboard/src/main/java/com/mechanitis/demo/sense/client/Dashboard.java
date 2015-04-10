package com.mechanitis.demo.sense.client;

import com.mechanitis.demo.sense.client.mood.HappinessChartData;
import com.mechanitis.demo.sense.client.mood.MoodChartData;
import com.mechanitis.demo.sense.client.mood.MoodsParser;
import com.mechanitis.demo.sense.client.mood.TweetMood;
import com.mechanitis.demo.sense.client.user.LeaderboardData;
import com.mechanitis.demo.sense.infrastructure.ClientEndpoint;
import com.mechanitis.demo.sense.infrastructure.MessageHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URI;

import static java.net.URI.create;

public class Dashboard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: wire up the models to the services they're getting the data from
        LeaderboardData leaderboardData = new LeaderboardData();
        ClientEndpoint<String> userEndpoint = new ClientEndpoint<>(create("ws://localhost:8083/users/"),
                twitterHandle -> twitterHandle);
        userEndpoint.addListener(leaderboardData);
        userEndpoint.connect();

        // initialise the UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        primaryStage.setTitle("Twitter Dashboard");
        Scene scene = new Scene(loader.load(), 1024, 1024);
        scene.getStylesheets().add("dashboard.css");

        // TODO: get each of the controllers and wire up the models
        DashboardController dashboardController = loader.getController();
        dashboardController.getLeaderboardController().setData(leaderboardData);

        // let's go!
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
