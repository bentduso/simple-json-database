package database.server

import java.util.*

sealed class Command {
    data class Set(val index: Int, val value: List<String>) : Command()
    data class Get(val index: Int) : Command()
    data class Delete(val index: Int) : Command()
    data object Exit : Command()
    data object Invalid : Command()
}

class Database {
    private val data: MutableList<String?> = MutableList(MAX_SIZE) { "" }

    companion object {
        private const val MAX_SIZE: Int = 100
    }

    init {
        generateSequence { readlnOrNull()?.split(" ") }
            .map { parseCommand(it) }
            .takeWhile { it != Command.Exit }
            .forEach {
                println(
                    when (it) {
                        is Command.Set -> set(it.index, it.value)
                        is Command.Get -> get(it.index)
                        is Command.Delete -> delete(it.index)
                        else -> "ERROR"
                    }
                )
            }
    }

    private fun respond(index: Int, action: (Int) -> Unit): String {
        return if (index in data.indices) {
            action(index)
            "OK"
        } else "ERROR"
    }

    private fun set(index: Int, value: List<String>): String = respond(index) { data[it] = value.joinToString(" ") }

    private fun get(index: Int): String = data.getOrElse(index) { "ERROR" }.takeIf { it != "" } ?: "ERROR"

    private fun delete(index: Int): String = respond(index) { data[it] = "" }

    private fun parseCommand(input: List<String>): Command {
        return when (input[0].lowercase(Locale.getDefault())) {
            "set" -> if (input.size >= 2) Command.Set(input[1].toInt() - 1, input.drop(2)) else Command.Invalid
            "get" -> if (input.size == 2) Command.Get(input[1].toInt() - 1) else Command.Invalid
            "delete" -> if (input.size == 2) Command.Delete(input[1].toInt() - 1) else Command.Invalid
            "exit" -> Command.Exit
            else -> Command.Invalid
        }
    }
}