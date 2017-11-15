package com.valchkou.demo.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.valchkou.demo.model.MeasureType
import com.valchkou.demo.model.SensorMeasureEntity
import com.valchkou.demo.repo.ISensorMeasureRepository
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.time.ZonedDateTime
import java.util.stream.Stream

@Slf4j
@Api(value = "Sensor Measures API")
@RestController
@RequestMapping("/api/sensor/measure")
class SensorMeasureController {

    @Autowired
    ISensorMeasureRepository repository

    @Autowired
    ObjectMapper mapper


    @ApiOperation(value = "Get measures by Sensor Id")
    @RequestMapping(value = "{sensorId}", method = RequestMethod.GET, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    List<SensorMeasureEntity> getForSensor(
            @ApiParam(value = "sensorId", required = true)
            @PathVariable int sensorId
        ) {

        Stream<SensorMeasureEntity> stream = repository.getAllBySensor(sensorId)

        // load all records into memory for the sake of simplicity. Truly streaming covered in the following project
        List<SensorMeasureEntity> result = []
        stream.forEach({
            result.add(it)
        })

        return result
    }

    @ApiOperation(value = "Get measures by Sensor Id and Date Range")
    @RequestMapping(value = "{sensorId}/range", method = RequestMethod.GET, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    List<SensorMeasureEntity> getForSensor(
            @ApiParam(value = "sensorId", required = true)
            @PathVariable int sensorId,

            @ApiParam(value = "Start Date ISO yyyy-MM-dd'T'HH:mm:ss.SSSZ (example: 2020-10-31T01:30:00.000-05:00)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,

            @ApiParam(value = "End Date ISO yyyy-MM-dd'T'HH:mm:ss.SSSZ (example: 2020-11-31T01:30:00.000-05:00)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate
            ) {


        List<SensorMeasureEntity> result = repository.getBySensorAndDateRange(
                sensorId,
                new Date(startDate.toInstant().toEpochMilli()),
                new Date(endDate.toInstant().toEpochMilli()))
        return result
    }

    @ApiOperation(value = "Save Sensor Measure")
    @RequestMapping(value = "{sensorId}", method = RequestMethod.POST, produces = [MediaType.APPLICATION_JSON_VALUE])
    void  save(
            @ApiParam(value = "sensorId", required = true)
            @PathVariable int sensorId,

            @ApiParam(value = "Measure Type", required = true)
            @RequestParam(required = true) MeasureType measureType,

            @ApiParam(value = "Measure Value", required = true)
            @RequestParam(required = true) Double measureValue

    ) {
        SensorMeasureEntity e = new SensorMeasureEntity()
        e.sensorId = sensorId
        e.measureType = measureType
        e.measureValue = measureValue
        repository.save(e)
    }

}