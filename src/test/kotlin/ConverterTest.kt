import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.NoSuchElementException

internal class ConverterTest {
    private val converter = Converter()

    @Test
    fun readToken() {
        val file1 = File("file1").apply {
            createNewFile()
            val bw = bufferedWriter()
            bw.write(name)
            bw.close()
        }
        val file2 = File("Lorem ipsum").apply {
            createNewFile()
            val bw = bufferedWriter()
            bw.write(name)
            bw.close()
        }

        assertEquals(file1.name, converter.readToken(file1.name))
        assertEquals(file2.name, converter.readToken(file2.name))
        assertNull(converter.readToken("NoSuchFile"))

        file1.delete()
        file2.delete()
    }

    @Test
    fun getRes() {
        assertEquals(
            "1.0 USD = 0.8 EUR (for 20:23 13.12.2021)",
            converter.getRes(1.0, Response(1639416185L, mapOf("EUR" to 0.8)))
        )
        assertEquals(
            "125.0 USD = 529.25 EUR (for 17:56 22.08.2008)",
            converter.getRes(125.0, Response(1219413385L, mapOf("EUR" to 4.234)))
        )
        assertEquals(
            "125.0 USD = 529.25 EUR (for 17:56 22.08.2008)",
            converter.getRes(125.0, Response(1219413385L, mapOf("EUR" to 4.234, "RUB" to 0.001)))
        )
        assertThrows(NoSuchElementException::class.java) { converter.getRes(125.0, Response(1639416185L, mapOf())) }
        assertThrows(NoSuchElementException::class.java) {
            converter.getRes(
                125.0,
                Response(1639416185L, mapOf("USD" to 1.0))
            )
        }
    }

    @Test
    fun request() {
        if (File("token.txt").exists()) {
            val res = converter.request(25.0)
            assertNotNull(res)
            assertTrue(
                res!!.matches(Regex("""[0-9]+.[0-9]+ USD = [0-9]+.[0-9]+ EUR \(for [0-2][0-9]:[0-5][0-9] [0-3][0-9].[0-1][0-9].[0-9]{4}\)"""))
            )
        } else {
            assertNull(converter.request(25.0))
        }
        assertNull(converter.request(25.0, "wrongToken"))
    }
}