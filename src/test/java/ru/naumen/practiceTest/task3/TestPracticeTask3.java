package ru.naumen.practiceTest.task3;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.http.ContentType;
import ru.naumen.practiceTest.RestTestBase;

/**
 * Тестирование практического задания 3
 * @author vmikolyuk
 * @since 20.04.2022
 */
public class TestPracticeTask3 extends RestTestBase
{
    private static final Deque<Map<String, Object>> storedEntities = new LinkedList<>();

    private static Map<String, Object> client1 = new HashMap<>();
    private static Map<String, Object> client2 = new HashMap<>();
    private static Map<String, Object> client3 = new HashMap<>();

    private static Map<String, Object> parentCategory = new HashMap<>();
    private static Map<String, Object> childCategory = new HashMap<>();

    private static Map<String, Object> product1 = new HashMap<>();
    private static Map<String, Object> product2 = new HashMap<>();
    private static Map<String, Object> product3 = new HashMap<>();
    private static Map<String, Object> product4 = new HashMap<>();

    private static Map<String, Object> clientOrder1 = new HashMap<>();
    private static Map<String, Object> clientOrder2 = new HashMap<>();
    private static Map<String, Object> clientOrder3 = new HashMap<>();
    private static Map<String, Object> clientOrder4 = new HashMap<>();

