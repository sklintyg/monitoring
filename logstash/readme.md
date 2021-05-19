Setup
=====

To run setup.sh specify which files (or "all" for all three) that should be modified with the path to the
directory containing the custom patterns needed to run these filters.

Examples
=======

```
./setup.sh all ./patterns
```
or
```
./setup.sh logstash-webcert-log.conf /opt/inera/misc/patterns
```
or wherever the patterns directory is located.

Test
====

1. Installera logstash lokalt. (kan laddas ned från https://www.elastic.co/downloads/logstash). Lägg den på en path utan mellanslag, annars kan man få problem med Ruby.
2. Lägg till logstash/bin till path.
3. Ändra i logstash-<app>.conf genom att bortkommentera "input"-blocket som läser från fil.
4. Ändra i logstash-<app>.conf genom att bortkommentera "output"-blocket som ger output av meddelanden. Om osäker vilket, kör med "Print all log messages"
5. Ändra i logstash-<app>.conf genom att ersätta <path_to_project> med absolut sökväg till monitoringprojektet på din lokala disk.
6. Ändra i logstash-<app>.conf genom att ersätta \$PATTERNS_DIR med absolut sökväg till patterns mappen på din lokala disk.
7. Kör ```logstash -f logstash-<app>.conf```
8. Notera att utskrifterna inte kommer i samma ordning som i input-filen och att scriptet stannar när den processat alla loggar i input-filen
9. Kontollera att loggarna i utskrifterna i konsolen ser korrekta ut
10. Om du önskar köra om rad #7 så kom ihåg att ta bort logstash/data/plugins/inputs/file/.sincedb-filen (Den kommer ihåg att du parsat <app>-monitoring.log och kommer endast parsa om den om du tar bort filen)
11. Backa dessa ändringar innan commiten av den faktiska ändringen.