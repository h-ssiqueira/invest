import http from 'k6/http';
import { check, group } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const RATE_TYPE = ['SELIC', `IPCA`];
const INVESTMENT_TYPE = ['CDB', 'RDB', 'LCA', 'LCI', 'CRA', 'CRI'];
const ALIQUOT_TYPE = ['PREFIXED', 'INFLATION', 'POSTFIXED'];

export const options = {
    scenarios: {
        rates_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 20 },
                { duration: '3m', target: 20 },
                { duration: '1m', target: 0 }
            ],
            exec: 'ratesScenario',
            gracefulRampDown: '30s'
        },
        simulate_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 10 },
                { duration: '3m', target: 10 },
                { duration: '1m', target: 0 }
            ],
            exec: 'simulateScenario',
            gracefulRampDown: '30s'
        }
    }
};

function checkOk(r) {
    check(r, {
        'status is 2xx': (res) => res && res.status >= 200 && res.status < 300,
    });
}

function randomValue(max) {
    return Math.round(Math.random() * max * 100) / 100;
}

function randomInteger(max) {
    return Math.floor(Math.random() * max);
}

function randomDates() {
    const start = new Date('2000-01-01').getTime();
    const end = new Date('2025-12-31').getTime();

    let t1 = start + randomInteger(end - start);
    let t2 = start + randomInteger(end - start);

    let initial = Math.min(t1, t2);
    let final = Math.max(t1, t2);

    const oneDay = 24 * 60 * 60 * 1000;
    if (final - initial < oneDay) {
        final = Math.min(initial + oneDay, end);
        initial = Math.max(initial - oneDay, start);
    }

    const fmt = (ms) => new Date(ms).toISOString().slice(0, 10);
    return { initialDate: fmt(initial), finalDate: fmt(final) };
}

export function ratesScenario() {
    group('Rates GET', function () {
        const dates = randomDates();
        const rate = RATE_TYPE[randomInteger(RATE_TYPE.length)];
        const url = `${BASE_URL}/api/v1/rates/${rate}?initialDate=${dates.initialDate}&finalDate=${dates.finalDate}`;
        const params = { headers: { Accept: 'application/json' } };
        checkOk(http.get(url, params));
    });
}

export function simulateScenario() {
    group('Investment Simulate POST', function () {
        const url = `${BASE_URL}/api/v1/investments/simulate`;
        const dates = randomDates();
        const payload = JSON.stringify({
            type: INVESTMENT_TYPE[randomInteger(INVESTMENT_TYPE.length)],
            aliquot: ALIQUOT_TYPE[randomInteger(ALIQUOT_TYPE.length)],
            rate: randomValue(10),
            initialDate: dates.initialDate,
            finalDate: dates.finalDate,
            amount: randomValue(100000),
        });
        const params = { headers: { 'Content-Type': 'application/json', Accept: 'application/json' } };
        checkOk(http.post(url, payload, params));
    });
}
