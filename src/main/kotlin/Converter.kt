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
    private val token: String = readToken("token.txt") ?: exitProcess(0)

    fun request(amount: Double, token: String = this.token): String? {
        val url = URL("${baseUrl}?app_id=${token}&base=USD&symbols=EUR")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode != 200) {
            if (log) {
                println("Ошибка ${connection.responseCode}.")
                println(connection.errorStream.bufferedReader().readText())
            }
            return null
        }

        try {
            val inputStream = connection.inputStream
            val response: Response = jacksonObjectMapper().readValue(inputStream)
            val res = getRes(amount, response)
            if (log)
                println(res)
            return res
        } catch (e: JacksonException) {
            if (log) {
                println("Ошибка при конвертации ответа от сервера.")
                println(connection.errorStream.bufferedReader().readText())
            }
            return null
        } catch (e: NoSuchElementException) {
            if (log) {
                println("Сервер не выдал ответ для EUR.")
                println(connection.errorStream.bufferedReader().readText())
            }
            return null
        }
    }

    fun readToken(path: String): String? {
        return try {
            val buffReader = File(path).bufferedReader()
            val res = buffReader.readLine()
            buffReader.close()
            res
        } catch (e: FileNotFoundException) {
            println("The file \"token.txt\" with the token for openexchangerates.org was not found.")
            null
        }
    }

    fun getRes(amount: Double, response: Response): String {
        val rate = response.rates["EUR"] ?: throw NoSuchElementException()
        val euro = amount * rate
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