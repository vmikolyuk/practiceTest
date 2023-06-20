package ru.naumen.practiceTest.task3;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static io.restassured.RestAssured.given;

/**
 * Тестирование практического задания 3
 * @author vmikolyuk
 * @since 20.04.2022
 */
public class TestOptionalTask3 extends Task3TestBase
{
    /**
     * Протестировать получение товаров по подстроке названия
     * GET /rest/products/search?name=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSearchProductsByName()
    {
        //todo параметризовать
        String requestPath = "/rest/products/search";

        // Выполняем запрос
        //@formatter:off
        List<Map<String, Object>> response1 = given()
                .contentType(ContentType.JSON)
                .queryParam("name", "test")
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 1 товар
        Assert.assertEquals(1, response1.size());

        // Проверяем, что у товаров ответа корректная категория
        response1.forEach(product ->
        {
            Assert.assertEquals(
                    getIdFromPath((String)childCategory.get(SELF_KEY)),
                    ((Map<String, Object>)product.get(PRODUCT_CATEGORY_KEY)).get(ID_KEY)
            );
            product.remove(PRODUCT_CATEGORY_KEY);
        });

        // Создаём временные копии товаров для сравнения с товарами ответа
        List<Map<String, Object>> productsToCompare1 = new ArrayList<>();
        productsToCompare1.add(new HashMap<>(product1));

        // Подготавливаем копии товаров (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        productsToCompare1.forEach(product ->
        {
            product.put(ID_KEY, getIdFromPath((String)product.get(SELF_KEY)));
            product.remove(SELF_KEY);
            product.remove(PRODUCT_KEY);
            product.remove(PRODUCT_CATEGORY_KEY);
            Assert.assertTrue(response1.contains(product));
        });

        // Выполняем запрос (поиск по sample)
        //@formatter:off
        List<Map<String, Object>> response2 = given()
                .contentType(ContentType.JSON)
                .queryParam("name", "sOmE")
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 3 товара
        Assert.assertEquals(2, response2.size());

        // Проверяем, что у товаров ответа корректная категория
        response2.forEach(product ->
        {
            Assert.assertEquals(
                    getIdFromPath((String)childCategory.get(SELF_KEY)),
                    ((Map<String, Object>)product.get(PRODUCT_CATEGORY_KEY)).get(ID_KEY)
            );
            product.remove(PRODUCT_CATEGORY_KEY);
        });

        // Создаём временные копии товаров для сравнения с товарами ответа
        List<Map<String, Object>> productsToCompare2 = new ArrayList<>();
        productsToCompare2.add(new HashMap<>(product2));
        productsToCompare2.add(new HashMap<>(product3));

        // Подготавливаем копии товаров (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        productsToCompare2.forEach(product ->
        {
            product.put(ID_KEY, getIdFromPath((String)product.get(SELF_KEY)));
            product.remove(SELF_KEY);
            product.remove(PRODUCT_KEY);
            product.remove(PRODUCT_CATEGORY_KEY);
            Assert.assertTrue(response2.contains(product));
        });
    }

    /**
     * Протестировать получение клиентов по подстроке имени
     * GET /rest/clients/search?name=
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSearchClientsByName()
    {
        String requestPath = "/rest/clients/search";

        // Выполняем запрос
        //@formatter:off
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .queryParam("name", "full")
            .expect()
                .statusCode(200)
            .when()
                .get(requestPath)
            .then()
                .extract().body().as(List.class);
        //@formatter:on

        // Проверяем, что в ответе содержится 2 клиента
        Assert.assertEquals(2, response.size());

        // Создаём временные копии клиентов для сравнения с клиентами ответа
        List<Map<String, Object>> clientsToCompare = new ArrayList<>();
        clientsToCompare.add(new HashMap<>(client1));
        clientsToCompare.add(new HashMap<>(client3));

        // Подготавливаем копии клиентов (удаляем специфичные для RepositoryRestResource ссылочные атрибуты) и проверяем
        // их наличие в ответе
        clientsToCompare.forEach(client ->
        {
            client.put(ID_KEY, getIdFromPath((String)client.get(SELF_KEY)));
            client.remove(SELF_KEY);
            client.remove(CLIENT_CLIENT_KEY);
            Assert.assertTrue(response.contains(client));
        });
    }
}
