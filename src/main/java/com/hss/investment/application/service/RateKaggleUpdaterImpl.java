package com.hss.investment.application.service;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import com.hss.investment.application.persistence.ConfigurationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.hss.investment.application.exception.ErrorMessages.INV_005;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static org.apache.commons.csv.CSVFormat.DEFAULT;

@Slf4j
@Service
@RequiredArgsConstructor
public non-sealed class RateKaggleUpdaterImpl implements RateKaggleUpdater {

    private final RestTemplate client = new RestTemplate();
    private final RateService rateService;
    private final ConfigurationDao configurationDao;

    @Value("${investments.kaggle.username}")
    private String username;

    @Value("${investments.kaggle.key}")
    private String key;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void processRates() {
        log.info("Updating rates...");
        var lastUpdatedRates = retrieveLastUpdateTimestamp();

        lastUpdatedRates.ifPresent(timestamp -> {
            if(ZonedDateTime.now().toLocalDate().equals(timestamp.toLocalDate())){
                log.info("Rates already processed today");
                return;
            }
        });
        retrieveAndUpdateRates();
    }

    @Override
    public Optional<ZonedDateTime> retrieveLastUpdateTimestamp() {
        return configurationDao.getLastUpdatedTimestamp();
    }

    @Override
    public void retrieveAndUpdateRates() {
        var request = RequestEntity.get("https://www.kaggle.com/api/v1/datasets/download/hssiqueira/brazil-interest-rate-history-selic")
            .header("Authorization", Base64.getEncoder().encodeToString((username + ":" + key).getBytes(UTF_8)))
            .build();
        try {
            log.info("Downloading dataset...");
            var response = client.exchange(request, byte[].class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to download: " + response);
                return;
            }
            log.info("Download complete!");
            processZipContents(response.getBody());
            log.info("All rates processed successfully!");
            configurationDao.save(ZonedDateTime.now());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new InvestmentException(INV_005.formatted(ex.getMessage()));
        }
    }

    private void processZipContents(byte[] zipBytes) throws IOException {
        try (var zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while (nonNull(entry = zis.getNextEntry())) {
                var filename = entry.getName();
                log.debug("Found file in ZIP: " + entry.getName());
                if (filename.endsWith(".csv")) {
                    processCSV(getCsvBytes(zis), filename);
                }
                zis.closeEntry();
            }
        }
    }

    private static byte[] getCsvBytes(ZipInputStream zis) throws IOException {
        var baos = new ByteArrayOutputStream();
        var buffer = new byte[4096];
        int len;
        while ((len = zis.read(buffer)) > 0)
            baos.write(buffer, 0, len);
        return baos.toByteArray();
    }

    private void processCSV(byte[] csvBytes, String filename) throws IOException {
        try (
            var csvParser = CSVParser.builder()
                .setFormat(DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get())
                .setReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvBytes), UTF_8)))
                .get()
        ) {
            log.debug("CSV Headers: " + csvParser.getHeaderNames());
            if (filename.equals("IBGE_IPCA.csv")) {
                log.info("Processing IPCA rates...");
                rateService.processIpca(new ArrayList<>(csvParser.stream()
                    .map(row -> Ipca.of(
                        YearMonth.parse(row.get(0), DateTimeFormatter.ofPattern("MM/yyyy")).atDay(1),
                        BigDecimal.valueOf(Double.parseDouble(row.get(1))))
                    ).toList()));
            } else if (filename.equals("BACEN_SELIC.csv")) {
                log.info("Processing SELIC rates...");
                rateService.processSelic(new ArrayList<>(csvParser.stream()
                    .map(row -> Selic.of(
                        toLocalDate(row.get(5)),
                        toLocalDate(row.get(6)),
                        BigDecimal.valueOf(Double.parseDouble(row.get(7))))
                    ).toList()));
            } else {
                log.warn("Unknown filename, skipping...");
            }
        }
    }

    private LocalDate toLocalDate(String date) {
        return date.isBlank() ? null : LocalDateTime.ofInstant(Instant.parse(date), ZoneId.systemDefault()).toLocalDate();
    }

}