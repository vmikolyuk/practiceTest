properties([
        buildDiscarder(logRotator(numToKeepStr: '15')), // хранить последние 15 сборок
        disableResume(),
        parameters([
                text(name: 'commands', defaultValue: 'git status\ngit branch -a', description: 'Список команд', trim: true)
        ])
])

/**
 * Проверка задания по git
 */
def testTaskGit() {
    stage('Task git') {
        dir('repo') {
            git branch: 'main', url: 'https://github.com/vmikolyuk/task1.git'
            // создаем ветки для всех удаленных веток и настраиваем git пользователя
            wrap([$class: 'BuildUser']) {
                sh """
                    for remote in `git branch -r | grep -v /HEAD`; do git checkout --track \$remote ; done || true
                    git config --global user.email "$BUILD_USER_EMAIL"
                    git config --global user.name "$BUILD_USER"
                """
            }
            sh params.commands
        }

        dir('test') {
            withMaven(maven: 'mvn') {
                sh """
                    mvn test -Dtest="ru.naumen.practiceTest.task1.TestPracticeTask*\" \
                             -Dapp.git.repo="$WORKSPACE/${env.BUILD_NUMBER}/repo"
                """
            }
        }
    }
}

// таймаут сборки
timeout(time: 2, unit: 'MINUTES') {
    node {
        dir("${env.BUILD_NUMBER}") {
            stage('Prepare') {
                // проверка заполненности URL репозитория
                if (params.commands == '') {
                    currentBuild.result = 'ABORTED'
                    error('Command list is empty')
                }
                // загрузка репозиториев
                dir('test') {
                    git branch: 'main', url: 'https://github.com/vmikolyuk/practiceTest'
                }
            }
            try {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64']) {
                    testTaskGit()
                }
            } finally {
                // очистка ресурсов
                cleanWs()
            }
        }
    }
}
