package com.pointtils.pointtils.src.application.clients;

import com.pointtils.pointtils.src.application.dto.CityResponseDTO;
import com.pointtils.pointtils.src.application.dto.LocationDataDTO;
import com.pointtils.pointtils.src.application.dto.StateResponseDTO;
import com.pointtils.pointtils.src.core.domain.exceptions.ClientTimeoutException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class IbgeClient {

    private final RestTemplate restTemplate;
    private final String stateUrl;
    private final String cityUrl;

    public IbgeClient(@Qualifier("ibgeRestTemplate") RestTemplate restTemplate,
                      @Value("${client.ibge.state-url}") String stateUrl,
                      @Value("${client.ibge.city-url}") String cityUrl) {
        this.restTemplate = restTemplate;
        this.stateUrl = stateUrl;
        this.cityUrl = cityUrl;
    }

    public List<LocationDataDTO> getStateList() {
        try {
            log.info("Iniciando uma requisicao para a API do IBGE para buscar a lista de UFs");
            ResponseEntity<StateResponseDTO[]> stateResponse = restTemplate.getForEntity(stateUrl, StateResponseDTO[].class);
            Function<StateResponseDTO, LocationDataDTO> mapper = state -> new LocationDataDTO(state.getAbbreviation());
            return formatResponseData(stateResponse.getBody(), mapper, "UFs não encontradas");
        } catch (ResourceAccessException ex) {
            log.error("Timeout na requisicao para a API do IBGE para buscar a lista de UFs", ex);
            throw new ClientTimeoutException("Timeout ao acessar o serviço de UFs");
        }
    }

    public List<LocationDataDTO> getCityListByState(String state) {
        try {
            log.info("Iniciando uma requisicao para a API do IBGE para buscar a lista de municípios da UF {}", state);
            ResponseEntity<CityResponseDTO[]> cityResponse = restTemplate.getForEntity(cityUrl, CityResponseDTO[].class, state);
            Function<CityResponseDTO, LocationDataDTO> mapper = city -> new LocationDataDTO(city.getName());
            return formatResponseData(cityResponse.getBody(), mapper, "Municípios não encontrados");
        } catch (ResourceAccessException ex) {
            log.error("Timeout na requisicao para a API do IBGE para buscar a lista de municípios da UF {}", state, ex);
            throw new ClientTimeoutException("Timeout ao acessar o serviço de municípios");
        }
    }

    private <T> List<LocationDataDTO> formatResponseData(T[] responseBody,
                                                         Function<? super T, LocationDataDTO> mapper,
                                                         String errorMessage) {
        if (ArrayUtils.isEmpty(responseBody)) {
            throw new EntityNotFoundException(errorMessage);
        }
        return Arrays.stream(responseBody)
                .map(mapper)
                .sorted(Comparator.comparing(LocationDataDTO::getName))
                .toList();
    }
}
