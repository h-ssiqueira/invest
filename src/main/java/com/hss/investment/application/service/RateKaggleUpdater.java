package com.hss.investment.application.service;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
public class RateKaggleUpdater {

    private final SelicRepository selicRepository;
    private final IpcaRepository ipcaRepository;
    private static final RestTemplate client = new RestTemplate();

    @Value("${investments.kaggle.username}")
    private String username;

    @Value("${investments.kaggle.key}")
    private String key;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void processRates() {
        log.info("Updating rates...");
        var encodedAuth = Base64.getEncoder().encodeToString((username + ":" + key).getBytes(UTF_8));
        var request = RequestEntity.get("https://www.kaggle.com/api/v1/datasets/download/hssiqueira/brazil-interest-rate-history-selic")
            .header("Authorization", encodedAuth)
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
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new InvestmentException(INV_005.formatted(ex.getMessage()));
        }
    }

    private void processZipContents(byte[] zipBytes) throws IOException {
        try (var zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while (nonNull(entry = zis.getNextEntry())) {
                log.debug("Found file in ZIP: " + entry.getName());
                if (entry.getName().endsWith(".csv")) {
                    var baos = new ByteArrayOutputStream();
                    var buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0)
                        baos.write(buffer, 0, len);
                    var csvBytes = baos.toByteArray();
                    try (
                        var csvParser = CSVParser.builder()
                            .setFormat(DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get())
                            .setReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvBytes), UTF_8))).get()
                    ){
                        log.debug("CSV Headers: " + csvParser.getHeaderNames());
                        if(entry.getName().equals("IBGE_IPCA.csv")) {
                            log.info("Processing IPCA rates...");
                            processIpca(csvParser);
                        } else if (entry.getName().equals("BACEN_SELIC.csv")) {
                            log.info("Processing SELIC rates...");
                            processSelic(csvParser);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private void processIpca(CSVParser csvParser) {
        var lastIPCA = ipcaRepository.findFirstByOrderByReferenceDateDesc();
        var list = new java.util.ArrayList<>(csvParser.stream()
            .map(row -> Ipca.of(
                YearMonth.parse(row.get(0), DateTimeFormatter.ofPattern("MM/yyyy")).atDay(1),
                BigDecimal.valueOf(Double.parseDouble(row.get(1))))
            ).toList());
        if(nonNull(lastIPCA))
            list.removeIf(ipca ->
                ipca.getReferenceDate().isBefore(lastIPCA.getReferenceDate()) ||
                ipca.getReferenceDate().isEqual(lastIPCA.getReferenceDate())
            );
        log.debug("Saved {} new registers into database", list.size());
        ipcaRepository.saveAllAndFlush(list);
    }

    private void processSelic(CSVParser csvParser) {
        var lastSELIC = selicRepository.findFirstByOrderByRangeInitialDateDesc();
        var list = new java.util.ArrayList<>(csvParser.stream()
            .map(row -> Selic.of(
                toLocalDate(row.get(5)),
                toLocalDate(row.get(6)),
                BigDecimal.valueOf(Double.parseDouble(row.get(7))))
            ).toList());
        if(nonNull(lastSELIC)) {
            var second = list.get(1);
            list.removeIf(selic ->
                selic.getRange().getInitialDate().isBefore(lastSELIC.getRange().getInitialDate()) ||
                selic.getRange().getInitialDate().isEqual(lastSELIC.getRange().getInitialDate())
            );
            if(lastSELIC.getRange().getInitialDate().equals(second.getRange().getInitialDate()) && nonNull(second.getRange().getFinalDate())) {
                lastSELIC.getRange().setFinalDate(second.getRange().getFinalDate());
                list.add(lastSELIC);
            }
        }
        log.debug("Saved {} registers into database", list.size());
        selicRepository.saveAllAndFlush(list);
    }

    private LocalDate toLocalDate(String date) {
        return date.isBlank() ? null : LocalDateTime.ofInstant(Instant.parse(date), ZoneId.systemDefault()).toLocalDate();
    }

}