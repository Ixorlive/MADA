# MADA

## Описание

// TODO

## Установка и запуск проекта

Для работы приложения необходим интернет и должны быть запущены следующие сервера: Java-server, ML-server.

### Java-server

Для запуска Java-server на компьютере должна быть предустановлена JVM и открыт доступ для подключения по ip по сети. Далее необходимо скомпилировать .jar с помощью Gradle и запустить.

### ML-server

Описано [здесь](https://github.com/Ixorlive/MADA/tree/main/python-project).

### Установка на android

Для установки на android требуется собрать .apk из проекта, предварительно указав ip Java-server и  ML-server, который находится в папке android-project с помощью Gradle, установить полученное приложение на телефон и предоставить ему доступ к камере и локальному хранилищу.

## Баги

- Переключение на первого пользователя при добавлении второго
- При неправильном вводе каптчи ошибка не отображается, просто закрывается окно
- Не отображается ошибка при отсутствии подключения к серверу
- Не 100% точность распознания показаний
- Не отображается ошибка при неправильном вводе лицевого счета
- Слетает вёрстка
