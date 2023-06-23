import sqlalchemy
from random import randint
from datetime import datetime
from time import mktime

# planes: 12057
# airports: 3503

db = sqlalchemy.create_engine('mysql+mysqlconnector://root:@localhost/airport')
m = mktime(datetime.now().timetuple()) # (интервал)
c = 600

sql = "INSERT INTO flights (plane_id, airport_id, deptime, arrtime, eco_price, comf_price, biz_price, baggage_price, pets_price, windows_price, insurance_price, outin) VALUES "

for i in range(9000):
    plane = randint(1, 12057)
    airport = randint(1, 3221)
    deptime = c * i + m
    arrtime = deptime + randint(1, 9) * 3600
    sql += f"({plane}, {airport}, {deptime}, {arrtime}, {randint(1, 300)}, {randint(300, 1000)}, {randint(1000, 4000)}, {randint(1, 50)}, {randint(10, 250)}, {randint(10, 50)}, {randint(1, 10)}, {randint(0, 1)}), "
    #db.execute('INSERT INTO flights (plane_id, airport_id, deptime, arrtime, eco_price, comf_price, biz_price, baggage_price, pets_price, windows_price, insurance_price, outin) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)',
    #(plane, airport, deptime, arrtime, randint(1, 300), randint(300, 1000), randint(1000, 4000), randint(1, 50), randint(10, 250), randint(10, 50), randint(1, 10), randint(0, 1)))

db.execute(sql[:-2])