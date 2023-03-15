package frame

import javax.swing.JFrame

import java.awt.*
import java.awt.event.*
import javax.swing.*

class GameFrame(title: String) : JFrame(title), ActionListener {
    private val DELAY = 100
    private val INITIAL_SNAKE_SIZE = 3
    private val BLOCK_SIZE = 20
    private val GAME_WIDTH = 600
    private val GAME_HEIGHT = 400
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

        for (i in 0 until INITIAL_SNAKE_SIZE) {
            snake.add(Point(GAME_WIDTH / 2 - BLOCK_SIZE * i, GAME_HEIGHT / 2))
        }

        placeApple()

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

        isRunning = true
        timer?.start()
        add(pointsLabel, BorderLayout.NORTH)
    }

    override fun actionPerformed(e: ActionEvent?) {
        moveSnake()
        checkCollisions()
        repaint()
    }

    private fun placeApple() {
        apple = Point((Math.random() * (GAME_WIDTH / BLOCK_SIZE)).toInt() * BLOCK_SIZE,
            (Math.random() * (GAME_HEIGHT / BLOCK_SIZE)).toInt() * BLOCK_SIZE)

        points++
        pointsLabel.text = "Points: $points"

        if(points == 5) {
            gameWon()
        }
    }

    private fun moveSnake() {
        val head = snake.first().location
        when (direction) {
            Direction.LEFT -> head.x -= BLOCK_SIZE
            Direction.RIGHT -> head.x += BLOCK_SIZE
            Direction.UP -> head.y -= BLOCK_SIZE
            Direction.DOWN -> head.y += BLOCK_SIZE
        }
        snake.add(0, head)
        snake.removeLast()
    }

    private fun checkCollisions() {
        val head = snake.first()
        if (head.x < 0 || head.x >= GAME_WIDTH || head.y < 0 || head.y >= GAME_HEIGHT) {
            gameOver()
        } else {
            for (i in 1 until snake.size) {
                if (head == snake[i]) {
                    gameOver()
                    break
                }
            }
        }
        if (head == apple) {
            if (snake.size < GAME_WIDTH * GAME_HEIGHT / BLOCK_SIZE / BLOCK_SIZE) {
                snake.add(snake.last().location)
                placeApple()
            } else {
                gameOver()
            }
        }

    }


    private fun gameOver() {
        isRunning = false
        timer?.stop()

        JOptionPane.showMessageDialog(this, "Game over!", "Snake - Game over", JOptionPane.INFORMATION_MESSAGE)

        dispose()
    }

    private fun gameWon() {
        isRunning = false
        timer?.stop()

        JOptionPane.showMessageDialog(this, "You won!", "Snake - You won", JOptionPane.INFORMATION_MESSAGE)

        dispose()
    }

    override fun paint(g: Graphics?) {
        super.paint(g)

        g?.color = Color.GREEN
        for (point in snake) {
            g?.fillRect(point.x, point.y, BLOCK_SIZE, BLOCK_SIZE)
        }

        g?.color = Color.RED
        g?.fillRect(apple?.x ?: 0, apple?.y ?: 0, BLOCK_SIZE, BLOCK_SIZE)
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }
}