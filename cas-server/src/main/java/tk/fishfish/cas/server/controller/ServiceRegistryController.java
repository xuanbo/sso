package tk.fishfish.cas.server.controller;

import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.fishfish.cas.server.model.RegisteredServiceDTO;

/**
 * 服务注册管理
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@RestController
@RequestMapping("/v1/registry-service")
public class ServiceRegistryController {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @PostMapping
    public RegisteredService create(@RequestBody RegisteredServiceDTO dto) {
        return serviceRegistry.save(RegisteredServiceDTO.convert(dto));
    }

    @PutMapping
    public RegisteredService modify(@RequestBody RegisteredServiceDTO dto) {
        return serviceRegistry.save(RegisteredServiceDTO.convert(dto));
    }

    @DeleteMapping("/{id}")
    public void removeById(@PathVariable long id) {
        RegisteredService service = serviceRegistry.findServiceById(id);
        if (service == null) {
            return;
        }
        serviceRegistry.delete(service);
    }

}
