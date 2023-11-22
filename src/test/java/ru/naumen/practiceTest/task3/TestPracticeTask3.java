package ru.naumen.practiceTest.task3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.naumen.practiceTest.RestTestBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Тестирование практического задания 3
 *
 * @author aspirkin
 * @since 13.05.2022
 */
class TestPracticeTask3 extends RestTestBase {
    /**
     * Протестировать CRUD операции REST-репозитория категории
     */
    @Test
    void testCategoryRepositoryCRUD() {
        // Создаём категорию 1
        Map<String, Object> category1 = new HashMap<>();
        category1.put(CATEGORY_NAME_KEY, "testCategory1");
        category1.put(CATEGORY_PARENT_KEY, null);
        Map<String, Object> postResponse1 = postEntity(category1, CATEGORIES_PATH);

        // Проверяем поля и сохраняем ссылку на категорию 1
        Assertions.assertEquals(category1.get(CATEGORY_NAME_KEY), postResponse1.get(CATEGORY_NAME_KEY));
        Assertions.assertNotNull(postResponse1.get(CATEGORY_PARENT_KEY));
        String category1Link = (String) postResponse1.get(SELF_KEY);
        Assertions.assertNotNull(category1Link);

        // Обновляем категорию 1
        category1.put(CATEGORY_NAME_KEY, "testCategory1Updated");
        Map<String, Object> putResponse = putEntity(category1, category1Link);

        // Проверяем поля
        Assertions.assertEquals(category1.get(CATEGORY_NAME_KEY), putResponse.get(CATEGORY_NAME_KEY));

        // Создаём категорию 2
        Map<String, Object> category2 = new HashMap<>();
        category2.put(CATEGORY_NAME_KEY, "testCategory2");
        category2.put(CATEGORY_PARENT_KEY, null);
        Map<String, Object> postResponse2 = postEntity(category2, CATEGORIES_PATH);

        // Проверяем поля и сохраняем ссылку на категорию 2
        Assertions.assertNotNull(postResponse2.get(CATEGORY_PARENT_KEY));
        String category2Link = (String) postResponse2.get(SELF_KEY);
        Assertions.assertNotNull(category2Link);

        // Проверяем, что есть 2 категории
        Assertions.assertEquals(2, getEntities(CATEGORIES_PATH).size());

        // Удаляем категории
        deleteEntity(category1Link);
        deleteEntity(category2Link);

        // Проверяем, что категорий нет
        Assertions.assertEquals(0, getEntities(CATEGORIES_PATH).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория товара
     */
    @Test
    void testProductRepositoryCRUD() {
        // Создаём категорию для товаров
        Map<String, Object> category = new HashMap<>();
        category.put(CATEGORY_NAME_KEY, "productCategory");
        category.put(CATEGORY_PARENT_KEY, null);
        category = postEntity(category, CATEGORIES_PATH);
        String categoryLink = (String) category.get("self");


        // Создаём товар 1
        Map<String, Object> product1 = new HashMap<>();
        product1.put(PRODUCT_NAME_KEY, "testProduct1");
        product1.put(PRODUCT_CATEGORY_KEY, categoryLink);
        product1.put(PRODUCT_DESCRIPTION_KEY, "test product 1 description");
        product1.put(PRODUCT_PRICE_KEY, 3.50);
        Map<String, Object> postResponse1 = postEntity(product1, PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на товар 1
        Assertions.assertEquals(product1.get(PRODUCT_NAME_KEY), postResponse1.get(PRODUCT_NAME_KEY));
        Assertions.assertEquals(product1.get(PRODUCT_DESCRIPTION_KEY), postResponse1.get(PRODUCT_DESCRIPTION_KEY));
        Assertions.assertEquals(product1.get(PRODUCT_PRICE_KEY), postResponse1.get(PRODUCT_PRICE_KEY));
        Assertions.assertNotNull(postResponse1.get(PRODUCT_CATEGORY_KEY));
        String product1Link = (String) postResponse1.get(SELF_KEY);
        Assertions.assertNotNull(product1Link);

        // Обновляем товар 1
        product1.put(PRODUCT_NAME_KEY, "testProduct1Updated");
        product1.put(PRODUCT_DESCRIPTION_KEY, "test product 1 description updated");
        product1.put(PRODUCT_PRICE_KEY, 13.50);
        Map<String, Object> putResponse = putEntity(product1, product1Link);

        // Проверяем поля
        Assertions.assertEquals(product1.get(PRODUCT_NAME_KEY), putResponse.get(PRODUCT_NAME_KEY));
        Assertions.assertEquals(product1.get(PRODUCT_DESCRIPTION_KEY), putResponse.get(PRODUCT_DESCRIPTION_KEY));
        Assertions.assertEquals(product1.get(PRODUCT_PRICE_KEY), putResponse.get(PRODUCT_PRICE_KEY));

        // Создаём товар 2
        Map<String, Object> product2 = new HashMap<>();
        product2.put(PRODUCT_NAME_KEY, "testProduct2");
        product2.put(PRODUCT_CATEGORY_KEY, categoryLink);
        product2.put(PRODUCT_DESCRIPTION_KEY, "test product 2 description");
        product2.put(PRODUCT_PRICE_KEY, 4.50);
        Map<String, Object> postResponse2 = postEntity(product2, PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на товар 2
        Assertions.assertEquals(product2.get(PRODUCT_NAME_KEY), postResponse2.get(PRODUCT_NAME_KEY));
        Assertions.assertEquals(product2.get(PRODUCT_DESCRIPTION_KEY), postResponse2.get(PRODUCT_DESCRIPTION_KEY));
        Assertions.assertEquals(product2.get(PRODUCT_PRICE_KEY), postResponse2.get(PRODUCT_PRICE_KEY));
        Assertions.assertNotNull(postResponse2.get(PRODUCT_CATEGORY_KEY));
        String product2Link = (String) postResponse2.get(SELF_KEY);
        Assertions.assertNotNull(product2Link);

        // Проверяем, что есть 2 товара
        Assertions.assertEquals(2, getEntities(PRODUCTS_PATH).size());

        // Удаляем товары
        deleteEntity(product1Link);
        deleteEntity(product2Link);

        // Проверяем, что товаров нет
        Assertions.assertEquals(0, getEntities(PRODUCTS_PATH).size());

        deleteEntity(categoryLink);
    }

    /**
     * Протестировать CRUD операции REST-репозитория клиента
     */
    @Test
    void testClientRepositoryCRUD() {
        // Создаём клиента 1
        Map<String, Object> client1 = new HashMap<>();
        client1.put(CLIENT_FULL_NAME_KEY, "client1 FullName");
        client1.put(CLIENT_ADDRESS_KEY, "client 1 address");
        client1.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber1");
        client1.put(CLIENT_EXTERNAL_ID_KEY, 1L);
        Map<String, Object> postResponse1 = postEntity(client1, CLIENTS_PATH);

        // Проверяем поля и сохраняем ссылку на клиента 1
        Assertions.assertEquals(client1.get(CLIENT_FULL_NAME_KEY), postResponse1.get(CLIENT_FULL_NAME_KEY));
        Assertions.assertEquals(client1.get(CLIENT_ADDRESS_KEY), postResponse1.get(CLIENT_ADDRESS_KEY));
        Assertions.assertEquals(client1.get(CLIENT_PHONE_NUMBER_KEY), postResponse1.get(CLIENT_PHONE_NUMBER_KEY));
        Assertions.assertEquals(client1.get(CLIENT_EXTERNAL_ID_KEY),
                ((Number) postResponse1.get(CLIENT_EXTERNAL_ID_KEY)).longValue());
        String client1Link = (String) postResponse1.get(SELF_KEY);
        Assertions.assertNotNull(client1Link);

        // Обновляем клиента 1
        client1.put(CLIENT_FULL_NAME_KEY, "client1 FullName updated");
        client1.put(CLIENT_ADDRESS_KEY, "client 1 address updated");
        client1.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber1u");
        client1.put(CLIENT_EXTERNAL_ID_KEY, 11L);
        Map<String, Object> putResponse = putEntity(client1, client1Link);

        // Проверяем поля
        Assertions.assertEquals(client1.get(CLIENT_FULL_NAME_KEY), putResponse.get(CLIENT_FULL_NAME_KEY));
        Assertions.assertEquals(client1.get(CLIENT_ADDRESS_KEY), putResponse.get(CLIENT_ADDRESS_KEY));
        Assertions.assertEquals(client1.get(CLIENT_PHONE_NUMBER_KEY), putResponse.get(CLIENT_PHONE_NUMBER_KEY));
        Assertions.assertEquals(client1.get(CLIENT_EXTERNAL_ID_KEY),
                ((Number) putResponse.get(CLIENT_EXTERNAL_ID_KEY)).longValue());

        // Создаём клиента 2
        Map<String, Object> client2 = new HashMap<>();
        client2.put(CLIENT_FULL_NAME_KEY, "client2 FullName");
        client2.put(CLIENT_ADDRESS_KEY, "client 2 address");
        client2.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber2");
        client2.put(CLIENT_EXTERNAL_ID_KEY, 2L);
        Map<String, Object> postResponse2 = postEntity(client2, CLIENTS_PATH);

        // Проверяем поля и сохраняем ссылку на клиента 2
        Assertions.assertEquals(client2.get(CLIENT_FULL_NAME_KEY), postResponse2.get(CLIENT_FULL_NAME_KEY));
        Assertions.assertEquals(client2.get(CLIENT_ADDRESS_KEY), postResponse2.get(CLIENT_ADDRESS_KEY));
        Assertions.assertEquals(client2.get(CLIENT_PHONE_NUMBER_KEY), postResponse2.get(CLIENT_PHONE_NUMBER_KEY));
        Assertions.assertEquals(client2.get(CLIENT_EXTERNAL_ID_KEY),
                ((Number) postResponse2.get(CLIENT_EXTERNAL_ID_KEY)).longValue());
        String client2Link = (String) postResponse2.get(SELF_KEY);
        Assertions.assertNotNull(client2Link);

        // Проверяем, что есть 2 клиента
        Assertions.assertEquals(2, getEntities(CLIENTS_PATH).size());

        // Удаляем клиентов
        deleteEntity(client1Link);
        deleteEntity(client2Link);

        // Проверяем, что клиентов нет
        Assertions.assertEquals(0, getEntities(CLIENTS_PATH).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория заказа клиента
     */
    @Test
    void testClientOrderRepositoryCRUD() {
        // Создаём клиента для заказов
        Map<String, Object> client = new HashMap<>();
        client.put(CLIENT_FULL_NAME_KEY, "client1 FullName");
        client.put(CLIENT_ADDRESS_KEY, "client 1 address");
        client.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber1");
        client.put(CLIENT_EXTERNAL_ID_KEY, 91L);
        client = postEntity(client, CLIENTS_PATH);
        String clientLink = (String) client.get("self");

        // Создаём заказ клиента 1
        Map<String, Object> clientOrder1 = new HashMap<>();
        clientOrder1.put(CLIENT_ORDER_CLIENT_KEY, clientLink);
        clientOrder1.put(CLIENT_ORDER_STATUS_KEY, 1);
        clientOrder1.put(CLIENT_ORDER_TOTAL_KEY, 1.1);
        Map<String, Object> postResponse1 = postEntity(clientOrder1, CLIENT_ORDERS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ клиента 1
        Assertions.assertEquals(clientOrder1.get(CLIENT_ORDER_STATUS_KEY), postResponse1.get(CLIENT_ORDER_STATUS_KEY));
        Assertions.assertEquals(clientOrder1.get(CLIENT_ORDER_TOTAL_KEY), postResponse1.get(CLIENT_ORDER_TOTAL_KEY));
        Assertions.assertNotNull(postResponse1.get(CLIENT_ORDER_CLIENT_KEY));
        String clientOrder1Link = (String) postResponse1.get(SELF_KEY);
        Assertions.assertNotNull(clientOrder1Link);

        // Обновляем заказ клиента 1
        clientOrder1.put(CLIENT_ORDER_STATUS_KEY, 2);
        clientOrder1.put(CLIENT_ORDER_TOTAL_KEY, 1.2);
        Map<String, Object> putResponse = putEntity(clientOrder1, clientOrder1Link);

        // Проверяем поля
        Assertions.assertEquals(clientOrder1.get(CLIENT_ORDER_STATUS_KEY), putResponse.get(CLIENT_ORDER_STATUS_KEY));
        Assertions.assertEquals(clientOrder1.get(CLIENT_ORDER_TOTAL_KEY), putResponse.get(CLIENT_ORDER_TOTAL_KEY));

        // Создаём заказ клиента 2
        Map<String, Object> clientOrder2 = new HashMap<>();
        clientOrder2.put(CLIENT_ORDER_CLIENT_KEY, clientLink);
        clientOrder2.put(CLIENT_ORDER_STATUS_KEY, 3);
        clientOrder2.put(CLIENT_ORDER_TOTAL_KEY, 2.1);
        Map<String, Object> postResponse2 = postEntity(clientOrder2, CLIENT_ORDERS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ клиента 2
        Assertions.assertEquals(clientOrder2.get(CLIENT_ORDER_STATUS_KEY), postResponse2.get(CLIENT_ORDER_STATUS_KEY));
        Assertions.assertEquals(clientOrder2.get(CLIENT_ORDER_TOTAL_KEY), postResponse2.get(CLIENT_ORDER_TOTAL_KEY));
        Assertions.assertNotNull(postResponse2.get(CLIENT_ORDER_CLIENT_KEY));
        String clientOrder2Link = (String) postResponse2.get(SELF_KEY);
        Assertions.assertNotNull(clientOrder2Link);

        // Проверяем, что есть 2 заказа клиентов
        Assertions.assertEquals(2, getEntities(CLIENT_ORDERS_PATH).size());

        // Удаляем заказы клиентов
        deleteEntity(clientOrder1Link);
        deleteEntity(clientOrder2Link);

        // Проверяем, что заказов клиентов нет
        Assertions.assertEquals(0, getEntities(CLIENT_ORDERS_PATH).size());

        deleteEntity(clientLink);
    }

    /**
     * Протестировать CRUD операции REST-репозитория заказа-товара
     */
    @Test
    void testOrderProductRepositoryCRUD() {
        // Создаем категорию, товар, клиента и заказ клиента
        Map<String, Object> category = new HashMap<>();
        category.put(CATEGORY_NAME_KEY, "testCategory1");
        category.put(CATEGORY_PARENT_KEY, null);
        String categoryLink = (String) postEntity(category, CATEGORIES_PATH).get("self");

        Map<String, Object> product = new HashMap<>();
        product.put(PRODUCT_NAME_KEY, "testProduct1");
        product.put(PRODUCT_CATEGORY_KEY, categoryLink);
        product.put(PRODUCT_DESCRIPTION_KEY, "test product 1 description");
        product.put(PRODUCT_PRICE_KEY, 3.50);
        String productLink = (String) postEntity(product, PRODUCTS_PATH).get("self");

        Map<String, Object> client = new HashMap<>();
        client.put(CLIENT_FULL_NAME_KEY, "client1 FullName");
        client.put(CLIENT_ADDRESS_KEY, "client 1 address");
        client.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber1");
        client.put(CLIENT_EXTERNAL_ID_KEY, 91L);
        String clientLink = (String) postEntity(client, CLIENTS_PATH).get("self");

        Map<String, Object> clientOrder = new HashMap<>();
        clientOrder.put(CLIENT_ORDER_CLIENT_KEY, clientLink);
        clientOrder.put(CLIENT_ORDER_STATUS_KEY, 1);
        clientOrder.put(CLIENT_ORDER_TOTAL_KEY, 1.1);
        String clientOrderLink = (String) postEntity(clientOrder, CLIENT_ORDERS_PATH).get("self");

        // Создаём заказ-товар 1
        Map<String, Object> orderProduct1 = new HashMap<>();
        orderProduct1.put(ORDER_PRODUCT_PRODUCT_KEY, productLink);
        orderProduct1.put(ORDER_PRODUCT_CLIENT_ORDER_KEY, clientOrderLink);
        orderProduct1.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, 10);
        Map<String, Object> postResponse1 = postEntity(orderProduct1, ORDER_PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ-товар 1
        Assertions.assertEquals(orderProduct1.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY),
                postResponse1.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY));
        Assertions.assertNotNull(postResponse1.get(ORDER_PRODUCT_PRODUCT_KEY));
        Assertions.assertNotNull(postResponse1.get(ORDER_PRODUCT_CLIENT_ORDER_KEY));
        String orderProduct1Link = (String) postResponse1.get(SELF_KEY);
        Assertions.assertNotNull(orderProduct1Link);

        // Обновляем заказ-товар 1
        orderProduct1.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, 11);
        Map<String, Object> putResponse = putEntity(orderProduct1, orderProduct1Link);

        // Проверяем поля
        Assertions.assertEquals(orderProduct1.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY),
                putResponse.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY));

        // Создаём заказ-товар 2
        Map<String, Object> orderProduct2 = new HashMap<>();
        orderProduct2.put(ORDER_PRODUCT_PRODUCT_KEY, productLink);
        orderProduct2.put(ORDER_PRODUCT_CLIENT_ORDER_KEY, clientOrderLink);
        orderProduct2.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, 20);
        Map<String, Object> postResponse2 = postEntity(orderProduct2, ORDER_PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ-товар 2
        Assertions.assertEquals(orderProduct2.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY),
                postResponse2.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY));
        Assertions.assertNotNull(postResponse2.get(ORDER_PRODUCT_PRODUCT_KEY));
        Assertions.assertNotNull(postResponse2.get(ORDER_PRODUCT_CLIENT_ORDER_KEY));
        String orderProduct2Link = (String) postResponse2.get(SELF_KEY);
        Assertions.assertNotNull(orderProduct2Link);

        // Проверяем, что есть 2 заказа-товара
        Assertions.assertEquals(2, getEntities(ORDER_PRODUCTS_PATH).size());

        // Удаляем заказы-товары
        deleteEntity(orderProduct1Link);
        deleteEntity(orderProduct2Link);

        // Проверяем, что заказов-товаров нет
        Assertions.assertEquals(0, getEntities(ORDER_PRODUCTS_PATH).size());

        deleteEntity(clientOrderLink);
        deleteEntity(productLink);
        deleteEntity(clientLink);
        deleteEntity(categoryLink);
    }
}