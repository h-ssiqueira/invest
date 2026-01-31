import http from 'k6/http';
import { Trend } from 'k6/metrics';
import { check, group } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const THROUGHPUT = Number(__ENV.THROUGHPUT) || 125;
const FIXED_VUS = Number(__ENV.FIXED_VUS) || 10;
const DURATION_WARMUP = Number(__ENV.DURATION_WARMUP) || 5;
const DURATION_MAIN = Number(__ENV.DURATION_MAIN) || 10;
const SCENARIO = __ENV.SCENARIO || 'ratesScenario';

// Conatant definitions
const RATE_TYPE = ['SELIC', `IPCA`];
const INVESTMENT_TYPE = ['CDB', 'RDB', 'LCA', 'LCI', 'CRA', 'CRI'];
const ALIQUOT_TYPE = ['PREFIXED', 'INFLATION', 'POSTFIXED'];
const getDeleteHeaders = { headers: { Accept: 'application/json' } };
const postPutPatchHeaders = { headers: { 'Content-Type': 'application/json', Accept: 'application/json' } };

const scenarios = (exec, throughput, vus, warmupDuration, mainDuration) => ({
    warmup: {
        executor: 'constant-arrival-rate',
        rate: throughput,
        timeUnit: '1s',
        preAllocatedVUs: vus,
        maxVUs: vus,
        duration: `${warmupDuration}m`,
        gracefulStop: '10s',
        tags: { phase: 'warmup' },
        exec,
        env: {},
    },
    main: {
        executor: 'constant-arrival-rate',
        rate: throughput,
        timeUnit: '1s',
        preAllocatedVUs: vus,
        maxVUs: vus,
        startTime: `${warmupDuration}m`,
        duration: `${mainDuration}m`,
        gracefulStop: '10s',
        tags: { phase: 'main' },
        exec,
        env: {},
    }
});

export const options = {
    scenarios: { ...scenarios(SCENARIO, THROUGHPUT, FIXED_VUS, DURATION_WARMUP, DURATION_MAIN) },
    discardResponseBodies: true,
    thresholds: {},
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)', 'count']
};

let customMetrics = {};

for (let key in options.scenarios) {
    options.scenarios[key].env['MY_SCENARIO'] = key;
    customMetrics[key] = new Trend(key, true);
    options.thresholds[`http_reqs{scenario:${key}, expected_response:true}`] = [`count>=0`];
}


// Scenario functions
export function ratesScenario() {
    group('Rates GET', _ => {
        const dates = randomDates();
        const rate = RATE_TYPE[randomInteger(RATE_TYPE.length)];
        const url = `${BASE_URL}/api/v1/rates/${rate}?initialDate=${dates.initialDate}&finalDate=${dates.finalDate}`;
        checkResponseAndWriteMetrics(http.get(url, getDeleteHeaders));
    });
}

export function simulateScenario() {
    group('Investment Simulate POST', _ => {
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
        checkResponseAndWriteMetrics(http.post(url, payload, postPutPatchHeaders));
    });
}

export function teardown() {
    console.log('✅ Test finished. Cleaning up resources...');
}

function checkResponseAndWriteMetrics(res) {
    var output = check(res, {
        'status is 2xx': (resp) => resp && resp.status >= 200 && resp.status < 300,
    });
    if(!output) {
        console.error(`❌ Request failed with status ${res.status}: ${res.body}`);
    }
    customMetrics[__ENV.MY_SCENARIO].add(res.timings.duration);
}

// Random data generators
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