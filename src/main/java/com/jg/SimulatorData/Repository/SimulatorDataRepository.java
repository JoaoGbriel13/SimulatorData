package com.jg.SimulatorData.Repository;

import com.jg.SimulatorData.DTO.FuelAverageDTO;
import com.jg.SimulatorData.Model.SimulatorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface SimulatorDataRepository extends JpaRepository<SimulatorData, Long> {
    @Query(nativeQuery = true, value = "WITH Quartiles AS ( " +
            "    SELECT " +
            "        PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY d.fuel_used) AS Q1, " +
            "        PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY d.fuel_used) AS Q3 " +
            "    FROM tb_data d " +
            "    WHERE d.fuel_used > 0 " +
            "      AND d.car LIKE '%' || :car || '%' " +
            "      AND d.track LIKE '%' || :track || '%' " +
            "      AND d.lap_time_numeric > 0 " +
            "      AND d.driver = :driver " +
            "), " +
            "FilteredData AS ( " +
            "    SELECT " +
            "        d.fuel_used, " +
            "        d.track_state_enum, " +
            "        Q1, " +
            "        Q3, " +
            "        (Q3 - Q1) AS IQR " +
            "    FROM tb_data d " +
            "    CROSS JOIN Quartiles " +
            "    WHERE d.fuel_used > 0 " +
            "      AND d.car LIKE '%' || :car || '%' " +
            "      AND d.track LIKE '%' || :track || '%' " +
            "      AND d.lap_time_numeric > 0 " +
            "      AND d.driver = :driver " +
            "      AND d.fuel_used BETWEEN (Q1 - 1.5 * (Q3 - Q1)) AND (Q3 + 1.5 * (Q3 - Q1)) " +
            "), " +
            "Averages AS ( " +
            "    SELECT " +
            "        AVG(CASE WHEN fd.track_state_enum LIKE '%' || 'Wet' || '%' THEN fd.fuel_used ELSE NULL END) AS fuelRain, " +
            "        AVG(CASE WHEN fd.track_state_enum LIKE '%' || 'Dry' || '%' THEN fd.fuel_used ELSE NULL END) AS fuelDry " +
            "    FROM FilteredData fd " +
            ") " +
            "SELECT * FROM Averages")
    Optional<FuelAverageDTO> getFuelAverage(
            @Param("car") String car,
            @Param("track") String track,
            @Param("driver") String driver);


    @Query(nativeQuery = true, value = "WITH Quartiles AS ( " +
            "    SELECT " +
            "        PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY d.lap_time_numeric) AS Q1, " +
            "        PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY d.lap_time_numeric) AS Q3 " +
            "    FROM tb_data d " +
            "    WHERE d.lap_time_numeric > 0 " +
            "      AND d.track LIKE '%' || :track || '%' " +
            "      AND d.car LIKE '%' || :car || '%' " +
            "      AND d.track_state_enum LIKE '%' || :state || '%' " +
            "      AND d.driver = :driver " +
            "), " +
            "FilteredData AS ( " +
            "    SELECT " +
            "        d.lap_time_numeric " +
            "    FROM tb_data d " +
            "    CROSS JOIN Quartiles " +
            "    WHERE d.lap_time_numeric > 0 " +
            "      AND d.track LIKE '%' || :track || '%' " +
            "      AND d.car LIKE '%' || :car || '%' " +
            "      AND d.track_state_enum LIKE '%' || :state || '%' " +
            "      AND d.driver = :driver " +
            "      AND d.lap_time_numeric BETWEEN (Q1 - 1.5 * (Q3 - Q1)) AND (Q3 + 1.5 * (Q3 - Q1)) " +
            ") " +
            "SELECT AVG(lap_time_numeric) " +
            "FROM FilteredData")
    BigDecimal getLapTimeAVG(
            @Param("car") String car,
            @Param("track") String track,
            @Param("state") String state,
            @Param("driver") String driver);



}
