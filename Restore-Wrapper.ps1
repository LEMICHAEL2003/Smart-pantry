$ErrorActionPreference = 'Stop'
$wrapperDirectory = Join-Path $PSScriptRoot 'gradle/wrapper'
$encodedWrapper = Get-Content -LiteralPath (Join-Path $wrapperDirectory 'gradle-wrapper.jar.base64') -Raw
[IO.File]::WriteAllBytes((Join-Path $wrapperDirectory 'gradle-wrapper.jar'), [Convert]::FromBase64String($encodedWrapper))
Write-Host 'Gradle wrapper restored. Open this project in Android Studio.'
