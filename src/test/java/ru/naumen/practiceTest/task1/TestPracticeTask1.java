package ru.naumen.practiceTest.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.naumen.practiceTest.RestTestBase;

/**
 * Тестирование практического задания 1
 * @author vmikolyuk
 * @since 25.03.2022
 */
class TestPracticeTask1 extends RestTestBase
{
    private static final Pattern pattern = Pattern.compile(".+Hello.+world.+");

    /**
     * Тестирование доступности домашней страницы приложения.
     * Ожидается, что на домашней странице будет выведена фраза "Hello world".
     */
    @Test
    void testHomePage() throws IOException
    {
        URL url = new URL(baseURI);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())))
        {
            String content = reader.lines().collect(Collectors.joining());
            Assertions.assertTrue(pattern.matcher(content).matches(), "Содержимое страницы не содержит Hello world");
        }
    }
}
