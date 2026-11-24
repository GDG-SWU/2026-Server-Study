param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('Fail', 'Restore')]
    [string]$Mode
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$testPath = Join-Path $projectRoot 'src/test/java/com/example/server_study_2026/JwtSecurityTest.java'
$utf8 = New-Object System.Text.UTF8Encoding($false)
$source = [System.IO.File]::ReadAllText($testPath, $utf8)
$passing = '.andExpect(jsonPath("$.data").value("OK v2"));'
$failing = '.andExpect(jsonPath("$.data").value("INTENTIONAL_CI_FAILURE"));'

if ($Mode -eq 'Fail') {
    $before = $passing
    $after = $failing
} else {
    $before = $failing
    $after = $passing
}

if ($source.Contains($after) -and -not $source.Contains($before)) {
    Write-Output "Already in $Mode mode."
    exit 0
}
if ([regex]::Matches($source, [regex]::Escape($before)).Count -ne 1) {
    throw 'Expected exactly one health response assertion. No file was changed.'
}
[System.IO.File]::WriteAllText($testPath, $source.Replace($before, $after), $utf8)
Write-Output "CI practice mode: $Mode. Only the health response assertion was changed."
