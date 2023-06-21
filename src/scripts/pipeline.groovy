def appPort
def appPomFile

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

pipeline {
    // любая свободная нода
    agent any

    // параметры сборки
    options {
        buildDiscarder(logRotator(numToKeepStr: '15')) // хранить последние 15 сборок
        disableResume()
        parallelsAlwaysFailFast() // завершить сборку если хоть один этап упадет
        timeout(time: 5, unit: 'MINUTES') // таймаут сборки
    }

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
    }

    stages {
        // этап проверки
        stage('Check') {
            steps {
                script {
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
                }
            }
        }

        stage('All') {
            parallel {
                // запуск приложения
                stage('Running app') {
                    steps {
                        dir("${env.BUILD_NUMBER}/app") {
                            git branch: params.branch, url: params.url
                            withMaven(maven: 'mvn') {
                                script {
                                    appPomFile = findFiles(glob: '**/pom.xml')[0]
                                    echo "Found pom file $appPomFile"
                                    sh "mvn -B -f $appPomFile clean package -DskipTests"
                                }
                            }
                            script {
                                def jarFile = findFiles(glob: '**/target/*.jar')[0]
                                echo "Found jar file $jarFile"

                                // Находим свободный порт
                                appPort = getFreePort()
                                echo "Found available port $appPort"

                                def springArgs = "-Dspring.datasource.url=jdbc:h2:file:./db -Dserver.port=$appPort"
                                sh """
                                    $JAVA_HOME/bin/java -Xmx128m $springArgs -jar $jarFile &
                                    echo \$! > app.pid
                                    wait \$(cat app.pid) || exit 0
                                """
                            }
                        }
                    }
                }

                stage('Testing') {
                    steps {
                        // ожидание старта приложения
                        timeout(time: 180, unit: 'SECONDS') {
                            waitUntil(initialRecurrencePeriod: 1000) {
                                script {
                                    try {
                                        def request = httpRequest "http://localhost:$appPort"
                                        return (request.status == 200)
                                    }
                                    catch (def ignored) {
                                        return false
                                    }
                                }
                            }
                        }

                        echo 'App started....'

                        // запуск тестов
                        dir("${env.BUILD_NUMBER}/test") {
                            git branch: 'main', url: 'https://github.com/vmikolyuk/practiceTest'
                            withMaven(maven: 'mvn') {
                                script {
                                    def dTest = (1..(Integer.valueOf(params.task)))
                                            .collect { "ru.naumen.practiceTest.task${it}.TestPracticeTask*" }
                                            .join(',')
                                    sh "mvn test -Dtest=$dTest -Dapp.port=$appPort"
                                }
                            }
                        }
                    }

                    post {
                        always {
                            // остановка приложения
                            dir("${env.BUILD_NUMBER}/app") {
                                sh 'while ps -p $(cat app.pid); do kill $(cat app.pid); sleep 1; done'
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
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
