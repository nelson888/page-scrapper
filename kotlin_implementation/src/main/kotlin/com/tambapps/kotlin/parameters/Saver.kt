package com.tambapps.kotlin.parameters

import org.jsoup.nodes.Element
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface Saver {

    fun printResult()

    fun process(executor: ExecutorService, element: Element): Optional<Future<Any>>
}