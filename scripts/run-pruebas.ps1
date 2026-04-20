$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$jdkHome = $env:JAVA_HOME
if (-not $jdkHome -or -not (Test-Path (Join-Path $jdkHome "bin\javac.exe"))) {
    $jdkHome = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr"
}

$javac = Join-Path $jdkHome "bin\javac.exe"
$java = Join-Path $jdkHome "bin\java.exe"
if (-not (Test-Path $javac)) {
    throw "No se ha encontrado javac. Define JAVA_HOME apuntando a un JDK 21."
}

New-Item -ItemType Directory -Path "target\manual-classes" -Force | Out-Null
Get-ChildItem -Path "src\main\java","src\test\java" -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName } |
    Set-Content -Path "target\manual-sources.txt" -Encoding ASCII

& $javac --release 21 -encoding UTF-8 -d "target\manual-classes" "@target\manual-sources.txt"
& $java -cp "target\manual-classes" es.urjc.metprog.tests.SuitePruebas
& $java -cp "target\manual-classes" es.urjc.metprog.verification.VerificationRunner fresh
& $java -cp "target\manual-classes" es.urjc.metprog.verification.VerificationRunner persistence-check
