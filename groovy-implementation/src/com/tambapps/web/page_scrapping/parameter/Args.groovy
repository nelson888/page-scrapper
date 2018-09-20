package com.tambapps.web.page_scrapping.parameter

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import com.beust.jcommander.converters.FileConverter
import com.tambapps.web.page_scrapping.parameter.converter.ScrapingTypeConverter

import com.tambapps.web.page_scrapping.parameter.validator.ExistingDirectory
import com.tambapps.web.page_scrapping.parameter.validator.NaturalInteger
import com.tambapps.web.page_scrapping.parameter.validator.NotEmptyCollection

@Parameters(separators = '=')
class Args {

    static final String COLLECTION_SEPARATOR = ','

    @Parameter(names = '-urls', description = 'url(s) to scrap',
            validateWith = NotEmptyCollection, required = true)
    List<String> urls

    @Parameter(names = ['-dir', '--directory'], converter = FileConverter,
            validateValueWith = ExistingDirectory,
            description = 'The directory in which the images/links will be saved', required = true)
    File directory

    @Parameter(names = '-type', validateWith = NotEmptyCollection,
            converter = ScrapingTypeConverter,
            description = 'what to save', required = true)
    Set<ScrapingType> types

    @Parameter(names = ['-v', '--verbose'], description = 'get more output message')
    boolean verbose = false

    @Parameter(names = ['-t', '--threads'], validateValueWith = NaturalInteger,
            description = 'How many threads should be used to process image saving tasks')
    int nbThreads = Runtime.getRuntime().availableProcessors() + 1

}
