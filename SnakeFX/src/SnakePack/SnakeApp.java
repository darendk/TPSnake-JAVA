package SnakePack;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeApp extends Application {

    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 15;

    private List<BodyPart> snake = new ArrayList<>();
    private BodyPart food;
    private Direction direction = Direction.RIGHT;
    private boolean isGameOver = false;
    private int score = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        BorderPane gamePane = new BorderPane(canvas);

        VBox root = new VBox(gamePane);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            if (!isGameOver) {
                moveSnake();
                checkCollision();
                checkFood();
                draw(gc);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        initializeGame();

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            initializeGame();
            isGameOver = false;
            score = 0;
        });

        HBox buttonBox = new HBox(restartButton);
        buttonBox.setAlignment(Pos.CENTER);
        gamePane.setBottom(buttonBox);

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGame() {
        snake.clear();
        snake.add(new BodyPart(WIDTH / 2, HEIGHT / 2));
        spawnFood();

        isGameOver = false;
        score = 0;
    }

    private void handleKeyPress(KeyCode code) {
        switch (code) {
            case UP:
                if (direction != Direction.DOWN)
                    direction = Direction.UP;
                break;
            case DOWN:
                if (direction != Direction.UP)
                    direction = Direction.DOWN;
                break;
            case LEFT:
                if (direction != Direction.RIGHT)
                    direction = Direction.LEFT;
                break;
            case RIGHT:
                if (direction != Direction.LEFT)
                    direction = Direction.RIGHT;
                break;
		default:
			break;
        }
    }

    private void moveSnake() {
        int headX = snake.get(0).getX();
        int headY = snake.get(0).getY();

        switch (direction) {
            case UP:
                snake.add(0, new BodyPart(headX, headY - 1));
                break;
            case DOWN:
                snake.add(0, new BodyPart(headX, headY + 1));
                break;
            case LEFT:
                snake.add(0, new BodyPart(headX - 1, headY));
                break;
            case RIGHT:
                snake.add(0, new BodyPart(headX + 1, headY));
                break;
        }

        if (headX < 0 || headX >= WIDTH || headY < 0 || headY >= HEIGHT) {
            isGameOver = true;
        }
    }

    private void checkCollision() {
        int headX = snake.get(0).getX();
        int headY = snake.get(0).getY();

        for (int i = 1; i < snake.size(); i++) {
            if (headX == snake.get(i).getX() && headY == snake.get(i).getY()) {
                isGameOver = true;
                break;
            }
        }
    }

    private void checkFood() {
        int headX = snake.get(0).getX();
        int headY = snake.get(0).getY();

        if (headX == food.getX() && headY == food.getY()) {
            score++;
            spawnFood();
        } else {
            // If no food is eaten, remove the tail
            snake.remove(snake.size() - 1);
        }
    }

    private void spawnFood() {
        Random rand = new Random();
        int foodX, foodY;
        do {
            foodX = rand.nextInt(WIDTH);
            foodY = rand.nextInt(HEIGHT);
        } while (isFoodOnSnake(foodX, foodY));

        food = new BodyPart(foodX, foodY);
    }

    private boolean isFoodOnSnake(int x, int y) {
        for (BodyPart part : snake) {
            if (part.getX() == x && part.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Draw food
        gc.setFill(Color.RED);
        gc.fillRect(food.getX() * TILE_SIZE, food.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw snake
        for (BodyPart part : snake) {
            gc.setFill(Color.GREEN);
            gc.fillRect(part.getX() * TILE_SIZE, part.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw score
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 15);

        if (isGameOver) {
            gc.setFill(Color.RED);
            gc.fillText("Game Over", WIDTH * TILE_SIZE / 2 - 50, HEIGHT * TILE_SIZE / 2);
        }
    }

    private static class BodyPart {
        private int x;
        private int y;

        public BodyPart(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}

