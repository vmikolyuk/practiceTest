package ru.naumen.practiceTest.task2;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ru.naumen.practiceTest.RestTestBase;

/**
 * Тестирование практического задания 2
 * @author aspirkin
 * @since 13.05.2022
 */
public class TestPracticeTask2 extends RestTestBase
{
    /**
     * Протестировать CRUD операции REST-репозитория категории
     */
    @Test
    public void testCategoryRepositoryCRUD()
    {
        // Создаём категорию 1
        Map<String, Object> category1 = new HashMap<>();
        category1.put(CATEGORY_NAME_KEY, "testCategory1");
        category1.put(CATEGORY_PARENT_KEY, null);
        Map<String, Object> postResponse1 = postEntity(category1, CATEGORIES_PATH);

        // Проверяем поля и сохраняем ссылку на категорию 1
        Assert.assertEquals(category1.get(CATEGORY_NAME_KEY), postResponse1.get(CATEGORY_NAME_KEY));
        Assert.assertNotNull(postResponse1.get(CATEGORY_PARENT_KEY));
        String category1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(category1Link);

        // Обновляем категорию 1
        category1.put(CATEGORY_NAME_KEY, "testCategory1Updated");
        Map<String, Object> putResponse = putEntity(category1, category1Link);

        // Проверяем поля
        Assert.assertEquals(category1.get(CATEGORY_NAME_KEY), putResponse.get(CATEGORY_NAME_KEY));

        // Создаём категорию 2
        Map<String, Object> category2 = new HashMap<>();
        category2.put(CATEGORY_NAME_KEY, "testCategory2");
        category2.put(CATEGORY_PARENT_KEY, null);
        Map<String, Object> postResponse2 = postEntity(category2, CATEGORIES_PATH);

        // Проверяем поля и сохраняем ссылку на категорию 2
        Assert.assertNotNull(postResponse2.get(CATEGORY_PARENT_KEY));
        String category2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(category2Link);

        // Проверяем, что есть 2 категории
        Assert.assertEquals(2, getEntities(CATEGORIES_PATH).size());

        // Удаляем категории
        deleteEntity(category1Link);
        deleteEntity(category2Link);

        // Проверяем, что категорий нет
        Assert.assertEquals(0, getEntities(CATEGORIES_PATH).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория товара
     */
    @Test
    public void testProductRepositoryCRUD()
    {
        // Создаём товар 1
        Map<String, Object> product1 = new HashMap<>();
        product1.put(PRODUCT_NAME_KEY, "testProduct1");
        product1.put(PRODUCT_CATEGORY_KEY, null);
        product1.put(PRODUCT_DESCRIPTION_KEY, "test product 1 description");
        product1.put(PRODUCT_PRICE_KEY, 3.50);
        Map<String, Object> postResponse1 = postEntity(product1, PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на товар 1
        Assert.assertEquals(product1.get(PRODUCT_NAME_KEY), postResponse1.get(PRODUCT_NAME_KEY));
        Assert.assertEquals(product1.get(PRODUCT_DESCRIPTION_KEY), postResponse1.get(PRODUCT_DESCRIPTION_KEY));
        Assert.assertEquals(product1.get(PRODUCT_PRICE_KEY), postResponse1.get(PRODUCT_PRICE_KEY));
        Assert.assertNotNull(postResponse1.get(PRODUCT_CATEGORY_KEY));
        String product1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(product1Link);

        // Обновляем товар 1
        product1.put(PRODUCT_NAME_KEY, "testProduct1Updated");
        product1.put(PRODUCT_DESCRIPTION_KEY, "test product 1 description updated");
        product1.put(PRODUCT_PRICE_KEY, 13.50);
        Map<String, Object> putResponse = putEntity(product1, product1Link);

        // Проверяем поля
        Assert.assertEquals(product1.get(PRODUCT_NAME_KEY), putResponse.get(PRODUCT_NAME_KEY));
        Assert.assertEquals(product1.get(PRODUCT_DESCRIPTION_KEY), putResponse.get(PRODUCT_DESCRIPTION_KEY));
        Assert.assertEquals(product1.get(PRODUCT_PRICE_KEY), putResponse.get(PRODUCT_PRICE_KEY));

        // Создаём товар 2
        Map<String, Object> product2 = new HashMap<>();
        product2.put(PRODUCT_NAME_KEY, "testProduct2");
        product2.put(PRODUCT_CATEGORY_KEY, null);
        product2.put(PRODUCT_DESCRIPTION_KEY, "test product 2 description");
        product2.put(PRODUCT_PRICE_KEY, 4.50);
        Map<String, Object> postResponse2 = postEntity(product2, PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на товар 2
        Assert.assertEquals(product2.get(PRODUCT_NAME_KEY), postResponse2.get(PRODUCT_NAME_KEY));
        Assert.assertEquals(product2.get(PRODUCT_DESCRIPTION_KEY), postResponse2.get(PRODUCT_DESCRIPTION_KEY));
        Assert.assertEquals(product2.get(PRODUCT_PRICE_KEY), postResponse2.get(PRODUCT_PRICE_KEY));
        Assert.assertNotNull(postResponse2.get(PRODUCT_CATEGORY_KEY));
        String product2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(product2Link);

        // Проверяем, что есть 2 товара
        Assert.assertEquals(2, getEntities(PRODUCTS_PATH).size());

        // Удаляем товары
        deleteEntity(product1Link);
        deleteEntity(product2Link);

        // Проверяем, что товаров нет
        Assert.assertEquals(0, getEntities(PRODUCTS_PATH).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория клиента
     */
    @Test
    public void testClientRepositoryCRUD()
    {
        // Создаём клиента 1
        Map<String, Object> client1 = new HashMap<>();
        client1.put(CLIENT_FULL_NAME_KEY, "client1 FullName");
        client1.put(CLIENT_ADDRESS_KEY, "client 1 address");
        client1.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber1");
        client1.put(CLIENT_EXTERNAL_ID_KEY, 1L);
        Map<String, Object> postResponse1 = postEntity(client1, CLIENTS_PATH);

        // Проверяем поля и сохраняем ссылку на клиента 1
        Assert.assertEquals(client1.get(CLIENT_FULL_NAME_KEY), postResponse1.get(CLIENT_FULL_NAME_KEY));
        Assert.assertEquals(client1.get(CLIENT_ADDRESS_KEY), postResponse1.get(CLIENT_ADDRESS_KEY));
        Assert.assertEquals(client1.get(CLIENT_PHONE_NUMBER_KEY), postResponse1.get(CLIENT_PHONE_NUMBER_KEY));
        Assert.assertEquals(client1.get(CLIENT_EXTERNAL_ID_KEY),
                ((Number)postResponse1.get(CLIENT_EXTERNAL_ID_KEY)).longValue());
        String client1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(client1Link);

        // Обновляем клиента 1
        client1.put(CLIENT_FULL_NAME_KEY, "client1 FullName updated");
        client1.put(CLIENT_ADDRESS_KEY, "client 1 address updated");
        client1.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber1u");
        client1.put(CLIENT_EXTERNAL_ID_KEY, 11L);
        Map<String, Object> putResponse = putEntity(client1, client1Link);

        // Проверяем поля
        Assert.assertEquals(client1.get(CLIENT_FULL_NAME_KEY), putResponse.get(CLIENT_FULL_NAME_KEY));
        Assert.assertEquals(client1.get(CLIENT_ADDRESS_KEY), putResponse.get(CLIENT_ADDRESS_KEY));
        Assert.assertEquals(client1.get(CLIENT_PHONE_NUMBER_KEY), putResponse.get(CLIENT_PHONE_NUMBER_KEY));
        Assert.assertEquals(client1.get(CLIENT_EXTERNAL_ID_KEY),
                ((Number)putResponse.get(CLIENT_EXTERNAL_ID_KEY)).longValue());

        // Создаём клиента 2
        Map<String, Object> client2 = new HashMap<>();
        client2.put(CLIENT_FULL_NAME_KEY, "client2 FullName");
        client2.put(CLIENT_ADDRESS_KEY, "client 2 address");
        client2.put(CLIENT_PHONE_NUMBER_KEY, "PhoneNumber2");
        client2.put(CLIENT_EXTERNAL_ID_KEY, 2L);
        Map<String, Object> postResponse2 = postEntity(client2, CLIENTS_PATH);

        // Проверяем поля и сохраняем ссылку на клиента 2
        Assert.assertEquals(client2.get(CLIENT_FULL_NAME_KEY), postResponse2.get(CLIENT_FULL_NAME_KEY));
        Assert.assertEquals(client2.get(CLIENT_ADDRESS_KEY), postResponse2.get(CLIENT_ADDRESS_KEY));
        Assert.assertEquals(client2.get(CLIENT_PHONE_NUMBER_KEY), postResponse2.get(CLIENT_PHONE_NUMBER_KEY));
        Assert.assertEquals(client2.get(CLIENT_EXTERNAL_ID_KEY),
                ((Number)postResponse2.get(CLIENT_EXTERNAL_ID_KEY)).longValue());
        String client2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(client2Link);

        // Проверяем, что есть 2 клиента
        Assert.assertEquals(2, getEntities(CLIENTS_PATH).size());

        // Удаляем клиентов
        deleteEntity(client1Link);
        deleteEntity(client2Link);

        // Проверяем, что клиентов нет
        Assert.assertEquals(0, getEntities(CLIENTS_PATH).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория заказа клиента
     */
    @Test
    public void testClientOrderRepositoryCRUD()
    {
        // Создаём заказ клиента 1
        Map<String, Object> clientOrder1 = new HashMap<>();
        clientOrder1.put(CLIENT_ORDER_CLIENT_KEY, null);
        clientOrder1.put(CLIENT_ORDER_STATUS_KEY, 1);
        clientOrder1.put(CLIENT_ORDER_TOTAL_KEY, 1.1);
        Map<String, Object> postResponse1 = postEntity(clientOrder1, CLIENT_ORDERS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ клиента 1
        Assert.assertEquals(clientOrder1.get(CLIENT_ORDER_STATUS_KEY), postResponse1.get(CLIENT_ORDER_STATUS_KEY));
        Assert.assertEquals(clientOrder1.get(CLIENT_ORDER_TOTAL_KEY), postResponse1.get(CLIENT_ORDER_TOTAL_KEY));
        Assert.assertNotNull(postResponse1.get(CLIENT_ORDER_CLIENT_KEY));
        String clientOrder1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(clientOrder1Link);

        // Обновляем заказ клиента 1
        clientOrder1.put(CLIENT_ORDER_STATUS_KEY, 2);
        clientOrder1.put(CLIENT_ORDER_TOTAL_KEY, 1.2);
        Map<String, Object> putResponse = putEntity(clientOrder1, clientOrder1Link);

        // Проверяем поля
        Assert.assertEquals(clientOrder1.get(CLIENT_ORDER_STATUS_KEY), putResponse.get(CLIENT_ORDER_STATUS_KEY));
        Assert.assertEquals(clientOrder1.get(CLIENT_ORDER_TOTAL_KEY), putResponse.get(CLIENT_ORDER_TOTAL_KEY));

        // Создаём заказ клиента 2
        Map<String, Object> clientOrder2 = new HashMap<>();
        clientOrder2.put(CLIENT_ORDER_CLIENT_KEY, null);
        clientOrder2.put(CLIENT_ORDER_STATUS_KEY, 3);
        clientOrder2.put(CLIENT_ORDER_TOTAL_KEY, 2.1);
        Map<String, Object> postResponse2 = postEntity(clientOrder2, CLIENT_ORDERS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ клиента 2
        Assert.assertEquals(clientOrder2.get(CLIENT_ORDER_STATUS_KEY), postResponse2.get(CLIENT_ORDER_STATUS_KEY));
        Assert.assertEquals(clientOrder2.get(CLIENT_ORDER_TOTAL_KEY), postResponse2.get(CLIENT_ORDER_TOTAL_KEY));
        Assert.assertNotNull(postResponse2.get(CLIENT_ORDER_CLIENT_KEY));
        String clientOrder2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(clientOrder2Link);

        // Проверяем, что есть 2 заказа клиентов
        Assert.assertEquals(2, getEntities(CLIENT_ORDERS_PATH).size());

        // Удаляем заказы клиентов
        deleteEntity(clientOrder1Link);
        deleteEntity(clientOrder2Link);

        // Проверяем, что заказов клиентов нет
        Assert.assertEquals(0, getEntities(CLIENT_ORDERS_PATH).size());
    }

    /**
     * Протестировать CRUD операции REST-репозитория заказа-товара
     */
    @Test
    public void testOrderProductRepositoryCRUD()
    {
        // Создаём заказ-товар 1
        Map<String, Object> orderProduct1 = new HashMap<>();
        orderProduct1.put(ORDER_PRODUCT_PRODUCT_KEY, null);
        orderProduct1.put(ORDER_PRODUCT_CLIENT_ORDER_KEY, null);
        orderProduct1.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, 10);
        Map<String, Object> postResponse1 = postEntity(orderProduct1, ORDER_PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ-товар 1
        Assert.assertEquals(orderProduct1.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY),
                postResponse1.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY));
        Assert.assertNotNull(postResponse1.get(ORDER_PRODUCT_PRODUCT_KEY));
        Assert.assertNotNull(postResponse1.get(ORDER_PRODUCT_CLIENT_ORDER_KEY));
        String orderProduct1Link = (String)postResponse1.get(SELF_KEY);
        Assert.assertNotNull(orderProduct1Link);

        // Обновляем заказ-товар 1
        orderProduct1.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, 11);
        Map<String, Object> putResponse = putEntity(orderProduct1, orderProduct1Link);

        // Проверяем поля
        Assert.assertEquals(orderProduct1.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY),
                putResponse.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY));

        // Создаём заказ-товар 2
        Map<String, Object> orderProduct2 = new HashMap<>();
        orderProduct2.put(ORDER_PRODUCT_PRODUCT_KEY, null);
        orderProduct2.put(ORDER_PRODUCT_CLIENT_ORDER_KEY, null);
        orderProduct2.put(ORDER_PRODUCT_COUNT_PRODUCT_KEY, 20);
        Map<String, Object> postResponse2 = postEntity(orderProduct2, ORDER_PRODUCTS_PATH);

        // Проверяем поля и сохраняем ссылку на заказ-товар 2
        Assert.assertEquals(orderProduct2.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY),
                postResponse2.get(ORDER_PRODUCT_COUNT_PRODUCT_KEY));
        Assert.assertNotNull(postResponse2.get(ORDER_PRODUCT_PRODUCT_KEY));
        Assert.assertNotNull(postResponse2.get(ORDER_PRODUCT_CLIENT_ORDER_KEY));
        String orderProduct2Link = (String)postResponse2.get(SELF_KEY);
        Assert.assertNotNull(orderProduct2Link);

        // Проверяем, что есть 2 заказа-товара
        Assert.assertEquals(2, getEntities(ORDER_PRODUCTS_PATH).size());

        // Удаляем заказы-товары
        deleteEntity(orderProduct1Link);
        deleteEntity(orderProduct2Link);

        // Проверяем, что заказов-товаров нет
        Assert.assertEquals(0, getEntities(ORDER_PRODUCTS_PATH).size());
    }
}