# sqlcsv

Export query sql to csv format 

## Populate Argument

| parameter | meaning |
|---|---|
| -j <jdbcur>      | jdbc connection |
| -u <username>    | username |
| -p <password>    | password |
| -q <query>       | sql query |
| -d <delimiter>   | delimiter |
| -quote           | using quote |
| -o <file>        | file output optional default std IO. |


### Sample
```
java -jar sqlcsv.jar -j "jdbc:oracle:thin:@localhost:1521:xe" -u oracle -p oracle -q "select * form dual" -quote

```




