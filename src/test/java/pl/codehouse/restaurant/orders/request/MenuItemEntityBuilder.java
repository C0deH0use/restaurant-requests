package pl.codehouse.restaurant.orders.request;

public class MenuItemEntityBuilder {
    public static final int MENU_ITEM_1_ID = 10001;
    public static final int MENU_ITEM_2_ID = 10002;
    public static final int MENU_ITEM_3_ID = 10003;
    public static final int MENU_ITEM_4_ID = 10004;
    public static final String MENU_ITEM_1_NAME = "Pizza Margherita";
    public static final String MENU_ITEM_2_NAME = "Spaghetti Carbonara";
    public static final String MENU_ITEM_3_NAME = "Pizza Diavolo";
    public static final String MENU_ITEM_4_NAME = "Cezar Salate";

    private int id;
    private String name;
    private long price;
    private int volume;
    private boolean packing;
    private boolean immediate;

    private MenuItemEntityBuilder() {
        this.id = MENU_ITEM_1_ID;
        this.name = MENU_ITEM_1_NAME;
        this.price = 1000; // 10.00 in cents
        this.volume = 1;
        this.packing = false;
        this.immediate = false;
    }

    public static MenuItemEntityBuilder aMenuItemEntity() {
        return new MenuItemEntityBuilder();
    }

    public static MenuItemEntityBuilder aMenuItemEntityOne() {
        return aMenuItemEntity()
                .withId(MENU_ITEM_1_ID)
                .withName(MENU_ITEM_1_NAME);
    }

    public static MenuItemEntityBuilder aMenuItemEntityTwo() {
        return aMenuItemEntity()
                .withId(MENU_ITEM_2_ID)
                .withName(MENU_ITEM_2_NAME)
                .withPrice(1200); // 12.00 in cents
    }

    public static MenuItemEntityBuilder aMenuItemEntityThree() {
        return aMenuItemEntity()
                .withId(MENU_ITEM_3_ID)
                .withName(MENU_ITEM_3_NAME)
                .withPrice(1900); // 12.00 in cents
    }

    public static MenuItemEntityBuilder aMenuItemEntityFour() {
        return aMenuItemEntity()
                .withId(MENU_ITEM_4_ID)
                .withName(MENU_ITEM_4_NAME)
                .withPrice(1300)
                .withImmediate(); // 12.00 in cents
    }

    public MenuItemEntityBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public MenuItemEntityBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuItemEntityBuilder withPrice(long price) {
        this.price = price;
        return this;
    }

    public MenuItemEntityBuilder withVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public MenuItemEntityBuilder withPacking() {
        this.packing = true;
        return this;
    }

    public MenuItemEntityBuilder withImmediate() {
        this.immediate = true;
        return this;
    }

    public MenuItemEntity build() {
        return new MenuItemEntity(id, name, price, volume, packing, immediate);
    }
}
