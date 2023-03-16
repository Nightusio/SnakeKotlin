package frame

import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import kotlin.system.exitProcess

class StartFrame(title: String) : JFrame(title) {

    private val GAME_WIDTH = 800
    private val GAME_HEIGHT = 800

    init {
        val startMenu = JMenu("Start")
        val startMenuItem = JMenuItem("Start Game")
        startMenu.add(startMenuItem)

        val quitMenu = JMenu("Quit")
        val quitMenuItem = JMenuItem("Quit Game")
        quitMenu.add(quitMenuItem)

        val menuBar = JMenuBar()
        menuBar.add(startMenu)
        menuBar.add(quitMenu)

        jMenuBar = menuBar
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(GAME_WIDTH, GAME_HEIGHT)
        isVisible = true

        startMenuItem.addActionListener {
            GameFrame("Snake - Game")
            dispose()
        }

        quitMenuItem.addActionListener {
            exitProcess(0)
        }
    }
}