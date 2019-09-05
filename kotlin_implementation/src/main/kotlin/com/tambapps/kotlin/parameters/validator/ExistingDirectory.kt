package com.tambapps.kotlin.parameters.validator

import com.beust.jcommander.IValueValidator
import com.beust.jcommander.ParameterException
import java.io.File

class ExistingDirectory: IValueValidator<File> {
    override fun validate(name: String, value: File) {
        if (!value.exists()) {
            throw ParameterException("Directory with path ${value.path} doesn't exists")
        }
        if (!value.isDirectory) {
            throw ParameterException("File with path ${value.path} isn't a directory")
        }
    }
}