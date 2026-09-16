# Локальные тесты

Эти тесты предназначены для локального запуска и не входят в стандартный набор `mvn test`.

## WalletCardTransferSchedulerTest

Файл:
`src/test/java/qa/dmitriy/api/WalletCardTransferSchedulerTest.java`

Назначение:
Проверяет работу реального `WalletCardTransferStatusScheduler` на DEV через прямое подключение к PostgreSQL.

Что проверяет:
- создаётся тестовая запись со статусом `PENDING`;
- запись создаётся с истёкшим сроком ожидания;
- реальный scheduler самостоятельно находит запись;
- статус меняется `PENDING -> ERROR`;
- заполняется `processed_at`;
- в `error_message` фиксируется причина и источник изменения (`WalletCardTransferStatusScheduler`);
- тест удаляет созданную тестовую запись после выполнения.

Когда запускать:
Только при необходимости проверить реальную работу scheduler или повторить сценарий восстановления/истечения окна ожидания на DEV.

Условия:
- PostgreSQL должен быть доступен локально;
- переменные окружения должны быть заданы:
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- текущий локальный DEV URL БД: `jdbc:postgresql://127.0.0.1:15432/samozanyatiy_dev`

Запуск:
```powershell
mvn "-Dtest=WalletCardTransferSchedulerTest" test
```

## Важно

`WalletCardTransferSchedulerTest` исключён из обычного `mvn clean test` и игнорируется Git.

Обычный набор:
```powershell
mvn clean test
```

Локальный scheduler-тест:
```powershell
mvn "-Dtest=WalletCardTransferSchedulerTest" test
```

Другие локальные интеграционные тесты добавляются в этот файл по мере появления, с указанием назначения, условий запуска и команды.
