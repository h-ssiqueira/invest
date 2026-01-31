package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Ipca;
import java.util.List;

public interface IpcaCustomRepository {
    void insertAllMissingRates(List<Ipca> ipcaList);
}
