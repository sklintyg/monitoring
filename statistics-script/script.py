# -*- coding: utf-8 -*-
import datetime
import mysql.connector
import gzip
import sys
import tempfile
import scriptproperties as sp
from calendar import monthrange
import smtplib

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

### Testing to send email
def send_email(content):
    me = sp.sender
    recipient = sp.recipient
    msg = MIMEText(content)
    msg['Subject'] = "Nationell statistik intygsprojektet"
    msg['From'] = me
    msg['To'] = recipient
    s = smtplib.SMTP(sp.smtphost)
    s.sendmail(me, recipient, msg.as_string())
    s.quit()


## Find the number of logged in users in Mina Intyg
def logins(title, files, scheme_fun, user_fun, f):
    logins.schemes = {}

    def analyze(line):
        if "LOGIN" in line:
            scheme = scheme_fun(line)
            if scheme not in logins.schemes.keys():
                logins.schemes[scheme] = set()
            logins.schemes[scheme].add(user_fun(line))

    map(analyze, [line for file in files for line in file])

    f.write("\n\n")
    f.write(title)
    total = set()
    for scheme, users in logins.schemes.iteritems():
        f.write("\n{0}, {1}".format(scheme, len(users)))
        total = total.union(users)
    f.write("\ntotal, {0}".format(len(total)))

def qa(files, f):
    qa.sent_questions = 0
    qa.sent_answers = 0
    qa.received_questions = 0
    qa.received_answers = 0
    def analyze(line):
        if "QUESTION_RECEIVED" in line:
            qa.received_questions += 1
        elif "QUESTION_SENT" in line:
            qa.sent_questions += 1
        elif "ANSWER_RECEIVED" in line:
            qa.received_answers += 1
        elif "ANSWER_SENT" in line:
            qa.sent_answers += 1

    map(analyze, [line for file in files for line in file])

    f.write("\n\nFråga och svar statistik")
    f.write("\nfrån vården skickade frågor, {0}".format(qa.sent_questions))
    f.write("\nfrån FK skickade frågor, {0}".format(qa.received_questions))
    f.write("\nfrån vården skickade svar, {0}".format(qa.sent_answers))
    f.write("\nfrån FK skickade svar, {0}".format(qa.received_answers))

### Functions for IO

def get_files(app, days, file_string, delay = 3):
    base = "/mnt/tomcat_logs/ine-pib-app0"
    res = list()
    today = datetime.datetime.today()
    date_list = [today - datetime.timedelta(days=x + delay) for x in range(1, days + 1)]

    for date in date_list:
        res.append(gzip.open(file_string.format(base, app, date)))
        res.append(gzip.open(file_string.format(base, app, date)))
    return res;

def get_files_mon_normal(app, days):
    return get_files(app, days, "{0}1/{1}/archive/{2:%Y}/{2:%m}/{2:%d}/{1}-monitoring.{2:%Y}-{2:%m}-{2:%d}.log.gz")

# def get_files_mon_it(app, days):
    # return get_files(app, days, "{0}1/{1}/archive/{2:%Y}/{2:%m}/{2:%d}/monitoring.{2:%Y}-{2:%m}-{2:%d}.log.gz")

def close_files(files):
    map(lambda s: s.close(), files)


### Print the number of unique logins to webcert and mina intyg
def print_logins(days, f):
    # Functions used to extract relevant information from logs
    scheme_fun = lambda s: s.split("'")[3]
    user_fun = lambda s: s.split("'")[1]

    # Unique logins to Mina intyg
    mi_files = get_files_mon_normal("minaintyg", days)
    logins("Mina intyg", mi_files, scheme_fun, user_fun, f)
    close_files(mi_files)

    # Unique logins to Webcert
    wc_files = get_files_mon_normal("webcert", days)
    logins("Webcert", wc_files, scheme_fun, user_fun, f)
    close_files(wc_files)

### Print statistics of question and answers in webcert
def print_qa(days, f):
    files = get_files_mon_normal("webcert", days)
    qa(files, f)
    close_files(files)

