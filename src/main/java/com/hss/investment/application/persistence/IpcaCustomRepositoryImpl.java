package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Ipca;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class IpcaCustomRepositoryImpl implements IpcaCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void insertAllMissingRates(List<Ipca> ipcaList) {
        if (isEmpty(ipcaList)) return;
        var sql = new StringBuilder("INSERT INTO ipca (reference_date, rate) VALUES ");
        var params = new ArrayList<>(ipcaList.size() << 1);
        for (var ipca : ipcaList) {
            sql.append("(?, ?),");
            params.add(Date.valueOf(ipca.referenceDate()));
            params.add(ipca.rate().ratePercentage());
        }
        sql.setLength(sql.length() - 1);
        jdbcTemplate.update(sql.toString(), params.toArray());
    }
}
