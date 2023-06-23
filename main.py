from fastapi import Request, FastAPI
import json
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel
import sqlalchemy
from hashlib import sha256
from datetime import datetime

# requests.post('http://127.0.0.1:8000/register', json = {'login': '123', 'password': '123'}).json()
# requests.post('http://127.0.0.1:8000/login', json = {'login': '123', 'password': '123'}).json()
# requests.post('http://127.0.0.1:8000/next_flights', json = {'type_': 0/1}).json()
# requests.post('http://127.0.0.1:8000/search', json = {'type_': 0/1, 'city': 'Стокгольм/Stockholm'}).json()
# requests.post('http://127.0.0.1:8000/get_flight', json = {'id': 28161}).json()
# requests.post('http://127.0.0.1:8000/get_templates', json = {'token': 'Y2M3YTg5YjYxMjkyNmU1MDBjZDA5NTNjYTc3YTJiMDhjNzVlYzEzMThiY2E0NmYx'})
# requests.post('http://127.0.0.1:8000/get_tickets', json = {'token': 'Y2M3YTg5YjYxMjkyNmU1MDBjZDA5NTNjYTc3YTJiMDhjNzVlYzEzMThiY2E0NmYx'})
# requests.post('http://127.0.0.1:8000/delete_template', json = {'token': 'Y2M3YTg5YjYxMjkyNmU1MDBjZDA5NTNjYTc3YTJiMDhjNzVlYzEzMThiY2E0NmYx', id = 1})

'''
requests.post('http://127.0.0.1:8000/buy_ticket', json = {
    'token': 'Y2M3YTg5YjYxMjkyNmU1MDBjZDA5NTNjYTc3YTJiMDhjNzVlYzEzMThiY2E0NmYx',
    'flight': 7344,
    'full_name': 'Юзеров Юзер Юзерович',
    'sex': 0,
    'citizenship': 'США',
    'birth_date': '01.01.2000',
    'pass_no': '12345678',
    'tb_no': '12345678',
    'class_': 2,
    'baggage': 1,
    'pets': 1,
    'windows': 1,
    'insurance': 1
}).json()

requests.post('http://127.0.0.1:8000/buy_ticket', json = {
    'token': 'Y2M3YTg5YjYxMjkyNmU1MDBjZDA5NTNjYTc3YTJiMDhjNzVlYzEzMThiY2E0NmYx',
    'flight': 7043,
    'template': 5
}).json()
'''

# requests.get('<address>').json()

# Инициализация API, цветов и подключение к БД
app = FastAPI()
db = sqlalchemy.create_engine('mysql+mysqlconnector://root:@localhost/airport')

# eval-cтрока для рейсов
eval_ = '''{'id': i[0], 'airport': i[1], 'airport_en': i[2], 'city': i[3],
    'city_en': i[4], 'country': i[5], 'country_en': i[6], 'plane': i[7],
    'airlines': i[8], 'departure': i[9], 'arrive': i[10], 'eco_price': i[11],
    'comf_price': i[12], 'biz_price': i[13], 'baggage_price': i[14],
    'pets_price': i[15], 'windows_price': i[16], 'insurance_price': i[17],
    'outin': i[18], 'cancel': i[19]}'''

# Базовый SQL-запрос для рейсов
sql_flights = '''SELECT flights.id_, airports.name_, airports.en,
cities.name_, cities.en, countries.name_, countries.en,
models.name_, airlines.name_,
DATE_FORMAT(FROM_UNIXTIME(deptime), "%d.%m.%Y %H:%i"),
DATE_FORMAT(FROM_UNIXTIME(arrtime), "%d.%m.%Y %H:%i"),
eco_price, comf_price, biz_price, baggage_price,
pets_price, windows_price, insurance_price, outin, cancel FROM flights
INNER JOIN airports ON airports.id_ = flights.airport_id
INNER JOIN cities ON airports.city = cities.id_
INNER JOIN countries ON cities.country = countries.id_
INNER JOIN planes ON planes.id_ = flights.plane_id
INNER JOIN models ON models.id_ = planes.model
INNER JOIN airlines ON planes.airline = airlines.id_
WHERE deptime > UNIX_TIMESTAMP(NOW()) 
AND outin = %s
AND cities.name_ IN (SELECT name_ FROM cities WHERE en = %s OR name_ = %s)
ORDER BY deptime ASC
LIMIT %s'''
sql_flight_id = sql_flights[:676] + 'WHERE flights.id_ = %s'

