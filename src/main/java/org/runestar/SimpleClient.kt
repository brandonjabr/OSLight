package org.runestar

import org.runestar.general.JavConfig
import org.runestar.general.downloadGamepack
import org.runestar.general.updateRevision
import java.awt.Color
import java.awt.Dimension
import java.io.IOException
import java.net.URLClassLoader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.WindowConstants
import javax.swing.border.LineBorder
import java.awt.Image;
import javax.imageio.ImageIO
import java.net.URL;
import javax.swing.ImageIcon;


val KratosColor = Color(0xFFCC00)
val bgColor = Color(0x2B2B2B)
fun main(args: Array<String>) {


    val revision = updateRevision()
    System.setProperty("sun.awt.noerasebackground", true.toString()) // fixes resize flickering
    val jar = Paths.get(System.getProperty("java.io.tmpdir"), "runescape-gamepack.$revision.jar")
    try {
        JarFile(jar.toFile(), true).close()
    } catch (e: IOException) {
        // jar does not exist or was partially downloaded
        downloadGamepack(jar)
    }
    launch(jar)
}

fun launch(
        gamepack: Path,
        javConfig: JavConfig = JavConfig.load()
) {
    val classLoader = URLClassLoader(arrayOf(gamepack.toUri().toURL()))
    val clientConstructor = classLoader.loadClass(javConfig.initialClass).getDeclaredConstructor()
    @Suppress("DEPRECATION")
    val client = clientConstructor.newInstance() as java.applet.Applet
    client.apply {
        layout = null // fixes resize bouncing
        setStub(JavConfig.AppletStub(javConfig))
        minimumSize = Dimension(200, 350)
        maximumSize = javConfig.appletMaxSize
        preferredSize = javConfig.appletMinSize
        size = preferredSize
    }

    var image: Image? = null
    try {
        val url = URL("https://i.imgur.com/AO4QGtx.png")
        image = ImageIO.read(url)
    } catch (e: IOException) {
        e.printStackTrace()
    }


    JFrame("OSLight v2.1").apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        add(client)
        //rootPane.border = LineBorder(KratosColor,2)

        pack()
        setLocationRelativeTo(null)
        isVisible = true
        preferredSize = size
        minimumSize = client.minimumSize
        background = KratosColor
        iconImage = image
    }
    client.apply {
        init()
        start()
    }
}