def average_response_time_total(cursor, days, f):
    query = "SELECT COUNT(internReferens), SUM(TIMESTAMPDIFF(HOUR, FRAGE_SKICKAD_DATUM, SVAR_SKICKAD_DATUM))/COUNT(ENHETS_ID) FROM FRAGASVAR WHERE FRAGE_STALLARE = '{0}' AND SVAR_SKICKAD_DATUM > DATE_ADD(NOW(), INTERVAL -{1} DAY) AND SVARS_TEXT IS NOT NULL"

    cursor.execute(query.format('FK', days))
    f.write("\n\nAktör, antal behandlade frågor, medel av svarstid i timmar")
    for intygs_id, response_time in cursor:
        f.write("\nVården, {0}, {1}".format(intygs_id, response_time))

    cursor.execute(query.format('WC', days))
    for intygs_id, response_time in cursor:
        f.write("\nFK, {0}, {1}".format(intygs_id, response_time))

def kompletteringsprocent_units(cursor, days, f):
    query = """SELECT ENHETS_ID, 100*SUM(IF(AMNE='KOMPLETTERING_AV_LAKARINTYG', 1, 0))/COUNT(INTYGS_ID)
            FROM
                (SELECT INTYG.ENHETS_ID, INTYG.INTYGS_ID, AMNE
                FROM INTYG LEFT OUTER JOIN FRAGASVAR
                ON INTYG.INTYGS_ID = FRAGASVAR.INTYGS_ID
                   AND FRAGASVAR.AMNE = 'KOMPLETTERING_AV_LAKARINTYG'
                   AND INTYG.SENAST_SPARAD_DATUM > DATE_ADD(NOW(), INTERVAL -{0} DAY)
                GROUP BY INTYG.INTYGS_ID
                ORDER BY INTYG.INTYGS_ID) AS t
            GROUP BY ENHETS_ID;""".format(days)
    cursor.execute(query)
    f.write("\n\nEnhet, kompletteringsprocent")
    for enhets_id, komplettering in cursor:
        f.write("\n{0}, {1}".format(enhets_id, komplettering))

def kompletteringsprocent_total(cursor, days, f):
    query = """SELECT 100*SUM(IF(AMNE='KOMPLETTERING_AV_LAKARINTYG', 1, 0))/COUNT(INTYGS_ID)
            FROM
                (SELECT INTYG.ENHETS_ID, INTYG.INTYGS_ID, AMNE
                FROM INTYG LEFT OUTER JOIN FRAGASVAR
                ON INTYG.INTYGS_ID = FRAGASVAR.INTYGS_ID
                   AND FRAGASVAR.AMNE = 'KOMPLETTERING_AV_LAKARINTYG'
                   AND INTYG.SENAST_SPARAD_DATUM > DATE_ADD(NOW(), INTERVAL -{0} DAY)
                GROUP BY INTYG.INTYGS_ID ORDER BY INTYG.INTYGS_ID)
            AS t;""".format(days)
    cursor.execute(query)
    f.write("\n\nkompletteringsprocent, {0}%".format(cursor.fetchone()[0]))

def enhet_qa_stat(cursor, days, f):
    query = """SELECT ENHETS_ID,
            SUM(IF(FRAGE_STALLARE = 'FK' AND SVARS_TEXT IS NOT NULL, 1, 0)),
            SUM(IF(FRAGE_STALLARE = 'FK' AND SVARS_TEXT IS NULL, 1, 0)),
            SUM(IF(FRAGE_STALLARE = 'WC' AND FRAGE_SKICKAD_DATUM > DATE_ADD(NOW(), INTERVAL -{0} DAY), 1, 0)),
            TIMESTAMPDIFF(DAY, MAX(SVAR_SKICKAD_DATUM), NOW()),
            SUM(IF(FRAGE_STALLARE='FK' AND SVARS_TEXT IS NOT NULL, TIMESTAMPDIFF(HOUR, FRAGE_SKICKAD_DATUM, SVAR_SKICKAD_DATUM), 0)) / SUM(IF(FRAGE_STALLARE='FK' AND SVARS_TEXT IS NOT NULL, 1, 0))
            FROM FRAGASVAR
            WHERE SENASTE_HANDELSE > DATE_ADD(NOW(), INTERVAL -{0} DAY)
            GROUP BY ENHETS_ID""".format(days)
    cursor.execute(query)
    f.write("\n\nenhet, svar, nya obesvarade, ställda frågor, dagar sedan senaste svar, svarstidsmedel i timmar")
    for enhet, answered, unanswered, asked, time, average in cursor:
        f.write("\n{0}, {1}, {2}, {3}, {4}, {5}".format(enhet, answered, unanswered, asked, time, average))

