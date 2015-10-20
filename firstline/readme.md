Tanken med detta script är att det ska användas för när meddelanden hamnar på en
DLQ i Webcert.

Detta script ska köras på en miljö av en användare som har tillgång till
aktuella loggfiler. Dessa loggfiler som syftas på är antingen Webcerts
catalina.out eller Webcerts webcert.log (det är samma output).

Scriptet behöver ha filen troublemakers.txt tillgänglig i samma mapp som
scriptet körs från. Denna filen beskriver vilka enheter som är kända källor till
fel. Dessa kan vi inte göra något åt och behöver inte eskalera ärendet för.

Scriptet körs på liknande sätt:
./diagnosera.sh <logfil> troublemakers.txt <eventuellt datum>

(om inget datum anges så används dagens datum)

Potentiell output:

Hittade potentiellt takningsfel för känt problematisk enhet: <enhet>
 - Detta är inte akut utan kan vänta till måndag morgon

Hittade potentiellt takningsfel för enhet: SE2321000115-O48545
 - Detta behöver man undersöka direkt

VAS kunde inte hantera notifieringar som skickats, brukar bero på att de får meddelanden i fel ordning
 - Detta behöver man undersöka vidare. Meddela gärna denna output så har vi mer
   att gå på.

Intyg kunde inte skickas till Försäkringskassan för att de inte svarade
 - Detta behöver man undersöka vidare. Har FK service fönster? Om inte så
   meddela Inera med detta meddelandet.

--> Det finns okända fel som behöver utredas
 - Detta är output när scriptet inte kunde identifiera vad det var som gick fel.
   Det vill säga det är inte ett känt fel.
