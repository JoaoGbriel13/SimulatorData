package com.jg.SimulatorData.Repository;

import com.jg.SimulatorData.DTO.FuelAverageDTO;
import com.jg.SimulatorData.Model.SimulatorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface SimulatorDataRepository extends JpaRepository<SimulatorData, Long> {
    @Query(nativeQuery = true, value = "SELECT " +
            "AVG(CASE WHEN d.track_state_enum LIKE '%' || 'Wet' || '%' THEN d.fuel_used ELSE NULL END) AS fuelRain, " +
            "AVG(CASE WHEN d.track_state_enum LIKE '%' || 'Dry' || '%' THEN d.fuel_used ELSE NULL END) AS fuelDry " +
            "FROM tb_data d " +
            "WHERE d.fuel_used > 0 " +
            "AND d.car LIKE '%' || :car || '%' " +
            "AND d.track LIKE '%' || :track || '%' " +
            "AND d.lap_time_numeric > 0 " +
            "AND d.driver = :driver")
    Optional<FuelAverageDTO> getFuelAverage(@Param("car") String car, @Param("track") String track, @Param("driver") String driver);

    @Query(nativeQuery = true, value = "SELECT AVG(d.lap_time_numeric) FROM tb_data d " +
            "WHERE d.lap_time_numeric > 0 " +
            "AND d.track LIKE '%' || :track || '%' " +
            "AND d.car LIKE '%' || :car || '%' " +
            "AND d.track_state_enum LIKE '%' || :state || '%' " +
            "AND d.driver = :driver ")
    BigDecimal getLapTimeAVG(@Param("car")String car, @Param("track") String track, @Param("state") String state,@Param("driver") String driver);


}
