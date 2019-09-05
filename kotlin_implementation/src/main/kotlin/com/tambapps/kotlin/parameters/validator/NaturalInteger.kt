package com.tambapps.kotlin.parameters.validator

import com.beust.jcommander.IValueValidator
import com.beust.jcommander.ParameterException

class NaturalInteger: IValueValidator<Int> {
    override fun validate(name: String, n: Int) {
        if (n <= 0) {
            throw ParameterException("Parameter $name should be natural (found $n)")
        }
    }
}