package com.tambapps.web.page_scrapping.parameter.validator

import com.beust.jcommander.IValueValidator
import com.beust.jcommander.ParameterException

class NaturalInteger implements IValueValidator<Integer> {

    @Override
    void validate(String name, Integer n) throws ParameterException {
        if (n <= 0) {
            throw new ParameterException("Parameter $name should be natural (found $n)")
        }
    }
}