# Поиск рейсов
def search_flights(outin, city, limit):
    sql = sql_flights
    if city == '':
        sql = sql.replace(
'AND cities.name_ IN (SELECT name_ FROM cities WHERE en = %s OR name_ = %s)',
        '')
        flights = db.execute(sql, (outin, limit)).fetchall()
    else:
        sql = sql.replace('AND outin = %s', 'AND outin = %s AND cancel = 0')
        flights = db.execute(sql, (outin, city, city, limit)).fetchall()
    flights_ = []
    for i in flights:
        flights_.append(eval(eval_))
    return {'result': flights_}

# Проверка sql-инъекциий
def is_sql(a):
    for keys, values in a.__dict__.items():
        keys = str(keys)
        values = str(values)
        if [keys[0:2], keys[-2:]] != ['__'] * 2:
            for i in ['DELETE', 'FROM', '*', '&', '{', '}', '|', '+']:
                if i in values.upper():
                    return True

# ID по токену
def get_id(token):
    try:
        return db.execute('''SELECT id_ FROM users WHERE token = %s''',
            (token)).fetchall()[0][0]
    except:
        return False

# Хэш
def get_hash(a):
    return sha256(a.encode()).hexdigest()

# Класс авторизации
class User(BaseModel):
    login: str
    password: str

# Класс токена
class Token(BaseModel):
    token: str
    
# Класс города
class Search(BaseModel):
    search: str

# Класс удаления шаблона
class deleteTemp(BaseModel):
    token: str
    id: int

# Класс типа полёта
class flightType(BaseModel):
    type_: int
    
# Класс поиска рейсов
class searchFlights(BaseModel):
    city: str
    type_: int

# Класс покупки билета
class buyTicket(BaseModel):
    token: str
    flight: int
    full_name: str
    sex: str
    citizenship: str
    birth_date: str
    pass_no: str
    class_: int
    baggage: int
    pets: int
    windows: int
    insurance: int
    
# Класс ID рейса
class flightId(BaseModel):
    id: int
    
# Класс удаления билета
class deleteTicket(BaseModel):
    token: str
    ticket: int
    
# Класс редактирования пароля
class changePassword(BaseModel):
    token: str
    old: str
    new: str

@app.get("/status")
async def status():
    return {'result': 'ok'}

'''if (any(map(str.isdigit, user.password)) and user.password not in
 [user.password.upper(), user.password.lower()] and
    4 <= len(user.password) <= 16):'''
'''else:
    return {'result': 'fail', 'reason': 'should_meet_requirements'}'''

@app.post("/register")
async def reg(user: User):
    if is_sql(user) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    
    try:

        db.execute('''INSERT INTO users (login, pass, isadm, token) VALUES
                      (%s, %s, 0, NEW_TOKEN())''', (user.login,
                      get_hash(user.password)))
        token = db.execute('''SELECT token FROM users
                              WHERE login = %s AND pass = %s''',
            (user.login, get_hash(user.password))).fetchall()[0][0]
        return {'result': token}

    except:
        return {'result': 'fail', 'reason': 'already_exists'}
    
@app.post('/login')
async def login(user: User):
    if is_sql(user) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    try:
        token = db.execute('''SELECT token FROM users
                              WHERE login = %s AND pass = %s''',
            (user.login, get_hash(user.password))).fetchall()[0][0]
        return {'result': token}
    except:
        return {'result': 'fail', 'reason': 'incorrect_data'}
        
@app.post('/next_flights')
async def next_flights(t: flightType):
    if is_sql(t) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    if t.type_ not in [0, 1]:
        return {'result': 'fail', 'reason': 'invalid_number'}
    return search_flights(t.type_, '', 10)
    
@app.post('/search')
async def search(sf: searchFlights):
    if is_sql(sf) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    if sf.type_ not in [0, 1]:
        return {'result': 'fail', 'reason': 'invalid_number'}
    return search_flights(sf.type_, sf.city, 500)
    
