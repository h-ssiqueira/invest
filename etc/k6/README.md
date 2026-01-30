# k6 load tests

Two load test scenarios:

- `rates_test` — GET `/api/v1/rates/{rateType}`
- `simulate_test` — POST `/api/v1/investments/simulate`

Environment variables
- `BASE_URL` (default `http://localhost:8080`) — base URL of the app

## Execution

* Run all scenarios defined in the script:

```bash
k6 run etc/k6/loadtest.js
```

* Run only the rates scenario:

```bash
k6 run --scenario rates_test -e BASE_URL=http://localhost:8080 etc/k6/loadtest.js
```

* Run only the simulate scenario:

```bash
k6 run --scenario simulate_test -e BASE_URL=http://localhost:8080 etc/k6/loadtest.js
```
___
Notes
- Scenarios are configured under `options.scenarios` and use `exec` to map to exported functions; there is no default export.
