import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress


fun main() {
    val port = 8888
    val server = HttpServer.create()
    server.bind(InetSocketAddress(port), 0)
    val converter = Converter()

    server.createContext("/api/convert") { exchange: HttpExchange ->
        val amount = parseQuery(exchange.requestURI.query)

        if (amount == null) {
            exchange.sendResponseHeaders(400, -1)
            exchange.close()
        } else {
            val res = converter.request(if (amount < 0) 1 else amount)
            exchange.sendResponseHeaders(200, res.toByteArray().size.toLong())
            val output = exchange.responseBody
            output.write(res.toByteArray())
            output.flush()
            exchange.close()
        }
    }

    server.executor = null
    server.start()
    println("Сервер запущен")
}

fun parseQuery(query: String?): Int? {
    if (query.isNullOrEmpty()) return null
    val args = query.split("&")
    if (args.size != 1) return null

    val amountArg = args.first().split("=")
    if (amountArg.size != 2 || amountArg.first() != "amount") return null

    return amountArg[1].toInt()
}