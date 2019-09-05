package com.tambapps.kotlin.parameters.converter

import com.beust.jcommander.IStringConverter

class ScrapingTypeConverter : IStringConverter<Set<Any>> {
    override
    fun convert(value: String): Set<Any> {
        return value.split(",").toSet();

    }
}