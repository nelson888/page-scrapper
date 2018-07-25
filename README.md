# Profile counter

This is a project for calculating the number of unique users for a day/timeframe.

## Compilation

You can compile the jar using 
```
mvn install
```
or by running the script compile.sh with the following arguments
- rootDirOfClassFiles is com/ directory that contains all the .class files
- libsDir is the path to all the dependencies (libs/ folder)
- jarName is the name wanted for the jar file that will be generated
All paths must be related to the current directory

## Usage

You have to give the following required options:
* -paths: the paths where are stored the profiles (separated by ';')
* -from: the date (yyyy/MM/dd) from which you want to compute the number of unique ids. You can also enter 'yesterday' instead of a date, or a number n (it will be interpreted as today minus n days).
* -fileFormat: the format in which the files are (publicis, sharethis, wcm or profile_exporter)


You can also launch the computation for a timeframe. For that, enter the starting date (included) with '-from' and the ending date with the option '-to' (included).

The default algorithm used to compute the number of ids is implemented with a set, we store ids in this set and then we get the size of it. But for large data, it will consume 
a lot of memory. To prevent that, you can use an approximate algorithm called HyperLogLog by adding '-alg=hll' in the program arguments.

By default, the files will be searched locally but you can also use it on s3 bucket by using '-src=s3'. If you do so, you will have to provide the '-accessKey' 
'-secretKey' and '-bucket' options. You can change the default region (eu-west-1) with the -region option.

Use the '-v' or '--verbose' option to get detailed logs of the operations executed by the program

If you want to get the number of unique users within all paths given (instead of each path separately), you can add the '-accumulateUsers' option.

The '--help' option will give you details on how to run this program.

Examples
```
java -jar profile-counter.jar -paths=./pb/us/;./pb/eu/ -from=yesterday -fileFormat=publicis -accumulateUsers
```

```
java -jar profile-counter.jar --verbose -src=s3 -alg=hll -accessKey=ACCESS_KEY -secretKey=SECRET_KEY -bucket=bucketName -to=yesterday -from=30 -paths=profiles/
```

```
java -jar profile-counter.jar --help
```