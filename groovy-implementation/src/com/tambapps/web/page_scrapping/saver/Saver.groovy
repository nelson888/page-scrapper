package com.tambapps.web.page_scrapping.saver

interface Saver {

    static final String ANY = ''

    void processElement(def element)

    void printResult()

    void finish()

}