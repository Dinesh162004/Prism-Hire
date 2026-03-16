$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$wrapperJar = Join-Path $scriptDir ".mvn\wrapper\maven-wrapper.jar"
$wrapperProperties = Join-Path $scriptDir ".mvn\wrapper\maven-wrapper.properties"

if (!(Test-Path $wrapperJar)) {
    Write-Host "Downloading maven-wrapper.jar..."
    Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -OutFile $wrapperJar
}

$javaCmd = "java"
if ($env:JAVA_HOME) {
    $javaCmd = Join-Path $env:JAVA_HOME "bin\java.exe"
}

$argsString = $args -join " "
Write-Host "Running Maven wrapper..."
& $javaCmd "-Dmaven.multiModuleProjectDirectory=$scriptDir" "-classpath" $wrapperJar "org.apache.maven.wrapper.MavenWrapperMain" $args
