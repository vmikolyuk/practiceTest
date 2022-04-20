package ru.naumen.practiceTest.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * Тестирование практического задания 1
 * @author vmikolyuk
 * @since 25.03.2022
 */
public class TestPracticeTask1
{
    private static final Pattern pattern = Pattern.compile(".+Hello.+world.+");

    /**
     * Тестирование доступности домашней страницы приложения.
     * Ожидается, что на домашней странице будет выведена фраза "Hello world".
     */
    @Test
    public void testHomePage() throws IOException
    {
        String homePageAddress = "http://localhost:8080/";
        URL url = new URL(homePageAddress);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())))
        {
            String content = reader.lines().collect(Collectors.joining());
            Assert.assertTrue("Содержимое страницы не содержит Hello world", pattern.matcher(content).matches());
        }
    }
}