@app.get('/cities')
async def get_cities():
    return {"result": [f"{i[0]}, {i[1]}" for i in
    db.execute(f'''SELECT cities.name_, countries.name_ FROM cities
                   INNER JOIN countries ON countries.id_ = cities.country
                   ORDER BY cities.name_ ASC''').fetchall()]}

'''
@app.post('/add_template')
async def add_template(temp: Template):
    if is_sql(temp) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    try:
        opt = [temp.class_, temp.baggage, temp.pets, temp.windows,
            temp.insurance];
        if not 0 <= temp.class_ <= 2:
            return {'result': 'fail', 'reason': 'invalid_number'}
        if False in [0 <= i <= 1 for i in opt[1:]]:
            return {'result': 'fail', 'reason': 'invalid_number'}
        opt = str(opt)[1:-1].replace(', ', '')
        db.execute("""
        INSERT INTO template (user_id, full_name, sex, citizenship,
        birth_date, pass_no, tb_no, opt)
        VALUES ((SELECT id_ FROM users WHERE token = %s),
        %s, %s, (SELECT id_ FROM countries WHERE name_ = %s),
        %s, %s, %s, %s)""",
        (temp.token, temp.full_name, temp.sex, temp.citizenship,
        temp.birth_date, temp.pass_no, temp.tb_no, opt))
        tid = db.execute("""SELECT * FROM template WHERE user_id IN
                            (SELECT id_ FROM users WHERE token = %s)
                            ORDER BY id_ DESC LIMIT 1""",
                        (temp.token)).fetchall()[0][0]
        return {'result': tid}
    except Exception as e:
        print(e)
        return {'result': 'fail', 'reason': 'no_country_or_no_token'}
     
@app.post('/get_templates')
async def get_templates(token: Token):
    if is_sql(token) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    result = []
    for i in db.execute("""
SELECT template.id_, full_name, sex, countries.name_, birth_date,
pass_no, tb_no, opt FROM template
INNER JOIN countries ON citizenship = countries.id_
WHERE user_id IN (SELECT id_ FROM users WHERE token = %s)
""", (token.token)).fetchall():
        opt = [int(j) for j in list(i[7])]
        result.append({'id': i[0], 'full_name': i[1],
        'sex': i[2], 'citizenship': i[3], 'birth_date': i[4],
        'pass_no': i[5], 'tb_no': i[6], 'class': opt[0], 'baggage': opt[1],
        'pets': opt[2], 'windows': opt[3], 'insurance': opt[4]})
        
    return {'result': result}
@app.post('/delete_template')
async def delete_template(dt: deleteTemp):
    if is_sql(dt) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    try:
        db.execute("""UPDATE template SET status = 1 WHERE user_id IN (SELECT
                      id_ FROM users WHERE token = %s) AND id_ = %s""", 
                      dt.token, dt.id)
        return {'result': 'ok'}
    except Exception as e:
        print(e)
        return {'result': 'fail', 'reason': 'no_token_or_no_id'}
'''        

@app.post('/buy_ticket')
async def buy_ticket(bt: buyTicket):
    if is_sql(bt) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    if (bt.flight, ) not in db.execute('''SELECT flights.id_ FROM flights
                                          WHERE UNIX_TIMESTAMP(NOW()) <=
                                       deptime AND outin = 0''').fetchall():
        return {'result': 'fail', 'reason': 'invalid_flight'}
    if (datetime.strptime(bt.birth_date, "%d.%m.%Y") >= 
        datetime.today().replace(hour = 0, second = 0, microsecond = 0)):
        return {'result': 'fail', 'reason': 'invalid_birth'}
    """
    price = 0
    prices = db.execute('''SELECT eco_price, comf_price, biz_price,
                           baggage_price, pets_price, windows_price,
                           insurance_price FROM flights WHERE id_ = %s''',
                           (bt.flight)).fetchall()[0]
    price += prices[int(bt.opt[0])]
    prices = prices[3:]
    for i in range(4):
        price += int(opt[i + 1]) * prices[i]
    """
    while "  " in bt.full_name: bt.full_name = bt.full_name.replace("  ", " ")
    while bt.full_name[-1] == " ": bt.full_name = bt.full_name[:-1]
    opt = "".join([str(i) for i in [bt.class_, bt.baggage, bt.pets,
        bt.windows, bt.insurance]])
    db.execute('''INSERT INTO tickets
                  VALUES (0, (SELECT id_ FROM users WHERE token = %s),
                  %s, %s, (SELECT id_ FROM genders WHERE name_ = %s),
                  (SELECT id_ FROM countries WHERE name_ = %s),
                  %s, %s, %s)''',
                  (bt.token, bt.flight, bt.full_name, bt.sex,
                  bt.citizenship, bt.birth_date, bt.pass_no, opt))
    return {'result': 'ok'}