def intyg_signed_stat(cursor, days, f):
    # query = """SELECT CARE_UNIT_ID, COUNT(CARE_UNIT_ID)
            # FROM CERTIFICATE
            # WHERE SIGNED_DATE > DATE_ADD(NOW(), INTERVAL -{0} DAY)
            # GROUP BY CARE_UNIT_ID""".format(days)

    query = """SELECT CERTIFICATE_TYPE, COUNT(CERTIFICATE_TYPE) FROM
            CERTIFICATE WHERE SIGNED_DATE > DATE_ADD(NOW(), INTERVAL -{0} DAY)
            GROUP BY CERTIFICATE_TYPE;""".format(days)
    cursor.execute(query)
    total = 0
    f.write("\n\nintygstyp, antal")
    for enhet, intyg in cursor:
        f.write("\n{0}, {1}".format(enhet, intyg))
        total += intyg
    f.write("\ntotal, {0}".format(total))

def intyg_sent_stat(cursor, days, f):
    query = """SELECT CERTIFICATE_TYPE, TARGET, COUNT(TARGET)
               FROM CERTIFICATE INNER JOIN CERTIFICATE_STATE
               ON ID = CERTIFICATE_ID
               AND TIMESTAMP > DATE_ADD(NOW(), INTERVAL -{0} DAY)
               AND STATE = 'SENT'
               GROUP BY CERTIFICATE_TYPE, TARGET""".format(days)
    cursor.execute(query)
    f.write("\n\nintygstyp, mottagare, antal")
    for type, target, number in cursor:
        f.write("\n{0}, {1}, {2}".format(type, target, number))

def stat_db_webcert(days, f):
    cnx = mysql.connector.connect(user=sp.dbuser_webcert, password=sp.dbpass_webcert, database=sp.dbname_webcert, host=sp.dbhost_webcert)
    cursor = cnx.cursor()
    average_response_time_total(cursor, days, f)
    # kompletteringsprocent_units(cursor, days, f)
    kompletteringsprocent_total(cursor, days, f)
    # enhet_qa_stat(cursor, days, f)
    cursor.close()
    cnx.close()

def stat_db_intygstjansten(days, f):
    cnx = mysql.connector.connect(user=sp.dbuser_intyg, password=sp.dbpass_intyg, database=sp.dbname_intyg, host=sp.dbhost_intyg)
    cursor = cnx.cursor()
    intyg_signed_stat(cursor, days, f)
    intyg_sent_stat(cursor, days, f)
    cursor.close()
    cnx.close()

def calculate_month_days(today):
    last_month = today.month - 1
    if not last_month:
        last_month = 12
    days_in_month = [31, 28, 31, 30, 31, 30, 31, 31,30, 31, 30, 31]
    days = days_in_month[last_month - 1]

    # Handle leap years
    if last_month == 2 and ((today.year % 4 == 0 and today.year % 100 != 0) or today.year % 400 == 0):
        days += 1
    return days

def main():
    # We use a month as standard and otherwise check if we want monthly or
    # weekly reports
    if (len(sys.argv) > 1):
        conf = sys.argv[1]
        if (conf == 'week'):
            days = 7
        else:
            days = calculate_month_days(datetime.date.today())
    else:
        days = calculate_month_days(datetime.date.today())

    f = tempfile.TemporaryFile()
    print_logins(30, f)
    print_qa(30, f)
    stat_db_webcert(days, f)
    stat_db_intygstjansten(days, f)
    f.seek(0)
    send_email(f.read())
    f.close()

if __name__ == "__main__":
    main()
