param(
    [ValidateSet('test', 'run', 'build')]
    [string]$Task = 'test'
)
$ErrorActionPreference = 'Stop'

# JAVA_HOME이 없으면 이 PC에 설치된 Java 17을 사용한다.
if (-not $env:JAVA_HOME) {
    $bundledJava = 'D:\Java\temurin-17\jdk-17.0.20.1+1'
    if (Test-Path -LiteralPath "$bundledJava\bin\java.exe") {
        $env:JAVA_HOME = $bundledJava
    } else {
        throw 'Java 17을 설치하고 JAVA_HOME을 지정하세요.'
    }
}
$javaRelease = Join-Path $env:JAVA_HOME 'release'
if (-not (Test-Path -LiteralPath $javaRelease) -or
    -not (Select-String -LiteralPath $javaRelease -Pattern '^JAVA_VERSION="17[.\"]' -Quiet)) {
    throw 'JAVA_HOME은 JDK 17을 가리켜야 합니다. IntelliJ의 Project SDK와 Gradle JVM도 17로 지정하세요.'
}
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME = Join-Path $PSScriptRoot '.local\gradle-user-home'
$gradleTask = switch ($Task) {
    'test' { 'test' }
    'run' { 'bootRun' }
    'build' { 'build' }
}
Push-Location $PSScriptRoot
try {
    & .\gradlew.bat $gradleTask --no-daemon --console=plain
    if ($LASTEXITCODE -ne 0) { throw "Gradle 실패: $LASTEXITCODE" }
} finally {
    Pop-Location
}
