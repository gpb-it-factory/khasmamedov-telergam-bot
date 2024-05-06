![img.png](images/img.png)

<h1 align="center"> khasmamedov-telegram-bot </h1>

<h1 align="center"> Банковское приложение с телеграмм-ботом и начинкой на java, c внешним хранилищем данных</h1>

![Static Badge](https://img.shields.io/badge/Java%20ver.=17-green)
![Static Badge](https://img.shields.io/badge/Spring-blue)
![Static Badge](https://img.shields.io/badge/Spring%20Boot-darkgreen)
![Static Badge](https://img.shields.io/badge/%D0%91%D0%94%3A%20Postgres-purple)
![Static Badge](https://img.shields.io/badge/Tests:Junit%20%2B%20Mockito-red)
![Static Badge](https://img.shields.io/badge/Gradle-magenta)
![Static Badge](https://img.shields.io/badge/Git-green)

### Базовое верхнеуровневое представление:  
![Overall.png](images/Overall.png)
<br/><br/>

<p align="left">
  <h3> Про проект в-общем: </h3>
  1. Бот является лишь пользовательским интерфейсом, что взаимодействует с пользователем (фронт) </i>
  <br/><br/>
  2. Сервис, или прослойка (движок), написанная на java, выполняет бизнес-логику и выступает прокладкой между фронтом и БД
  <br/><br/>
  3. Второй слой, или БД (аналог ДАО) - служит для хранения и обработки данных
  <br/><br/>
  <b><a href="https://gpb.fut.ru/itfactory/backend?utm_source=gpb&utm_medium=expert&utm_campaign=recommend&utm_content=all">GBP IT-factory</a></b> | <b><a href="">Документация(_пока пуста_)</a></b> | <b><a href="">Демо(_пока пуста_)</a></b> | <b><a href="https://github.com/gpb-it-factory/khasmamedov-telergam-bot">GitHub</a></b> | <b><a href="#Как_запустить_и_начать_работу">Запуск и начало работы</a></b>
  <br/><br/>
  <a href="https://github.com/gpb-it-factory/khasmamedov-telergam-bot/CHANGELOG.md"><img src="https://img.shields.io/github/package-json/v/gpb-it-factory/khasmamedov-telergam-bot?logo=hackthebox&color=609966&logoColor=fff" alt="Current Version"/></a>
  <a target="_blank" href="https://github.com/gpb-it-factory/khasmamedov-telergam-bot"><img src="https://img.shields.io/github/last-commit/gpb-it-factory/khasmamedov-telergam-bot?logo=github&color=609966&logoColor=fff" alt="Last commit"/></a>
  <br/><br/>

### Картинка выше в качестве UML-диаграммы:
```plantuml
@startUML
actor Клиент
participant Telegram
participant Service
participant Backend

activate Клиент
Клиент -> Telegram: Запрос на действие со счетом:\n проверить баланс,\n пополнить,\n снять деньги,\n перевести
deactivate Клиент
activate Telegram
Telegram -> Service: Валидация запроса
deactivate Telegram
activate Service
alt Вернуть клиенту запрос на доработку 
    Service -> Telegram: Клиентский запрос содержит ошибки
    deactivate Service
    activate Telegram
    Telegram -> Клиент: Клиент перепроверяет запрос;\n завершает работу, либо шлет запрос заново
    deactivate Telegram
    activate Клиент
    deactivate Клиент
else Запрос уже прошел цепочку клиент-телеграм-сервис + в сервисе успешно прошел проверку
    activate Service
    Service -> Service: Бизнес-логика
    Service -> Backend: Обработка данных
    deactivate Service
    activate Backend 
    Backend -> Service: Возврат успешности операции и\или \n данных, если они были запрошены
    deactivate Backend 
    activate Service
    Service -> Telegram: Дополнительная обработка данных (если требуется)
    deactivate Service
    activate Telegram
end    
Telegram -> Клиент: возврат данных
deactivate Telegram
deactivate Клиент
@endUML
```

### Как_запустить_и_начать_работу
((пока пусто))

