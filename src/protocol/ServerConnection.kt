package protocol

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ServerConnection(val url: String, val port: Int) {

    private val socket = Socket(url, port.toInt())
    val sin = BufferedReader(InputStreamReader(socket.getInputStream()))
    val sout = PrintWriter(socket.getOutputStream(), true)

    fun <T> sendJson(json: T) {
        val jsonString = objectMapper.writeValueAsString(json)
        sout.println("${jsonString.length}:${jsonString}")
        sout.flush()
    }

    inline fun <reified T> receiveJson(): T {
        val lengthChars = mutableListOf<Char>()
        var ch = '0'
        while (ch != ':') {
            lengthChars += ch
            ch = sin.read().toChar()
        }
        val length = lengthChars.joinToString("").trim().toInt()
        val contentAsArray = CharArray(length)
        var start = 0
        while (start < length) {
            val read = sin.read(contentAsArray, start, length - start)
            start += read
        }
        return objectMapper.readValue(String(contentAsArray), T::class.java)
    }
}
