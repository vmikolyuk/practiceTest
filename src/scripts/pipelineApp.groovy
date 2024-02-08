properties([
        buildDiscarder(logRotator(numToKeepStr: '15')), // хранить последние 15 сборок
        disableResume(),
        parameters([
                string(name: 'url', defaultValue: 'https://github.com/vmikolyuk/practice', description: 'Адрес репозитория', trim: true),
                string(name: 'branch', defaultValue: 'main', description: 'Имя ветки', trim: true),
                choice(name: 'task', choices: '2\n3\n4', description: 'Номер задания'),
                booleanParam(name: 'optional4', defaultValue: false, description: 'Дополнительное задание 4'),
        ])
])

static def getFreePort() {
    // не используем withCloseable, т.к. jenkins создает CPSClosure,
    // который нельзя передать в withClosable
    // а NonCPS повесить на метод нельзя из-за безопасности
    def s = null
    try {
        s = new ServerSocket(0)
        return s.getLocalPort()
    } finally {
        s?.close()
    }
}

def testTaskApp() {
    stage('Task app') {
        withMaven(maven: 'mvn') {
            def appPomFile = findFiles(glob: '**/pom.xml')[0]
            echo "Found pom file $appPomFile"
            sh "mvn -f \"$appPomFile\" clean package -DskipTests"
        }
        def jarFile = findFiles(glob: '**/target/*.jar')[0]
        echo "Found jar file $jarFile"

        // Находим свободный порт
        def appPort = getFreePort()
        echo "Found available port $appPort"

        def springArgs = "-Dspring.datasource.url=jdbc:h2:file:./db -Dspring.datasource.username=sa -Dserver.port=$appPort"
        sh """
            $JAVA_HOME/bin/java -Xmx128m $springArgs -jar \"$jarFile\" &
            echo \$! > app.pid
        """

        try {
            // ожидание старта приложения
            timeout(time: 180, unit: 'SECONDS') {
                waitUntil(initialRecurrencePeriod: 1000) {
                    try {
                        def request = httpRequest "http://localhost:$appPort"
                        return (request.status == 200)
                    }
                    catch (def ignored) {
                        return false
                    }
                }
            }

            dir('test') {
                withMaven(maven: 'mvn') {
                    def allTests = (2..(Integer.valueOf(params.task)))
                            .collect { "ru.naumen.practiceTest.task${it}.TestPracticeTask*" }
                    if (params.optional4) {
                        allTests.push("ru.naumen.practiceTest.task4.TestOptionalTask4")
                    }
                    def dTest = allTests.join(',')
                    sh "mvn test -Dtest=$dTest -Dapp.port=$appPort"
                }
            }
        } finally {
            // остановка приложения
            sh 'while ps -p $(cat app.pid); do kill $(cat app.pid); sleep 1; done'
        }
    }
}

// таймаут сборки
timeout(time: 5, unit: 'MINUTES') {
    node {
        dir("${env.BUILD_NUMBER}") {
            stage('Prepare') {
                // проверка заполненности URL репозитория
                if (params.url == '') {
                    currentBuild.result = 'ABORTED'
                    error('Repository url not set')
                }
                // проверка заполненности ветки
                if (params.branch == '') {
                    currentBuild.result = 'ABORTED'
                    error('Branch not set')
                }
                // загрузка репозиториев
                dir('task') {
                    git branch: params.branch, url: params.url
                }
                dir('test') {
                    git branch: 'main', url: 'https://github.com/vmikolyuk/practiceTest'
                }
            }
            try {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64']) {
                    testTaskApp()
                }
            } finally {
                // очистка ресурсов
                cleanWs()

// отправка почты
//            wrap([$class: 'BuildUser']) {
//                mail bcc: '',
//                     body: "Build №${env.BUILD_NUMBER} finished with status '${currentBuild.result}'",
//                     cc: '',
//                     from: 'Naumen Practice Bot',
//                     replyTo: '',
//                     subject: "Build №${env.BUILD_NUMBER} - [${currentBuild.result}]",
//                     to: "${BUILD_USER_EMAIL}"
//            }

// если сборка успешна, то отправим еще и боту
//            script {
//                if (currentBuild.result == 'SUCCESS')
//                {
//                    wrap([$class: 'BuildUser']) {
//                        mail bcc: '',
//                             body: "Build №${env.BUILD_NUMBER} finished with status '${currentBuild.result}'",
//                             cc: '',
//                             from: "${BUILD_USER_EMAIL}",
//                             replyTo: '',
//                             subject: "Build №${env.BUILD_NUMBER} - [${currentBuild.result}] user ${BUILD_USER_EMAIL}",
//                             to: "naumenpractice@gmail.com"
//                    }
//                }
//            }
            }
        }
    }
}
