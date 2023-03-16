package frame

import javax.swing.*
import java.awt.*
import java.awt.event.*
import kotlin.concurrent.thread

class GameFrame(title: String) : JFrame(title), ActionListener {
    private val DELAY = 80 // Decreased delay for faster updates
    private val INITIAL_SNAKE_SIZE = 3
    private val BLOCK_SIZE = 20
    private val GAME_WIDTH = 800
    private val GAME_HEIGHT = 800
    private var points = 0
    private val pointsLabel = JLabel("Points: $points")

    private val snake: MutableList<Point> = mutableListOf()
    private var direction = Direction.RIGHT
    private var apple: Point? = null
    private var isRunning = false
    private var timer: Timer? = null

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(GAME_WIDTH, GAME_HEIGHT)
        isVisible = true

        timer = Timer(DELAY, this)

        // Initialize snake with initial size and position
        for (i in 0 until INITIAL_SNAKE_SIZE) {
            snake.add(Point(GAME_WIDTH / 2 - BLOCK_SIZE * i, GAME_HEIGHT / 2))
        }

        // Place the first apple
        placeApple()

        // Listen for key presses to change direction
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_LEFT -> if (direction != Direction.RIGHT) direction = Direction.LEFT
                    KeyEvent.VK_RIGHT -> if (direction != Direction.LEFT) direction = Direction.RIGHT
                    KeyEvent.VK_UP -> if (direction != Direction.DOWN) direction = Direction.UP
                    KeyEvent.VK_DOWN -> if (direction != Direction.UP) direction = Direction.DOWN
                }
            }
        })

        // Start the game loop
        isRunning = true
        timer?.start()

        // Add points label to the top of the window
        add(pointsLabel, BorderLayout.NORTH)
    }

    override fun actionPerformed(e: ActionEvent?) {
        // Check if the player has won
        if (points == 30) {
            gameWon()
            return
        }

        // Move the snake and check for collisions
        thread {
            moveSnake()
            checkCollisions()
            repaint() // Moved repaint call to the thread to update the UI on a separate thread
        }
    }

    private fun placeApple() {
        // Randomly place the apple within the inner 50% of the game area
        val rangeX = GAME_WIDTH / BLOCK_SIZE / 4..(GAME_WIDTH / BLOCK_SIZE * 3 / 4)
        val rangeY = GAME_HEIGHT / BLOCK_SIZE / 4..(GAME_HEIGHT / BLOCK_SIZE * 3 / 4)
        apple = Point((rangeX.random() * BLOCK_SIZE), (rangeY.random() * BLOCK_SIZE))
    }

    private fun moveSnake() {
        // Move the snake's head in the current direction
        val head = snake.first().location
        when (direction) {
            Direction.LEFT -> head.x -= BLOCK_SIZE
            Direction.RIGHT -> head.x += BLOCK_SIZE
            Direction.UP -> head.y -= BLOCK_SIZE
            Direction.DOWN -> head.y += BLOCK_SIZE
        }
        // Add the new head position to the beginning of the snake list and remove the tail
        snake.add(0, head)
        snake.removeLast()
    }

    private fun checkCollisions() {
        val head = snake.first()

        // Check for collision with walls
        if (head.x < 0 || head.x >= GAME_WIDTH || head.y < 0 || head.y >= GAME_HEIGHT)

        // Game over if the snake hits the wall
        gameOver()

        // Check for collision with apple
        apple?.let {
            if (head == it) {
                // Increase points and update label
                points++
                pointsLabel.text = "Points: $points"

                // Add a new block to the snake and place a new apple
                snake.add(snake.last())
                placeApple()
            }
        }

        // Check for collision with snake's body
        for (i in 1 until snake.size) {
            if (head == snake[i]) {
                // Game over if the snake hits its own body
                gameOver()
            }
        }
    }

    private fun gameOver() {
        // Stop the game loop and display a message dialog
        isRunning = false
        timer?.stop()
        JOptionPane.showMessageDialog(this, "Game over! Your score is $points", "Game over", JOptionPane.INFORMATION_MESSAGE)
        dispose()
    }

    private fun gameWon() {
        // Stop the game loop and display a message dialog
        isRunning = false
        timer?.stop()
        JOptionPane.showMessageDialog(this, "You won! Your score is $points", "You won!", JOptionPane.INFORMATION_MESSAGE)
        dispose()
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        g as Graphics2D

        // Draw the snake
        g.color = Color.GREEN
        for (block in snake) {
            g.fillRect(block.x, block.y, BLOCK_SIZE, BLOCK_SIZE)
        }

        // Draw the apple
        g.color = Color.RED
        apple?.let {
            g.fillOval(it.x, it.y, BLOCK_SIZE, BLOCK_SIZE)
        }
    }

}

enum class Direction {
    LEFT, RIGHT, UP, DOWN
}