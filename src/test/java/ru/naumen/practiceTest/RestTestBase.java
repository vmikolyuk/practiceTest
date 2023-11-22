package ru.naumen.practiceTest;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * @author aspirkin
 * @since 08.06.2022
 */
public class RestTestBase
{
    protected static final String SELF_KEY = "self";
    protected static final String ID_KEY = "id";

    protected static final String CLIENTS_PATH = "clients";
    protected static final String CLIENT_FULL_NAME_KEY = "fullName";
    protected static final String CLIENT_ADDRESS_KEY = "address";
    protected static final String CLIENT_PHONE_NUMBER_KEY = "phoneNumber";
    protected static final String CLIENT_EXTERNAL_ID_KEY = "externalId";
    protected static final String CLIENT_CLIENT_KEY = "client";

    protected static final String CATEGORIES_PATH = "categories";
    protected static final String CATEGORY_NAME_KEY = "name";
    protected static final String CATEGORY_PARENT_KEY = "parent";

    protected static final String PRODUCTS_PATH = "products";
    protected static final String PRODUCT_KEY = "product";
    protected static final String PRODUCT_NAME_KEY = "name";
    protected static final String PRODUCT_CATEGORY_KEY = "category";
    protected static final String PRODUCT_DESCRIPTION_KEY = "description";
    protected static final String PRODUCT_PRICE_KEY = "price";

    protected static final String CLIENT_ORDERS_PATH = "clientOrders";
    protected static final String CLIENT_ORDER_KEY = "clientOrder";
    protected static final String CLIENT_ORDER_CLIENT_KEY = "client";
    protected static final String CLIENT_ORDER_STATUS_KEY = "status";
    protected static final String CLIENT_ORDER_TOTAL_KEY = "total";

    protected static final String ORDER_PRODUCTS_PATH = "orderProducts";
    protected static final String ORDER_PRODUCT_PRODUCT_KEY = "product";
    protected static final String ORDER_PRODUCT_CLIENT_ORDER_KEY = "clientOrder";
    protected static final String ORDER_PRODUCT_COUNT_PRODUCT_KEY = "countProduct";

    protected static final String baseURI;

    static {
        var port = Optional.ofNullable(System.getProperty("app.port")).orElse("8080");
        baseURI = "http://localhost:" + Integer.parseInt(port);
        RestAssured.baseURI = baseURI;
    }

    /**
     * Выполнить DELETE REST-запрос по передаваемому пути для удаления в системе объекта
     * @param path путь
     */
    protected static void deleteEntity(String path)
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
    protected static Map<String, Object> putEntity(Object entity, String path)
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
    protected static Map<String, Object> postEntity(Object entity, String path)
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
    protected static List<Map<String, Object>> getEntities(String path)
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