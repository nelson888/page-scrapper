# Page scrapper

This is a project for downloading links/images from a web page.
It can save the links in a .txt files and download all pictures in the web page.

## Motivation
The purpose of this project was to compare a Groovy implementation with a Java implementation to see the differences

## Compilation

### Java

You can compile the jar using 
```
mvn install
```
### Groovy

TODO

## Usage

You have to provide a list of urls and these required options:
* -type: images, links or both
* -dir: the directory were the links/the date (yyyy/MM/dd) from which you want to compute the number of unique ids. You can also enter 'yesterday' instead of a date, or a number n (it will be interpreted as today minus n days).

There are also optional options:
* -v: to activate verbose
* -nbThreads: to set a number of threads used to get the images (default is 3).

Examples
```
java -jar page-scraper-1.0-SNAPSHOT-jar-with-dependencies.jar -urls=http://website.com -type=images,links -dir=./downloads/
```

```
java -jar page-scraper-1.0-SNAPSHOT-jar-with-dependencies.jar -urls=http://website1.com,http://website2.com -v -type=links -dir=./downloads/
```

```
java -jar page-scraper-1.0-SNAPSHOT-jar-with-dependencies.jar -urls=http://website.com -type=text -dir=./ --threads=6
```
