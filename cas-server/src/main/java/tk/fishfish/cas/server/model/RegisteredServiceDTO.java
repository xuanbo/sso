package tk.fishfish.cas.server.model;

import lombok.Data;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceAttributeReleasePolicy;
import org.apereo.cas.services.RegisteredServiceMatchingStrategy;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.springframework.validation.annotation.Validated;
import tk.fishfish.cas.server.services.StartsWithRegisteredServiceMatchingStrategy;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

/**
 * 服务注册DTO
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Data
@Validated
public class RegisteredServiceDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String serviceId;

    /**
     * 服务匹配策略
     */
    private RegisteredServiceMatchingStrategy matchingStrategy;

    /**
     * 属性策略
     */
    private RegisteredServiceAttributeReleasePolicy attributeReleasePolicy;

    public static RegisteredService convert(RegisteredServiceDTO dto) {
        RegexRegisteredService service = new RegexRegisteredService();
        Optional.ofNullable(dto.id).ifPresent(service::setId);
        service.setServiceId(dto.serviceId);
        service.setName(dto.name);
        service.setDescription(dto.description);
        service.setMatchingStrategy(new StartsWithRegisteredServiceMatchingStrategy());
        service.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
        return service;
    }

    public static RegisteredServiceDTO convert(RegisteredService service) {
        RegisteredServiceDTO dto = new RegisteredServiceDTO();
        dto.id = service.getId();
        dto.serviceId = service.getServiceId();
        dto.name = service.getName();
        dto.description = service.getDescription();
        dto.matchingStrategy = service.getMatchingStrategy();
        dto.attributeReleasePolicy = service.getAttributeReleasePolicy();
        return dto;
    }

}
