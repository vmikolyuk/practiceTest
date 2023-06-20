package ru.naumen.practiceTest.task3;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.naumen.practiceTest.RestTestBase;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Наполнение БД для практического задания 3
 *
 * @author vmikolyuk
 * @since 20.04.2022
 */
public class Task3TestBase extends RestTestBase {
    private static final Deque<Map<String, Object>> storedEntities = new LinkedList<>();

    protected static Map<String, Object> client1 = new HashMap<>();
    protected static Map<String, Object> client2 = new HashMap<>();
    protected static Map<String, Object> client3 = new HashMap<>();

    protected static Map<String, Object> parentCategory = new HashMap<>();
    protected static Map<String, Object> childCategory = new HashMap<>();

    protected static Map<String, Object> product1 = new HashMap<>();
    protected static Map<String, Object> product2 = new HashMap<>();
    protected static Map<String, Object> product3 = new HashMap<>();
    protected static Map<String, Object> product4 = new HashMap<>();

    protected static Map<String, Object> clientOrder1 = new HashMap<>();
    protected static Map<String, Object> clientOrder2 = new HashMap<>();
    protected static Map<String, Object> clientOrder3 = new HashMap<>();
    protected static Map<String, Object> clientOrder4 = new HashMap<>();

    /**
     * Создать тестовые сущности
     */
    @BeforeClass
    public static void createTestEntities() {
        // Создаём клиентов
        client1 = createClient("client1 FullName", "client 1 address", "PhoneNumber1", 1L);
        client2 = createClient("client2 Name", "client 2 address", "PhoneNumber2", 2L);
        client3 = createClient("client3 FullName", "client 3 address", "PhoneNumber3", 3L);

        // Создаём категории
        parentCategory = createCategory("parent Category", null);
        childCategory = createCategory("child Category", parentCategory);

        // Создаём товары
        product1 = createProduct("test product 1 name", childCategory, "product 1 description", 1.5);
        product2 = createProduct("some product 2 name", childCategory, "product 2 description", 2.5);
        product3 = createProduct("some product 3 name", childCategory, "product 3 description", 3.5);
        product4 = createProduct("product 4 name", parentCategory, "product 4 description", 4.5);

        // Создаём заказы клиентов
        clientOrder1 = createClientOrder(client1, 1, 123.5);
        clientOrder2 = createClientOrder(client2, 2, 234.5);
        clientOrder3 = createClientOrder(client3, 1, 345.5);
        clientOrder4 = createClientOrder(client1, 2, 456.5);

        // Создаём связи товаров с заказами
        createOrderProduct(clientOrder1, product1, 1);
        createOrderProduct(clientOrder1, product2, 2);
        createOrderProduct(clientOrder2, product3, 4);
        createOrderProduct(clientOrder4, product2, 1);
    }

    /**
     * Удалить тестовые сущности
     */
    @AfterClass
    public static void deleteTestEntities() {
        // Удаляем сущности
        while (!storedEntities.isEmpty()) {
            deleteEntity((String) storedEntities.removeLast().get(SELF_KEY));
        }
    }

    /**
     * Получить идентификатор сущности из её пути
     *
     * @param path путь
     * @return идентификатор
     */
    protected static Integer getIdFromPath(String path) {
        String[] pathSplitString = path.split("/");
        String idString = pathSplitString[pathSplitString.length - 1];
        return Integer.valueOf(idString);
    }

    /**
     * Создать связь товара с заказом (сущность "заказ-товар")
     *
     * @param clientOrder  заказ клиента
     * @param product      товар
     * @param countProduct количество
     */
    private static void createOrderProduct(Map<String, Object> clientOrder, Map<String, Object> product,
                                           Integer countProduct) {
        Map<String, Object> orderProduct = new HashMap<>();
        orderProduct.put(
                ORDER_PRODUCT_CLIENT_ORDER_KEY,
                null == clientOrder ? null : clientOrder.get(ORDER_PRODUCT_CLIENT_ORDER_KEY)
        );
        orderProduct.put(
                ORDER_PRODUCT_PRODUCT_KEY,
                null == product ? null : product.get(ORDER_PRODUCT_PRODUCT_KEY)
        );
        orderProduct.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, countProduct);
        postEntityAndStoreForCleanUp(orderProduct, ORDER_PRODUCTS_PATH);
    }

    /**
     * Создать заказ клиента
     *
     * @param client клиент
     * @param status статус
     * @param total  сумма
     * @return ответ сервера - созданный заказ клиента
     */
    private static Map<String, Object> createClientOrder(Map<String, Object> client, Integer status, Double total) {
        Map<String, Object> clientOrder = new HashMap<>();
        clientOrder.put(CLIENT_ORDER_CLIENT_KEY, null == client ? null : client.get(SELF_KEY));
        clientOrder.put(CLIENT_ORDER_STATUS_KEY, status);
        clientOrder.put(CLIENT_ORDER_TOTAL_KEY, total);
        return postEntityAndStoreForCleanUp(clientOrder, CLIENT_ORDERS_PATH);
    }

    /**
     * Создать товар
     *
     * @param name        название
     * @param category    категория
     * @param description описание
     * @param price       цена
     * @return ответ сервера - созданный товар
     */
    private static Map<String, Object> createProduct(String name, Map<String, Object> category, String description,
                                                     Double price) {
        Map<String, Object> product = new HashMap<>();
        product.put(PRODUCT_NAME_KEY, name);
        product.put(PRODUCT_CATEGORY_KEY, null == category ? null : category.get(SELF_KEY));
        product.put(PRODUCT_DESCRIPTION_KEY, description);
        product.put(PRODUCT_PRICE_KEY, price);
        return postEntityAndStoreForCleanUp(product, PRODUCTS_PATH);
    }

    /**
     * Создать категорию
     *
     * @param name   название
     * @param parent родительская категория
     * @return ответ сервера - созданная категория
     */
    private static Map<String, Object> createCategory(String name, Map<String, Object> parent) {
        Map<String, Object> category = new HashMap<>();
        category.put(CATEGORY_NAME_KEY, name);
        category.put(CATEGORY_PARENT_KEY, null == parent ? null : parent.get(SELF_KEY));
        return postEntityAndStoreForCleanUp(category, CATEGORIES_PATH);
    }

    /**
     * Создать клиента
     *
     * @param fullName    полное имя
     * @param address     адрес
     * @param phoneNumber номер телефона
     * @param externalId  внешний идентификатор
     * @return ответ сервера - созданный клиент
     */
    private static Map<String, Object> createClient(String fullName, String address, String phoneNumber,
                                                    long externalId) {
        Map<String, Object> client = new HashMap<>();
        client.put(CLIENT_FULL_NAME_KEY, fullName);
        client.put(CLIENT_ADDRESS_KEY, address);
        client.put(CLIENT_PHONE_NUMBER_KEY, phoneNumber);
        client.put(CLIENT_EXTERNAL_ID_KEY, externalId);
        return postEntityAndStoreForCleanUp(client, CLIENTS_PATH);
    }

    /**
     * Отправить post-запрос на создание сущности и сохранить ответ для последующего удаления
     *
     * @param entity создаваемая сущность
     * @param path   путь запроса
     * @return ответ сервера - созданная сущность
     */
    private static Map<String, Object> postEntityAndStoreForCleanUp(Map<String, Object> entity, String path) {
        Map<String, Object> responseEntity = postEntity(entity, path);
        storedEntities.addLast(responseEntity);
        return responseEntity;
    }
}
