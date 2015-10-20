Diagnosera.sh
=============

Tanken med detta script är att det ska användas när det går larm och meddelanden
hamnar på en DLQ i Webcert.

Detta script ska köras på en produktionsmiljö av en användare som har tillgång
till aktuella loggfiler. Dessa loggfiler som syftas på är antingen Webcerts
catalina.out eller Webcerts webcert.log (felinformationen finns i båda
loggfilerna).

Scriptet behöver ha filen troublemakers.txt tillgänglig i samma mapp som
scriptet körs från. Denna filen beskriver vilka enheter som är kända källor till
fel. Dessa kan vi inte göra något åt och behöver inte eskalera ärendet för.

Scriptet körs på liknande sätt:
```
./diagnosera.sh <logfil> troublemakers.txt <eventuellt datum>
```
(om inget datum anges så används dagens datum)

Potentiell output:
==================

```
Hittade potentiellt takningsfel för känt problematisk enhet: <enhet>
```
 - Detta är inte akut utan kan vänta till nästa vardagsmorgon. Underrätta Inera
   beredskap om att detta ska göras på måndag och uppge ovanstående text.

```
Hittade potentiellt takningsfel för enhet: SE2321000115-O48545
```
 - Detta är inte akut utan kan vänta till nästa vardagsmorgon. Underrätta Inera
   beredskap om att detta ska göras på måndag och uppge ovanstående text.

```
Problem med kommunikationen till VAS. Detta brukar bero på att de får meddelanden i fel ordning efter ett tidigare stopp.
```
 - Detta behöver man undersöka vidare. Kontakta Inera beredskap och uppge
   ovanstående text.

```
Intyg kunde inte skickas till Försäkringskassan för att de inte svarar på anrop
```
 - Detta behöver man undersöka vidare. Har FK service fönster? Om inte så
   Kontakta Inera beredskap med detta meddelandet.

```
--> Det finns okända fel som behöver utredas. Dessa är:
 * <Felmeddelande 1>
 * <Felmeddelande 2>
 * ...
 * <Felmeddelande N>
```

 - Detta är output när scriptet inte kunde identifiera vad det var som gick fel.
   Detta är inte ett känt fel. Kontakta Inera beredskap och uppge ovanstående
   text.
