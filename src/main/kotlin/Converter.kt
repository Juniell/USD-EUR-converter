import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.system.exitProcess

class Converter(private val log: Boolean = true) {
    private val baseUrl = "https://openexchangerates.org/api/latest.json"
    private val token: String =
        try {
            val buffReader = File("token.txt").bufferedReader()
            buffReader.readLine()
        } catch (e: FileNotFoundException) {
            println("The file \"token.txt\" with the token for openexchangerates.org was not found.")
            exitProcess(0)
        }

    fun request(amount: Int): String {
        val url = URL("${baseUrl}?app_id=${token}&base=USD&symbols=EUR")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        val inputStream = connection.inputStream

        try {
            val response: Response = jacksonObjectMapper().readValue(inputStream)
            val res = getRes(amount, response)
            if (log)
                println(res)
            return res

        } catch (e: JacksonException) {
            if (log) {
                println("Ошибка при конвертации ответа от сервера.")
                println(inputStream.bufferedReader().readText())
            }
            exitProcess(0)
        }
    }

    private fun getRes(amount: Int, response: Response): String {
        val euro = amount * response.rates["EUR"]!!
        val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy")
        val t = sdf.format(Date(response.timestamp * 1000))
        return "$amount USD = $euro EUR (for $t)"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(
    val timestamp: Long,
    val rates: Map<String, Double>
)