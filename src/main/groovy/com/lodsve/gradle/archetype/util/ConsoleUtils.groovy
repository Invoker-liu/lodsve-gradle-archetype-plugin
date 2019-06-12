package com.lodsve.gradle.archetype.util

class ConsoleUtils {

    static final String PS1 = "> "

    static String prompt(message, defaultValue = null) {

        def msg = "$PS1$message: " + (defaultValue ? "[$defaultValue]" : "")

        def console = System.console()
        if (console) {
            return console.readLine(msg) ?: String.valueOf(defaultValue)
        } else {
            Scanner scanner = new Scanner(System.in)
            println "$msg"
            return scanner.nextLine() ?: String.valueOf(defaultValue)
        }
    }
}
