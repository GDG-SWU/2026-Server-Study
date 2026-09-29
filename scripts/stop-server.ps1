$ErrorActionPreference = 'Stop'
$pidFile = Join-Path $PSScriptRoot '..\.local\server.pid'
if (-not (Test-Path -LiteralPath $pidFile)) {
    Write-Output '백그라운드 서버 PID 파일이 없습니다. 직접 실행한 서버는 Ctrl+C로 종료하세요.'
    return
}
$serverProcessId = [int](Get-Content -LiteralPath $pidFile)
$serverProcess = Get-CimInstance Win32_Process -Filter "ProcessId = $serverProcessId"
if ($null -eq $serverProcess) {
    Write-Output '서버가 이미 종료되었습니다.'
    return
}
if ($serverProcess.CommandLine -notmatch 'blog-api-practice-0\.0\.1-SNAPSHOT\.jar') {
    throw 'PID가 다른 프로세스에 사용 중입니다. 종료하지 않았습니다.'
}
Stop-Process -Id $serverProcessId
Write-Output '블로그 실습 서버를 종료했습니다.'
