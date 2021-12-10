fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usae: java -jar USD-EUR-converter.jar num")
        return
    }
    val num = args[0].toIntOrNull()

    if (num == null) {
        println("Usage: java -jar USD-EUR-converter.jar num")
        return
    }
}