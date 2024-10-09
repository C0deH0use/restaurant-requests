package pl.codehouse.restaurant.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final RequestService requestService;
    private final CreateCommand createCommand;

    RequestResource(MenuItemRepository menuItemRepository, RequestService requestService, CreateCommand createCommand) {
        this.menuItemRepository = menuItemRepository;
        this.requestService = requestService;
        this.createCommand = createCommand;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<RequestDto> createRequest(@RequestBody RequestPayload request) {
        return createCommand.execute(new Context<>(request))
                .map(ExecutionResult::handle);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<RequestDto> createRequest(@PathVariable int requestId) {
        return requestService.findById(requestId);
    }

    @GetMapping("/menu-items")
    Mono<List<MenuItem>> fetchAvailableMenuItems() {
        return menuItemRepository.findAll()
                .map(MenuItem::from)
                .collectList();
    }
}
