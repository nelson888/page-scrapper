package com.tambapps.kotlin.parameters.validator

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

class NotEmptyCollection: IParameterValidator {
    override fun validate(name: String, value: String) {
        if (value.isEmpty()) {
            throw ParameterException("Parameter $name shouldn't be empty")
        }
    }
}