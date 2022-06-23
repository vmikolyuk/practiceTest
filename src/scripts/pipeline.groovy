pipeline {
    // любая свободная нода
    agent any

    // параметры сборки
    options {
        parallelsAlwaysFailFast() // завершить сборку если хоть один этап упадет
        timeout(time: 5, unit: 'MINUTES') // таймаут сборки
        disableConcurrentBuilds()
    }

    stages {
        // этап проверки
        stage('Checkout') {
            steps {
                script {
                    // проверка заполненности URL репозитория
                    if (params.url == '')
                    {
                        currentBuild.result = 'ABORTED'
                        error('Repository url not set')
                    }
                    // проверка заполненности ветки
                    if (params.branch == '')
                    {
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
                            // TODO номер сборки добавить
                            git branch: params.branch, url: params.url
                            withMaven(maven: 'mvn') {
                                script {
                                    def pom = findFiles(glob: '**/pom.xml')[0]
                                    echo "Found pom file ${pom}"
                                    sh "mvn clean install spring-boot:start -DskipTests -f ${pom}"
                                }
                            }
                        }
                    }
                }

                stage('Testing') {
                    steps {
                        // ожидание старта приложения
                        timeout(time: 60, unit: 'SECONDS') {
                            waitUntil(initialRecurrencePeriod: 1000) {
                                script {
                                    try
                                    {
                                        def request = httpRequest 'http://localhost:8080/'
                                        return (request.status == 200)
                                    }
                                    catch (def e)
                                    {
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
                                        .collect {"ru.naumen.practiceTest.task${it}.**"}
                                        .join(',')
                                    sh "mvn clean test -Dtest=${dTest}"
                                }
                            }
                        }

                        // остановка приложения
                        dir("${env.BUILD_NUMBER}/app") {
                            withMaven(maven: 'mvn') {
                                script {
                                    def pom = findFiles(glob: '**/pom.xml')[0]
                                    sh "mvn spring-boot:stop -f ${pom}"
                                }
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
