![img.png](images/img.png)

<h1 align="center"> khasmamedov-telegram-bot </h1>

<h1 align="center"> Банковское приложение с телеграмм-ботом и начинкой на java, c внешним хранилищем данных</h1>

![Static Badge](https://img.shields.io/badge/Java%20ver.=17-green)
![Static Badge](https://img.shields.io/badge/Spring-blue)
![Static Badge](https://img.shields.io/badge/Spring%20Boot-darkgreen)
![Static Badge](https://img.shields.io/badge/%D0%91%D0%94%3A%20Postgres-purple)
![Static Badge](https://img.shields.io/badge/Tests:Junit%20%2B%20Mockito-red)
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
  <a target="_blank" href="https://github.com/gpb-it-factory/khasmamedov-telergam-bot"><img src="https://img.shields.io/github/last-commit/gpb-it-factory/khasmamedov-telergam-bot?logo=github&color=609966&logoColor=fff" alt="Last commit"/></a>
  <br/><br/>

### Картинка выше в качестве UML-диаграммы:
```plantuml
@startUML
actor Клиент
participant Telegram
participant Service
participant Backend

Клиент -> Telegram: Запрос на действие со счетом:\n проверить баланс,\n пополнить,\n снять деньги,\n перевести
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
@endUML
```

### Как_запустить_и_начать_работу
1. Скачать проект с репозитория выше целиком [перейдя по ссылке на гитхаб](https://github.com/gpb-it-factory/khasmamedov-telergam-bot)  
_Далее, действия проводить в терминале (IDEA)_ 
3. Набрать: 
````gradle build````
3. Запустить проект: 
````java -jar ./build/libs/khasmamedov-telegram-bot-0.0.1-SNAPSHOT.jar>````  
   ((или, например, из командной строки с полным путем до jar-файла:  `C:\Users\Тимур\IdeaProjects\khasmamedov-telergam-bot\build\libs\khasmamedov-telegram-bot-0.0.1-SNAPSHOT.jar`))

todo:
1. посмотреть логгер, раз ты с него начал. 
<fileNamePattern>${LOG_PATH}myapp-%d{yyyy-MM-dd}.log</fileNamePattern> - добавляет расширение в первоначальный файл
1.2 возможно изменить на ямл настройки (см. статью на рефакторинггуру - там вроде +- то же самое)
1.3 посмотреть куда уходят логи если запускаешь как джарник (здесь они сейчас прямо в проекте) - в плане, сейчас это
C:\Users\timk0\Desktop\ideaprojects\IdeaProjects\khasmamedov-telergam-bot
а если поменять ?
2. апдейт ридми по запуску (это А - соло, как джарник, с ключом - см наработки)
3. пинг-понг базовый функционал, без хендлеров
4. хендлеры и апдейт "по уму"
5. докер

А. Потестировать с ключом. Еще раз. 