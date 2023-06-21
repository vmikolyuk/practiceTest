package ru.naumen.practiceTest.task3;

import io.restassured.http.ContentType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Тестирование практического задания 3
 *
 * @author vmikolyuk
 * @since 20.04.2022
 */
class TestPracticeTask3 extends Task3TestBase {
    private static final String PARAM_LIMIT = "limit";

    /**
     * Протестировать получение всех товаров, купленных когда-либо клиентом, методом
     * GET /rest/clients/{id}/products
     */
    @SuppressWarnings("unchecked")
    @Test
    void testGetClientProducts() {
        String requestPath = String.format("/rest/clients/%s/products", getIdFromPath((String) client1.get(SELF_KEY)));

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
        Assertions.assertEquals(2, response.size());

        // Проверяем, что у товаров ответа корректная категория
        response.forEach(product ->
        {
            Assertions.assertEquals(
                    getIdFromPath((String) childCategory.get(SELF_KEY)),
                    ((Map<String, Object>) product.get(PRODUCT_CATEGORY_KEY)).get(ID_KEY)
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
            product.put(ID_KEY, getIdFromPath((String) product.get(SELF_KEY)));
            product.remove(SELF_KEY);
            product.remove(PRODUCT_KEY);
            product.remove(PRODUCT_CATEGORY_KEY);
            Assertions.assertTrue(response.contains(product));
        });
    }

    /**
     * Протестировать получение всех заказов клиента методом
     * GET /rest/clients/{id}/orders
     */
    @SuppressWarnings("unchecked")
    @Test
    void testGetClientOrders() {
        String client1FullName = (String) client1.get(CLIENT_FULL_NAME_KEY);
        String requestPath = String.format("/rest/clients/%s/orders", getIdFromPath((String) client1.get(SELF_KEY)));

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
        Assertions.assertEquals(2, response.size());

        // Проверяем, что у заказов ответа корректный клиент
        response.forEach(clientOrder ->
                {
                    Map<String, Object> client = (Map<String, Object>) clientOrder.get(CLIENT_ORDER_CLIENT_KEY);
                    Assertions.assertNotNull(client);
                    Assertions.assertEquals(client1FullName, client.get(CLIENT_FULL_NAME_KEY));
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
            clientOrder.put(ID_KEY, getIdFromPath((String) clientOrder.get(SELF_KEY)));
            clientOrder.remove(SELF_KEY);
            clientOrder.remove(CLIENT_ORDER_KEY);
            clientOrder.remove(CLIENT_ORDER_CLIENT_KEY);
            Assertions.assertTrue(response.contains(clientOrder));
        });
    }

    /**
     * Протестировать получение {limit} самых популярных товаров среди клиентов методом
     * GET /rest/products/popular?limit=
     */
    @SuppressWarnings("unchecked")
    @Test
    void testGetPopularProducts() {
        String requestPath = "/rest/products/popular";

        // Выполняем запрос, limit=3
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .queryParam(PARAM_LIMIT, 3)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 3 товара
        Assertions.assertEquals(3, response.size());

        // Проверяем, что ответ содержит правильные товары в правильном порядке (с наибольшим количеством в заказах в
        // порядке убывания количества)
        Assertions.assertEquals(response.get(0).get(PRODUCT_NAME_KEY), product3.get(PRODUCT_NAME_KEY));
        Assertions.assertEquals(response.get(1).get(PRODUCT_NAME_KEY), product2.get(PRODUCT_NAME_KEY));
        Assertions.assertEquals(response.get(2).get(PRODUCT_NAME_KEY), product1.get(PRODUCT_NAME_KEY));

        // Выполняем запрос, limit=1
        //@formatter:off
        response = given()
                .contentType(ContentType.JSON)
                .queryParam(PARAM_LIMIT, 1)
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 1 товар
        Assertions.assertEquals(1, response.size());

        // Проверяем, что ответ содержит самый популярный товар
        Assertions.assertEquals(response.get(0).get(PRODUCT_NAME_KEY), product3.get(PRODUCT_NAME_KEY));
    }

    /**
     * Протестировать получение товаров по идентификатору категории методом
     * GET /rest/products/search?categoryId=
     */
    @SuppressWarnings("unchecked")
    @Test
    void testSearchProductsByCategoryId() {
        String requestPath = "/rest/products/search?categoryId=" + getIdFromPath((String) childCategory.get(SELF_KEY));

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
        Assertions.assertEquals(3, response.size());

        // Проверяем, что у товаров ответа корректная категория
        response.forEach(product ->
        {
            Assertions.assertEquals(
                    getIdFromPath((String) childCategory.get(SELF_KEY)),
                    ((Map<String, Object>) product.get(PRODUCT_CATEGORY_KEY)).get(ID_KEY)
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
            product.put(ID_KEY, getIdFromPath((String) product.get(SELF_KEY)));
            product.remove(SELF_KEY);
            product.remove(PRODUCT_KEY);
            product.remove(PRODUCT_CATEGORY_KEY);
            Assertions.assertTrue(response.contains(product));
        });
    }
}
