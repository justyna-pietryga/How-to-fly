package com.justyna.project.model.other.reservationHistory;

import com.justyna.project.model.relational.Pnr;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReservationHistory {
    private Pnr pnr;
    private List<PnrDetailDto> pnrDetailDtoList;
}
