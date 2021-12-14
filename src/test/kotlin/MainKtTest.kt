import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

internal class MainKtTest {

    @Test
    fun parseQuery() {
        assertEquals(25.0, parseQuery("amount=25"))
        assertEquals(-25.0, parseQuery("amount=-25"))
        assertEquals(0.125, parseQuery("amount=0.125"))
        assertNull(parseQuery(""))
        assertNull(parseQuery("amount=0.125&test=2"))
        assertNull(parseQuery("test=0.125"))
        assertNull(parseQuery("amount"))
    }

    @Test
    fun mainTest() {
        main()
        var url = URL("http://127.0.0.1:8888/api/convert?amount")
        var connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        assertEquals(400, connection.responseCode)

        url = URL("http://127.0.0.1:8888/api/convert?amount=25&error=1")
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        assertEquals(400, connection.responseCode)

        if (File("token.txt").exists()) {
            url = URL("http://127.0.0.1:8888/api/convert?amount=25")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            assertTrue(
                connection.inputStream.bufferedReader().readText()
                    .matches(Regex("""[0-9]+.[0-9]+ USD = [0-9]+.[0-9]+ EUR \(for [0-2][0-9]:[0-5][0-9] [0-3][0-9].[0-1][0-9].[0-9]{4}\)"""))
            )

            url = URL("http://127.0.0.1:8888/api/convert?amount=-25")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            assertTrue(
                connection.inputStream.bufferedReader().readText()
                    .matches(Regex("""[0-9]+.[0-9]+ USD = [0-9]+.[0-9]+ EUR \(for [0-2][0-9]:[0-5][0-9] [0-3][0-9].[0-1][0-9].[0-9]{4}\)"""))
            )

            url = URL("http://127.0.0.1:8888/api/convert?amount=0")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            assertTrue(
                connection.inputStream.bufferedReader().readText()
                    .matches(Regex("""1.0 USD = [0-9]+.[0-9]+ EUR \(for [0-2][0-9]:[0-5][0-9] [0-3][0-9].[0-1][0-9].[0-9]{4}\)"""))
            )

        }

    }
}