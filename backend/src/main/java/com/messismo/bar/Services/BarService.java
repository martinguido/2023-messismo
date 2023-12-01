package com.messismo.bar.Services;

import com.messismo.bar.DTOs.ModifyBarCapacityRequestDTO;
import com.messismo.bar.DTOs.NewBarRequestDTO;
import com.messismo.bar.Entities.Bar;
import com.messismo.bar.Entities.Shift;
import com.messismo.bar.Exceptions.BarNotFoundException;
import com.messismo.bar.Repositories.BarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarService {

    private final BarRepository barRepository;

    public String addBarConfiguration(NewBarRequestDTO newBarRequestDTO) throws Exception {
        try {
            Bar bar = new Bar(newBarRequestDTO.getCapacity());
            barRepository.save(bar);
        } catch (Exception e) {
            throw new Exception("CANNOT add bar configuration");
        }
        return null;
    }

    public String modifyBarCapacity(ModifyBarCapacityRequestDTO modifyBarCapacityRequestDTO) throws Exception {
        try {
            Bar bar = barRepository.findById(modifyBarCapacityRequestDTO.getBarId()).orElseThrow(() -> new BarNotFoundException("Provided bar id DOES NOT match any bar id"));
//            List<Shift> allShifts = shiftService.getAllShifts();
//            for (Shift shift : allShifts) {
//
//            }
            //VALIDAR RESERVAS
            bar.updateCapacity(modifyBarCapacityRequestDTO.getNewCapacity());
            barRepository.save(bar);
            return "Bar capacity updated successfully";
        } catch (BarNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("CANNOT modify bar capacity at the moment");
        }
    }

    public Bar getBarConfiguration() {
        return barRepository.findAll().get(0);
    }
}
