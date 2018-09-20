package com.tambapps.web.page_scrapping.saver

import groovy.util.slurpersupport.NodeChild

interface Saver {

    static final String ANY = ''

    void processElement(NodeChild element)
    void printResult()
    void finish()

}