@app.post('/get_tickets')
async def get_tickets(gt: Token):
    if is_sql(gt) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    tickets = []
    for i in db.execute(
'''SELECT tickets.id_, full_name, (SELECT name_ FROM genders WHERE id_ = sex),
(SELECT name_ FROM countries WHERE id_ = citizenship), birth_date, pass_no,
opt, airports.name_, airports.en, cities.name_, cities.en,
countries.name_, countries.en, models.name_, airlines.name_,
DATE_FORMAT(FROM_UNIXTIME(deptime), "%d.%m.%Y %H:%i"),
DATE_FORMAT(FROM_UNIXTIME(arrtime), "%d.%m.%Y %H:%i"),
eco_price, comf_price, biz_price, baggage_price,
pets_price, windows_price, insurance_price, cancel, flights.id_ FROM flights
INNER JOIN tickets ON flights.id_ = tickets.flight
INNER JOIN airports ON airports.id_ = flights.airport_id
INNER JOIN cities ON airports.city = cities.id_
INNER JOIN countries ON cities.country = countries.id_
INNER JOIN planes ON planes.id_ = flights.plane_id
INNER JOIN models ON models.id_ = planes.model
INNER JOIN airlines ON planes.airline = airlines.id_
WHERE user_id IN (SELECT id_ FROM users WHERE token = %s)
ORDER BY tickets.id_ DESC''',
        (gt.token)).fetchall():
        tickets.append({'ticket_id': i[0], 'flight_id': i[-1],
        'full_name': i[1], 'sex': i[2], 'citizenship': i[3],
        'birth_date': i[4], 'pass_no': i[5],
        'opt': i[6], 'airport': i[7], 'airport_en': i[8],
        'city': i[9], 'city_en': i[10], 'country': i[11],
        'country_en': i[12], 'plane': i[13], 'airlines': i[14],
        'departure': i[15], 'arrive': i[16], 'eco_price': i[17],
        'comf_price': i[18], 'biz_price': i[19], 'baggage_price': i[20],
        'pets_price': i[21], 'windows_price': i[22], 'insurance_price': i[23],
        'cancel': i[24]})
    return {'result': tickets}
    
@app.post('/get_flight')
async def get_flight(fid: flightId):
    if is_sql(fid) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    try:
        i = db.execute(sql_flight_id, (fid.id)).fetchall()[0]
        return {'result': eval(eval_)}
    except Exception as e:
        print(e)
        return {'result': 'fail', 'reason': 'no_flight'}
    
# НИЖЕ БЫСТРО СДЕЛАННЫЕ КОСТЫЛИ
    
@app.post('/delete_ticket')
async def delete_ticket(dt: deleteTicket):
    if is_sql(dt) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    
    db.execute("""DELETE FROM tickets WHERE id_ = %s
        AND user_id IN (SELECT id_ FROM users WHERE token = %s)""",
        (dt.ticket, dt.token))
    
    return {"result": "ok"}

@app.post('/change_password')
async def change_password(cp: changePassword):
    if is_sql(cp) == True:
        return {'result': 'fail', 'reason': 'invalid_characters'}
    
    if db.execute("SELECT pass FROM users WHERE token = %s",
        (cp.token)).fetchall()[0][0] == get_hash(cp.old):
            db.execute("""UPDATE users SET pass = %s WHERE
                token = %s""", (get_hash(cp.new), cp.token))
    
            return {"result": "ok"}
    
    else:
        return {"result": "fail", "reason": "incorrect_data"}