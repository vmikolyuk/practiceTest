package ru.naumen.practiceTest.task2;


import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.http.ContentType;

/**
 * Тестирование практического задания 2
 * @author aspirkin
 * @since 13.05.2022
 */
public class TestPracticeTask2
{
    private static final String SELF_KEY = "self";

    /**
     * Протестировать CRUD операции REST-репозитория категории
     */
    @Test
    public void testCategoryRepositoryCRUD()
    {
        final String nameKey = "name";
        final String parentKey = "parent";
        final String categoriesPath = "categories";

        // Создаём категорию 1
        Map<String, Object> category1 = new HashMap<>();
        category1.put(nameKey, "testCategory1");
        category1.put(parentKey, null);
        Map<String, Object> postResponse1 = postEntity(category1, categoriesPath);

        // Проверяем поля и сохраняем ссылку на категорию 1
        Assert.assertEquals(category1.get(nameKey), postResponse1.get(nameKey));
        Assert.assertNotNull(postResponse1.get(parentKey));
        String category1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(category1Link);

        // Обновляем категорию 1
        category1.put(nameKey, "testCategory1Updated");
        Map<String, Object> putResponse = putEntity(category1, category1Link);

        // Проверяем поля
        Assert.assertEquals(category1.get(nameKey), putResponse.get(nameKey));

        // Создаём категорию 2
        Map<String, Object> category2 = new HashMap<>();
        category2.put(nameKey, "testCategory2");
        category2.put(parentKey, null);
        Map<String, Object> postResponse2 = postEntity(category2, categoriesPath);

        // Проверяем поля и сохраняем ссылку на категорию 2
        Assert.assertNotNull(postResponse2.get(parentKey));
        String category2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(category2Link);

        // Проверяем, что есть 2 категории
        Assert.assertEquals(2, getEntities(categoriesPath).size());

        // Удаляем категории
        deleteEntity(category1Link);
        deleteEntity(category2Link);

        // Проверяем, что категорий нет
        Assert.assertEquals(0, getEntities(categoriesPath).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория товара
     */
    @Test
    public void testProductRepositoryCRUD()
    {
        final String nameKey = "name";
        final String categoryKey = "category";
        final String descriptionKey = "description";
        final String priceKey = "price";
        final String productsPath = "products";

        // Создаём товар 1
        Map<String, Object> product1 = new HashMap<>();
        product1.put(nameKey, "testProduct1");
        product1.put(categoryKey, null);
        product1.put(descriptionKey, "test product 1 description");
        product1.put(priceKey, 3.50);
        Map<String, Object> postResponse1 = postEntity(product1, productsPath);

        // Проверяем поля и сохраняем ссылку на товар 1
        Assert.assertEquals(product1.get(nameKey), postResponse1.get(nameKey));
        Assert.assertEquals(product1.get(descriptionKey), postResponse1.get(descriptionKey));
        Assert.assertEquals(product1.get(priceKey), postResponse1.get(priceKey));
        Assert.assertNotNull(postResponse1.get(categoryKey));
        String product1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(product1Link);

        // Обновляем товар 1
        product1.put(nameKey, "testProduct1Updated");
        product1.put(descriptionKey, "test product 1 description updated");
        product1.put(priceKey, 13.50);
        Map<String, Object> putResponse = putEntity(product1, product1Link);

        // Проверяем поля
        Assert.assertEquals(product1.get(nameKey), putResponse.get(nameKey));
        Assert.assertEquals(product1.get(descriptionKey), putResponse.get(descriptionKey));
        Assert.assertEquals(product1.get(priceKey), putResponse.get(priceKey));

        // Создаём товар 2
        Map<String, Object> product2 = new HashMap<>();
        product2.put(nameKey, "testProduct2");
        product2.put(categoryKey, null);
        product2.put(descriptionKey, "test product 2 description");
        product2.put(priceKey, 4.50);
        Map<String, Object> postResponse2 = postEntity(product2, productsPath);

        // Проверяем поля и сохраняем ссылку на товар 2
        Assert.assertEquals(product2.get(nameKey), postResponse2.get(nameKey));
        Assert.assertEquals(product2.get(descriptionKey), postResponse2.get(descriptionKey));
        Assert.assertEquals(product2.get(priceKey), postResponse2.get(priceKey));
        Assert.assertNotNull(postResponse2.get(categoryKey));
        String product2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(product2Link);

        // Проверяем, что есть 2 товара
        Assert.assertEquals(2, getEntities(productsPath).size());

        // Удаляем товары
        deleteEntity(product1Link);
        deleteEntity(product2Link);

        // Проверяем, что товаров нет
        Assert.assertEquals(0, getEntities(productsPath).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория клиента
     */
    @Test
    public void testClientRepositoryCRUD()
    {
        final String fullNameKey = "fullName";
        final String addressKey = "address";
        final String phoneNumberKey = "phoneNumber";
        final String externalIdKey = "externalId";
        final String clientsPath = "clients";

        // Создаём клиента 1
        Map<String, Object> client1 = new HashMap<>();
        client1.put(fullNameKey, "client1 FullName");
        client1.put(addressKey, "client 1 address");
        client1.put(phoneNumberKey, "PhoneNumber1");
        client1.put(externalIdKey, 1L);
        Map<String, Object> postResponse1 = postEntity(client1, clientsPath);

        // Проверяем поля и сохраняем ссылку на клиента 1
        Assert.assertEquals(client1.get(fullNameKey), postResponse1.get(fullNameKey));
        Assert.assertEquals(client1.get(addressKey), postResponse1.get(addressKey));
        Assert.assertEquals(client1.get(phoneNumberKey), postResponse1.get(phoneNumberKey));
        Assert.assertEquals(client1.get(externalIdKey), ((Number)postResponse1.get(externalIdKey)).longValue());
        String client1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(client1Link);

        // Обновляем клиента 1
        client1.put(fullNameKey, "client1 FullName updated");
        client1.put(addressKey, "client 1 address updated");
        client1.put(phoneNumberKey, "PhoneNumber1u");
        client1.put(externalIdKey, 11L);
        Map<String, Object> putResponse = putEntity(client1, client1Link);

        // Проверяем поля
        Assert.assertEquals(client1.get(fullNameKey), putResponse.get(fullNameKey));
        Assert.assertEquals(client1.get(addressKey), putResponse.get(addressKey));
        Assert.assertEquals(client1.get(phoneNumberKey), putResponse.get(phoneNumberKey));
        Assert.assertEquals(client1.get(externalIdKey), ((Number)putResponse.get(externalIdKey)).longValue());

        // Создаём клиента 2
        Map<String, Object> client2 = new HashMap<>();
        client2.put(fullNameKey, "client2 FullName");
        client2.put(addressKey, "client 2 address");
        client2.put(phoneNumberKey, "PhoneNumber2");
        client2.put(externalIdKey, 2L);
        Map<String, Object> postResponse2 = postEntity(client2, clientsPath);

        // Проверяем поля и сохраняем ссылку на клиента 2
        Assert.assertEquals(client2.get(fullNameKey), postResponse2.get(fullNameKey));
        Assert.assertEquals(client2.get(addressKey), postResponse2.get(addressKey));
        Assert.assertEquals(client2.get(phoneNumberKey), postResponse2.get(phoneNumberKey));
        Assert.assertEquals(client2.get(externalIdKey), ((Number)postResponse2.get(externalIdKey)).longValue());
        String client2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(client2Link);

        // Проверяем, что есть 2 клиента
        Assert.assertEquals(2, getEntities(clientsPath).size());

        // Удаляем клиентов
        deleteEntity(client1Link);
        deleteEntity(client2Link);

        // Проверяем, что клиентов нет
        Assert.assertEquals(0, getEntities(clientsPath).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория заказа клиента
     */
    @Test
    public void testClientOrderRepositoryCRUD()
    {
        final String clientKey = "client";
        final String statusKey = "status";
        final String totalKey = "total";
        final String clientOrdersPath = "clientOrders";

        // Создаём заказ клиента 1
        Map<String, Object> clientOrder1 = new HashMap<>();
        clientOrder1.put(clientKey, null);
        clientOrder1.put(statusKey, 1);
        clientOrder1.put(totalKey, 1.1);
        Map<String, Object> postResponse1 = postEntity(clientOrder1, clientOrdersPath);

        // Проверяем поля и сохраняем ссылку на заказ клиента 1
        Assert.assertEquals(clientOrder1.get(statusKey), postResponse1.get(statusKey));
        Assert.assertEquals(clientOrder1.get(totalKey), postResponse1.get(totalKey));
        Assert.assertNotNull(postResponse1.get(clientKey));
        String clientOrder1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(clientOrder1Link);

        // Обновляем заказ клиента 1
        clientOrder1.put(statusKey, 2);
        clientOrder1.put(totalKey, 1.2);
        Map<String, Object> putResponse = putEntity(clientOrder1, clientOrder1Link);

        // Проверяем поля
        Assert.assertEquals(clientOrder1.get(statusKey), putResponse.get(statusKey));
        Assert.assertEquals(clientOrder1.get(totalKey), putResponse.get(totalKey));

        // Создаём заказ клиента 2
        Map<String, Object> clientOrder2 = new HashMap<>();
        clientOrder2.put(clientKey, null);
        clientOrder2.put(statusKey, 3);
        clientOrder2.put(totalKey, 2.1);
        Map<String, Object> postResponse2 = postEntity(clientOrder2, clientOrdersPath);

        // Проверяем поля и сохраняем ссылку на заказ клиента 2
        Assert.assertEquals(clientOrder2.get(statusKey), postResponse2.get(statusKey));
        Assert.assertEquals(clientOrder2.get(totalKey), postResponse2.get(totalKey));
        Assert.assertNotNull(postResponse2.get(clientKey));
        String clientOrder2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(clientOrder2Link);

        // Проверяем, что есть 2 заказа клиентов
        Assert.assertEquals(2, getEntities(clientOrdersPath).size());

        // Удаляем заказы клиентов
        deleteEntity(clientOrder1Link);
        deleteEntity(clientOrder2Link);

        // Проверяем, что заказов клиентов нет
        Assert.assertEquals(0, getEntities(clientOrdersPath).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория заказа-товара
     */
    @Test
    public void testOrderProductRepositoryCRUD()
    {
        final String productKey = "product";
        final String clientOrderKey = "clientOrder";
        final String countProductKey = "countProduct";
        final String orderProductsPath = "orderProducts";

        // Создаём заказ-товар 1
        Map<String, Object> orderProduct1 = new HashMap<>();
        orderProduct1.put(productKey, null);
        orderProduct1.put(clientOrderKey, null);
        orderProduct1.put(countProductKey, 10);
        Map<String, Object> postResponse1 = postEntity(orderProduct1, orderProductsPath);

        // Проверяем поля и сохраняем ссылку на заказ-товар 1
        Assert.assertEquals(orderProduct1.get(countProductKey), postResponse1.get(countProductKey));
        Assert.assertNotNull(postResponse1.get(productKey));
        Assert.assertNotNull(postResponse1.get(clientOrderKey));
        String orderProduct1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(orderProduct1Link);

        // Обновляем заказ-товар 1
        orderProduct1.put(countProductKey, 11);
        Map<String, Object> putResponse = putEntity(orderProduct1, orderProduct1Link);

        // Проверяем поля
        Assert.assertEquals(orderProduct1.get(countProductKey), putResponse.get(countProductKey));

        // Создаём заказ-товар 2
        Map<String, Object> orderProduct2 = new HashMap<>();
        orderProduct2.put(productKey, null);
        orderProduct2.put(clientOrderKey, null);
        orderProduct2.put(countProductKey, 20);
        Map<String, Object> postResponse2 = postEntity(orderProduct2, orderProductsPath);

        // Проверяем поля и сохраняем ссылку на заказ-товар 2
        Assert.assertEquals(orderProduct2.get(countProductKey), postResponse2.get(countProductKey));
        Assert.assertNotNull(postResponse2.get(productKey));
        Assert.assertNotNull(postResponse2.get(clientOrderKey));
        String orderProduct2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(orderProduct2Link);

        // Проверяем, что есть 2 заказа-товара
        Assert.assertEquals(2, getEntities(orderProductsPath).size());

        // Удаляем заказы-товары
        deleteEntity(orderProduct1Link);
        deleteEntity(orderProduct2Link);

        // Проверяем, что заказов-товаров нет
        Assert.assertEquals(0, getEntities(orderProductsPath).size());
    }

    /**
     * Выполнить DELETE REST-запрос по передаваемому пути для удаления в системе объекта
     * @param path путь
     */
    private static void deleteEntity(String path)
    {
        //@formatter:off
        given()
            .contentType(ContentType.JSON)
            .expect()
            .statusCode(204)
            .when()
            .delete(path);
        //@formatter:on
    }

    /**
     * Выполнить PUT REST-запрос по передаваемому пути для обновления в системе передаваемого объекта
     * @param entity объект
     * @param path путь
     * @return обновлённый объект в виде {@link Map}
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> putEntity(Object entity, String path)
    {
        //@formatter:off
        return flattenResponseLinks(
            given()
                .contentType(ContentType.JSON)
                .body(entity)
                .expect()
                .statusCode(200)
                .when()
                .put(path)
                .then()
                .extract().body().as(Map.class)
        );
        //@formatter:on
    }

    /**
     * Выполнить POST REST-запрос по передаваемому пути для создания в системе передаваемого объекта
     * @param entity объект
     * @param path путь
     * @return созданный объект в виде {@link Map}
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> postEntity(Object entity, String path)
    {
        //@formatter:off
        return flattenResponseLinks(
            given()
                .contentType(ContentType.JSON)
                .body(entity)
                .expect()
                .statusCode(201)
                .when()
                .post("/" + path)
                .then()
                .extract().body().as(Map.class)
        );
        //@formatter:on
    }

    /**
     * Выполнить GET REST-запрос по передаваемому пути для получения существующих в системе объектов
     * @param path путь
     * @return список объектов
     */
    private static List<Map<String, Object>> getEntities(String path)
    {
        //@formatter:off
        return given()
            .contentType(ContentType.JSON)
            .expect()
            .statusCode(200)
            .when()
            .get("/" + path)
            .then()
            .extract().body().path("_embedded." + path);
        //@formatter:on
    }

    /**
     * Перенести содержимое ссылок (поле _links) на верхний уровень обрабатываемого ответа для удобства обращения к
     * ним в тестах
     * @param response обрабатываемый ответ
     * @return обработанный ответ
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> flattenResponseLinks(Map<String, Object> response)
    {
        Map<String, Object> linksMap = (Map<String, Object>)response.get("_links");
        linksMap.forEach((key, value) -> response.put(key, ((Map<String, String>)value).get("href")));
        response.remove("_links");
        return response;
    }
}