    /**
     * Создать тестовые сущности
     */
    @BeforeClass
    public static void createTestEntities()
    {
        // Создаём клиентов
        client1 = createClient("client1 FullName", "client 1 address", "PhoneNumber1", 1L);
        client2 = createClient("client2 FullName", "client 2 address", "PhoneNumber2", 2L);
        client3 = createClient("client3 FullName", "client 3 address", "PhoneNumber3", 3L);

        // Создаём категории
        parentCategory = createCategory("parent Category", null);
        childCategory = createCategory("child Category", parentCategory);

        // Создаём товары
        product1 = createProduct("product 1 name", childCategory, "product 1 description", 1.5);
        product2 = createProduct("product 2 name", childCategory, "product 2 description", 2.5);
        product3 = createProduct("product 3 name", childCategory, "product 3 description", 3.5);
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
    public static void deleteTestEntities()
    {
        // Удаляем сущности
        while (!storedEntities.isEmpty())
        {
            deleteEntity((String)storedEntities.removeLast().get(SELF_KEY));
        }
    }

    /**
     * Протестировать получение всех товаров, купленных когда-либо клиентом по его идентификатору, методом
     * /rest/listClientProducts?clientId=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testListClientProducts()
    {
        String requestPath = "/rest/listClientProducts?clientId=" + getIdFromPath((String)client1.get(SELF_KEY));

        // Выполняем запрос
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
                .when()
            .get(requestPath)
                .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 2 товара
        Assert.assertEquals(2, response.size());

        // Проверяем, что у товаров ответа корректная категория
        response.forEach(product ->
        {
            Assert.assertEquals(
                    getIdFromPath((String)childCategory.get(SELF_KEY)),
                    ((Map<String, Object>)product.get(PRODUCT_CATEGORY_KEY)).get(ID_KEY)
            );
            product.remove(PRODUCT_CATEGORY_KEY);
        });

        // Создаём временные копии товаров для сравнения с товарами ответа
        List<Map<String, Object>> productsToCompare = new ArrayList<>();
        productsToCompare.add(new HashMap<>(product1));
        productsToCompare.add(new HashMap<>(product2));

        // Подготавливаем копии товаров (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        productsToCompare.forEach(product ->
        {
            product.put(ID_KEY, getIdFromPath((String)product.get(SELF_KEY)));
            product.remove(SELF_KEY);
            product.remove(PRODUCT_KEY);
            product.remove(PRODUCT_CATEGORY_KEY);
            Assert.assertTrue(response.contains(product));
        });
    }

    /**
     * Протестировать получение всех заказов клиента по его имени методом listClientOrders?clientName=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testListClientOrdersByClientName()
    {
        String client1FullName = (String)client1.get(CLIENT_FULL_NAME_KEY);
        String requestPath = "/rest/listClientOrders?clientName=" + client1FullName;

        // Выполняем запрос
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 2 заказа
        Assert.assertEquals(2, response.size());

        // Проверяем, что у заказов ответа корректный клиент
        response.forEach(clientOrder ->
                {
                    Map<String, Object> client = (Map<String, Object>)clientOrder.get(CLIENT_ORDER_CLIENT_KEY);
                    Assert.assertNotNull(client);
                    Assert.assertEquals(client1FullName, client.get(CLIENT_FULL_NAME_KEY));
                    clientOrder.remove(CLIENT_ORDER_CLIENT_KEY);
                }
        );

        // Создаём временные копии заказов для сравнения с заказами ответа
        List<Map<String, Object>> clientOrdersToCompare = new ArrayList<>();
        clientOrdersToCompare.add(new HashMap<>(clientOrder1));
        clientOrdersToCompare.add(new HashMap<>(clientOrder4));

        // Подготавливаем копии заказов (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        clientOrdersToCompare.forEach(clientOrder ->
        {
            clientOrder.put(ID_KEY, getIdFromPath((String)clientOrder.get(SELF_KEY)));
            clientOrder.remove(SELF_KEY);
            clientOrder.remove(CLIENT_ORDER_KEY);
            clientOrder.remove(CLIENT_ORDER_CLIENT_KEY);
            Assert.assertTrue(response.contains(clientOrder));
        });
    }

    /**
     * Протестировать получение {top} самых популярных товаров среди клиентов методом /rest/topPopularProducts?top=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTopPopularProducts()
    {
        String requestPath = "/rest/topPopularProducts?top=";

        // Выполняем запрос, top=3
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath + 3)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 3 товара
        Assert.assertEquals(3, response.size());

        // Проверяем, что ответ содержит правильные товары в правильном порядке (с наибольшим количеством в заказах в
        // порядке убывания количества)
        Assert.assertEquals(response.get(0).get(PRODUCT_NAME_KEY), product3.get(PRODUCT_NAME_KEY));
        Assert.assertEquals(response.get(1).get(PRODUCT_NAME_KEY), product2.get(PRODUCT_NAME_KEY));
        Assert.assertEquals(response.get(2).get(PRODUCT_NAME_KEY), product1.get(PRODUCT_NAME_KEY));

        // Выполняем запрос, top=1
        //@formatter:off
        response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath + 1)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 1 товар
        Assert.assertEquals(1, response.size());

        // Проверяем, что ответ содержит самый популярный товар
        Assert.assertEquals(response.get(0).get(PRODUCT_NAME_KEY), product3.get(PRODUCT_NAME_KEY));
    }

    /**
     * Протестировать получение заказов в определённом статусе методом /rest/orders?status=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testClientOrdersByStatus()
    {
        String requestPath = "/rest/orders?status=1";

        // Выполняем запрос
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 2 заказа
        Assert.assertEquals(2, response.size());

        // Проверяем, у заказов ответа корректный статус и подготавливаем их для дальнейшей проверки
        response.forEach(clientOrder ->
                {
                    Assert.assertEquals(1, clientOrder.get(CLIENT_ORDER_STATUS_KEY));
                    clientOrder.remove(CLIENT_ORDER_CLIENT_KEY);
                }
        );

        // Создаём временные копии заказов в требуемом статусе для сравнения с заказами ответа
        List<Map<String, Object>> clientOrdersToCompare = new ArrayList<>();
        clientOrdersToCompare.add(new HashMap<>(clientOrder1));
        clientOrdersToCompare.add(new HashMap<>(clientOrder3));

        // Подготавливаем копии заказов (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        clientOrdersToCompare.forEach(clientOrder ->
        {
            clientOrder.put(ID_KEY, getIdFromPath((String)clientOrder.get(SELF_KEY)));
            clientOrder.remove(SELF_KEY);
            clientOrder.remove(CLIENT_ORDER_KEY);
            clientOrder.remove(CLIENT_ORDER_CLIENT_KEY);
            Assert.assertTrue(response.contains(clientOrder));
        });
    }

    /**
     * Протестировать получение товаров по идентификатору категории методом /rest/products?categoryId=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProductsByCategoryId()
    {
        String requestPath = "/rest/products?categoryId=" + getIdFromPath((String)childCategory.get(SELF_KEY));

        // Выполняем запрос
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 3 товара
        Assert.assertEquals(3, response.size());

        // Проверяем, что у товаров ответа корректная категория
        response.forEach(product ->
        {
            Assert.assertEquals(
                    getIdFromPath((String)childCategory.get(SELF_KEY)),
                    ((Map<String, Object>)product.get(PRODUCT_CATEGORY_KEY)).get(ID_KEY)
            );
            product.remove(PRODUCT_CATEGORY_KEY);
        });

        // Создаём временные копии товаров для сравнения с товарами ответа
        List<Map<String, Object>> productsToCompare = new ArrayList<>();
        productsToCompare.add(new HashMap<>(product1));
        productsToCompare.add(new HashMap<>(product2));
        productsToCompare.add(new HashMap<>(product3));

        // Подготавливаем копии товаров (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        productsToCompare.forEach(product ->
        {
            product.put(ID_KEY, getIdFromPath((String)product.get(SELF_KEY)));
            product.remove(SELF_KEY);
            product.remove(PRODUCT_KEY);
            product.remove(PRODUCT_CATEGORY_KEY);
            Assert.assertTrue(response.contains(product));
        });
    }

    /**
     * Протестировать получение товара по названию методом /rest/products?name=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProductByName()
    {
        String requestPath = "/rest/products?name=" + product2.get(PRODUCT_NAME_KEY);

        // Выполняем запрос
        //@formatter:off
        Map<String, Object> productResponse = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(Map.class);
        //@formatter:on

        // Проверяем, что в ответе содержится корректный товар
        Assert.assertEquals(product2.get(PRODUCT_NAME_KEY), productResponse.get(PRODUCT_NAME_KEY));
        Assert.assertEquals(
                childCategory.get(CATEGORY_NAME_KEY),
                ((Map<String, Object>)productResponse.get(PRODUCT_CATEGORY_KEY)).get(CATEGORY_NAME_KEY)
        );
        Assert.assertEquals(product2.get(PRODUCT_DESCRIPTION_KEY), productResponse.get(PRODUCT_DESCRIPTION_KEY));
        Assert.assertEquals(product2.get(PRODUCT_PRICE_KEY), productResponse.get(PRODUCT_PRICE_KEY));
    }

    /**
     * Тестирование получения клиента по имени методом /rest/clients?name=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testClientByName()
    {
        String requestPath = "/rest/clients?name=" + client2.get(CLIENT_FULL_NAME_KEY);
        
        // Выполняем запрос
        //@formatter:off
        Map<String, Object> response = given()
                .contentType(ContentType.JSON)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(Map.class);
        //@formatter:on

        // Проверяем, что в ответе содержится корректный клиент
        Assert.assertEquals(client2.get(CLIENT_FULL_NAME_KEY), response.get(CLIENT_FULL_NAME_KEY));
        Assert.assertEquals(client2.get(CLIENT_ADDRESS_KEY), response.get(CLIENT_ADDRESS_KEY));
        Assert.assertEquals(client2.get(CLIENT_PHONE_NUMBER_KEY), response.get(CLIENT_PHONE_NUMBER_KEY));
        Assert.assertEquals(client2.get(CLIENT_EXTERNAL_ID_KEY), response.get(CLIENT_EXTERNAL_ID_KEY));
    }

    /**
     * Получить идентификатор сущности из её пути
     * @param path путь
     * @return идентификатор
     */
    private static Integer getIdFromPath(String path)
    {
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
            Integer countProduct)
    {
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
     * @param client клиент
     * @param status статус
     * @param total сумма
     * @return ответ сервера - созданный заказ клиента
     */
    private static Map<String, Object> createClientOrder(Map<String, Object> client, Integer status, Double total)
    {
        Map<String, Object> clientOrder = new HashMap<>();
        clientOrder.put(CLIENT_ORDER_CLIENT_KEY, null == client ? null : client.get(SELF_KEY));
        clientOrder.put(CLIENT_ORDER_STATUS_KEY, status);
        clientOrder.put(CLIENT_ORDER_TOTAL_KEY, total);
        return postEntityAndStoreForCleanUp(clientOrder, CLIENT_ORDERS_PATH);
    }

    /**
     * Создать товар
     * @param name название
     * @param category категория
     * @param description описание
     * @param price цена
     * @return ответ сервера - созданный товар
     */
    private static Map<String, Object> createProduct(String name, Map<String, Object> category, String description,
            Double price)
    {
        Map<String, Object> product = new HashMap<>();
        product.put(PRODUCT_NAME_KEY, name);
        product.put(PRODUCT_CATEGORY_KEY, null == category ? null : category.get(SELF_KEY));
        product.put(PRODUCT_DESCRIPTION_KEY, description);
        product.put(PRODUCT_PRICE_KEY, price);
        return postEntityAndStoreForCleanUp(product, PRODUCTS_PATH);
    }

    /**
     * Создать категорию
     * @param name название
     * @param parent родительская категория
     * @return ответ сервера - созданная категория
     */
    private static Map<String, Object> createCategory(String name, Map<String, Object> parent)
    {
        Map<String, Object> category = new HashMap<>();
        category.put(CATEGORY_NAME_KEY, name);
        category.put(CATEGORY_PARENT_KEY, null == parent ? null : parent.get(SELF_KEY));
        return postEntityAndStoreForCleanUp(category, CATEGORIES_PATH);
    }

    /**
     * Создать клиента
     * @param fullName полное имя
     * @param address адрес
     * @param phoneNumber номер телефона
     * @param externalId внешний идентификатор
     * @return ответ сервера - созданный клиент
     */
    private static Map<String, Object> createClient(String fullName, String address, String phoneNumber,
            long externalId)
    {
        Map<String, Object> client = new HashMap<>();
        client.put(CLIENT_FULL_NAME_KEY, fullName);
        client.put(CLIENT_ADDRESS_KEY, address);
        client.put(CLIENT_PHONE_NUMBER_KEY, phoneNumber);
        client.put(CLIENT_EXTERNAL_ID_KEY, externalId);
        return postEntityAndStoreForCleanUp(client, CLIENTS_PATH);
    }

    /**
     * Отправить post-запрос на создание сущности и сохранить ответ для последующего удаления
     * @param entity создаваемая сущность
     * @param path путь запроса
     * @return ответ сервера - созданная сущность
     */
    private static Map<String, Object> postEntityAndStoreForCleanUp(Map<String, Object> entity, String path)
    {
        Map<String, Object> responseEntity = postEntity(entity, path);
        storedEntities.addLast(responseEntity);
        return responseEntity;
    }
}
