package com.tambapps.kotlin.parameters

import com.beust.jcommander.Parameter
import com.beust.jcommander.converters.FileConverter
import com.tambapps.kotlin.parameters.validator.ExistingDirectory
import java.io.File

data class Arguments(
    @Parameter(names = ["-urls"], description = "url(s) to scrap", required = true)
    var urls: List<String>,
   // @Parameter(names = ["--dir", "--directory"], converter = FileConverter, validateValueWith= ExistingDirectory)
    val directory: File
)