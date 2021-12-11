import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.system.exitProcess

class Converter(private val amount: Int) {
    private val baseUrl = "https://openexchangerates.org/api/latest.json"
    private val token: String =
        try {
            val buffReader = File("token.txt").bufferedReader()
            buffReader.readLine()
        } catch (e: FileNotFoundException) {
            println("The file \"token.txt\" with the token for openexchangerates.org was not found.")
            exitProcess(0)
        }

    fun request() {
        val url = URL("${baseUrl}?app_id=${token}&base=USD&symbols=EUR")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"

        val inputStream = connection.inputStream
        val response: Response = jacksonObjectMapper().readValue(inputStream)
        println("1 USD = ${response.rates["EUR"]} EUR")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(
    val timestamp: Long,
    val rates: Map<String, Double>
)