package fr.insee.sabianedata.ws.controller;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.service.PearlApiService;
import fr.insee.sabianedata.ws.service.QueenApiService;

/**
 * HealthCheck is the Controller used to check if own API and Pearl and Queen
 * API are alive
 *
 * @author Simon Demaziere
 */
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@Slf4j
public class HealthCheckController {

    private final PearlApiService pearlApiService;
    private final QueenApiService queenApiService;

    private static final String OK = "OK";
    private static final String KO = "KO";

    @Operation(summary = "Healthcheck, check if api are alive", description = "Healthcheck on Pearl and Queen API")
    @GetMapping(path = "/healthcheck")
    public ResponseEntity<ResponseModel> healthCheck(HttpServletRequest request) {
        boolean pearlApiIsHealthy = pearlApiService.healthCheck(request);
        boolean queenApiIsHealthy = queenApiService.healthCheck(request);
        String responseMessage = String.format("Pearl-API : %b - Queen-API : %b", pearlApiIsHealthy, queenApiIsHealthy);
        ResponseModel response = new ResponseModel(pearlApiIsHealthy && queenApiIsHealthy, responseMessage);
        return ResponseEntity.ok().body(response);

    }

    @Operation(summary = "Healthcheck, check if Queen api is alive")
    @GetMapping(path = "/queen/api/healthcheck")
    public ResponseEntity<Object> healthCheckQueen(HttpServletRequest request) {
        boolean queenIsHealthy = queenApiService.healthCheck(request);
        log.debug("HealthCheck on Queen API resulted in {}", queenIsHealthy ? OK : KO);
        return queenIsHealthy ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();

    }

    @Operation(summary = "Healthcheck, check if Pearl api is alive")
    @GetMapping(path = "/pearl/api/healthcheck")
    public ResponseEntity<Object> healthCheckPearl(HttpServletRequest request) {
        boolean pearlIsHealthy = pearlApiService.healthCheck(request);
        log.debug("HealthCheck on Pearl API resulted in {}", pearlIsHealthy ? OK : KO);
        return pearlIsHealthy ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();

    }
}
