package pl.codehouse.restaurant.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/request")
class RequestResource {

    private final MenuItemRepository menuItemRepository;
    private final RequestRepository requestRepository;
    private final RequestCreationCommand requestCreationCommand;

    RequestResource(MenuItemRepository menuItemRepository, RequestRepository requestRepository, RequestCreationCommand requestCreationCommand) {
        this.menuItemRepository = menuItemRepository;
        this.requestRepository = requestRepository;
        this.requestCreationCommand = requestCreationCommand;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<RequestDto> createRequest(@RequestBody RequestPayload request) {
        return requestCreationCommand.execute(new Context<>(request))
                .map(ExecutionResult::handle);
    }

    @GetMapping("/menu-items")
    Mono<List<MenuItem>> fetchAvailableMenuItems() {
        return menuItemRepository.findAll()
                .map(MenuItem::from)
                .collectList();
    }
}
