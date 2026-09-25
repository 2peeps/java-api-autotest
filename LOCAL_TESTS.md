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

## PaymentStatementRateLimitTest

Файл:
`src/test/java/qa/dmitriy/api/PaymentStatementRateLimitTest.java`

Назначение:
Проверяет ограничение количества запросов для ECOM API по настройкам rate limit.

Что проверяет:
- `/api/payment-statements/{statementId}/status` — 30 запросов проходят, 31-й получает `429`;
- `/api/payment-statements/{statementId}` — 30 запросов проходят, 31-й получает `429`;
- `/api/payment-statements/{statementId}/items/statuses` — 30 запросов проходят, 31-й получает `429`.

Особенности:
- лимит для проверяемых endpoints — 30 запросов;
- окно ограничения — 60 секунд;
- тесты используют отдельную авторизацию ECOM;
- перед каждым тестом выполняется ожидание окончания предыдущего окна rate limit;
- тесты намеренно создают нагрузку на rate limit и поэтому не входят в обычный `mvn clean test`.

Когда запускать:
Только при необходимости отдельно проверить настройки rate limit для ECOM API.

Запуск:

```powershell
mvn "-Dtest=PaymentStatementRateLimitTest" test
```
Другие локальные интеграционные тесты добавляются в этот файл по мере появления, с указанием назначения, условий запуска и команды.
