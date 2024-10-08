package pl.codehouse.restaurant.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.codehouse.restaurant.request.RequestDtoBuilder.CUSTOMER_ID;
import static pl.codehouse.restaurant.request.RequestDtoBuilder.REQUEST_ID;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_1;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_1_NAME;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_2;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.aRequestMenuItemOne;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.aRequestMenuItemTwo;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aMenuItemRequestOne;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aMenuItemRequestTwo;

@DisplayName("Request DTO Tests")
class RequestDtoTest {
    private final RequestEntity entity = new RequestEntity(REQUEST_ID, CUSTOMER_ID);
    private final MenuItemEntity item1 = new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false, false);
    private final MenuItemEntity item2 = new MenuItemEntity(MENU_ITEM_2, "Item 2", 1650, 1, false, false);

    @Test
    @DisplayName("Should convert with single MenuItem")
    void shouldConvertWithSingleMenuItem() {
        // given
        List<RequestMenuItemEntity> requestMenuItems = List.of(
                aMenuItemRequestOne()
                        .withQuantity(2)
                        .build()
        );
        List<RequestMenuItem> expectedMenuItems = List.of(
                aRequestMenuItemOne()
                        .withMenuItemName("Item 1")
                        .withQuantity(2)
                        .build()
        );

        // when
        RequestDto dto = RequestDto.from(entity, requestMenuItems, List.of(item1, item2));

        // then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("customerId", CUSTOMER_ID)
                .hasFieldOrPropertyWithValue("requestId", REQUEST_ID)
                .hasFieldOrPropertyWithValue("menuItems", expectedMenuItems);
    }

    @Test
    @DisplayName("Should convert with multiple MenuItems")
    void shouldConvertWithMultipleMenuItems() {
        // given
        List<RequestMenuItemEntity> requestMenuItems = List.of(
                aMenuItemRequestOne().withQuantity(2).build(),
                aMenuItemRequestTwo().withQuantity(4).build()
        );
        List<RequestMenuItem> expectedMenuItems = List.of(
                aRequestMenuItemOne()
                        .withMenuItemName("Item 1")
                        .withQuantity(2)
                        .build(),
                aRequestMenuItemTwo()
                        .withMenuItemName("Item 2")
                        .withQuantity(4)
                        .build()
        );

        // when
        RequestDto dto = RequestDto.from(entity, requestMenuItems, List.of(item1, item2));

        // then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("customerId", CUSTOMER_ID)
                .hasFieldOrPropertyWithValue("requestId", REQUEST_ID)
                .hasFieldOrPropertyWithValue("menuItems", expectedMenuItems);
    }

    @Test
    @DisplayName("Should throw exception when list of requested menu items does not contain known menu items")
    void shouldThrowExceptionWhenListOfRequestedMenuItemsDoesNotContainKnownMenuItems() {
        // given
        List<RequestMenuItemEntity> requestMenuItems = List.of(
                aMenuItemRequestTwo()
                        .withQuantity(4)
                        .build()
        );


        // when & then
        Assertions.assertThatThrownBy(() -> RequestDto.from(entity, requestMenuItems, List.of(item1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Menu item not found");
    }
}