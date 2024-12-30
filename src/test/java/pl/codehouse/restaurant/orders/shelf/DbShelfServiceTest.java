package pl.codehouse.restaurant.orders.shelf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_NAME;
import static pl.codehouse.restaurant.orders.request.RequestMenuItemBuilder.aRequestMenuItemOne;
import static pl.codehouse.restaurant.orders.shelf.ShelfBuilder.aShelf;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.codehouse.restaurant.orders.request.RequestMenuItem;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DbShelfServiceTest {
    private static final long NEW_VERSION_VALUE = 0L;

    private final Clock clock = Clock.fixed(Instant.parse("2024-12-30T10:15:30.00Z"), ZoneOffset.UTC);
    private final LocalDateTime updatedAt = LocalDateTime.now(clock);

    @InjectMocks
    private DbShelfService sut;

    @Mock
    private ShelfRepository shelfRepository;

    @Captor
    private ArgumentCaptor<ShelfEntity> shelfEntityCaptor;

    @BeforeEach
    void setUp() {
        this.sut = new DbShelfService(clock, shelfRepository);
    }

    @Test
    @DisplayName("should return existing shelf entity when one exists by request menu item id")
    void should_ReturnExistingShelfEntity_When_OneExistsByRequestMenuItemId() {
        // given
        RequestMenuItem menuItem = aRequestMenuItemOne().build();
        
        given(shelfRepository.findByMenuItemId(MENU_ITEM_1_ID)).willReturn(Mono.just(aShelf().build()));
        
        // when 
        Mono<ShelfEntity> resultMono = sut.findByMenuItem(menuItem);

        StepVerifier.create(resultMono)
                .assertNext(shelf -> assertThat(shelf).isNotNull())
                .verifyComplete();
    }

    @Test
    @DisplayName("should return newly created shelf entity when one does not exist by request menu item id")
    void should_ReturnNewShelfEntity_When_OneDoesNotExistByRequestMenuItemId() {
        // given
        RequestMenuItem menuItem = aRequestMenuItemOne().build();

        given(shelfRepository.findByMenuItemId(MENU_ITEM_1_ID)).willReturn(Mono.empty());
        given(shelfRepository.save(any())).willReturn(Mono.just(aShelf().build()));

        // when
        Mono<ShelfEntity> resultMono = sut.findByMenuItem(menuItem);

        StepVerifier.create(resultMono)
                .assertNext(shelf -> assertThat(shelf).isNotNull())
                .verifyComplete();

        // and
        then(shelfRepository).should(times(1)).save(shelfEntityCaptor.capture());
        assertThat(shelfEntityCaptor.getValue())
                        .hasFieldOrPropertyWithValue("id", 0)
                        .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_1_ID)
                        .hasFieldOrPropertyWithValue("itemName", MENU_ITEM_1_NAME)
                        .hasFieldOrPropertyWithValue("quantity", 0)
                        .hasFieldOrPropertyWithValue("updatedAt", updatedAt)
                        .hasFieldOrPropertyWithValue("version", NEW_VERSION_VALUE)
        ;
    }
}