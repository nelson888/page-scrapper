package com.tambapps.web.page_scrapping.saver

import groovy.util.slurpersupport.NodeChild

interface Saver {

    void printResult()

    void processElement(NodeChild element)

}