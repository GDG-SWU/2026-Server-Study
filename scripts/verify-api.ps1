param([string]$BaseUrl = 'http://127.0.0.1:8080')
$ErrorActionPreference = 'Stop'
$results = [System.Collections.Generic.List[object]]::new()

function Invoke-Check {
    param([string]$Name, [string]$Method, [string]$Path, [int]$ExpectedStatus, $Body)
    $params = @{
        Uri = "$BaseUrl$Path"
        Method = $Method
        UseBasicParsing = $true
    }
    if ($null -ne $Body) {
        $params.ContentType = 'application/json; charset=utf-8'
        $params.Body = [System.Text.Encoding]::UTF8.GetBytes(($Body | ConvertTo-Json))
    }
    $response = Invoke-WebRequest @params
    if ([int]$response.StatusCode -ne $ExpectedStatus) {
        throw "$Name 상태 코드 불일치: $($response.StatusCode)"
    }
    $results.Add([pscustomobject]@{
        name = $Name
        method = $Method
        path = $Path
        status = [int]$response.StatusCode
        body = $response.Content
    })
    return $response
}

$before = Invoke-Check 'initial-list' 'GET' '/api/articles' 200
$initialCount = @($before.Content | ConvertFrom-Json).Count
$created = Invoke-Check 'create' 'POST' '/api/articles' 201 @{
    title = '첫 번째 실습 글'; content = '블로그 API 실습입니다.'
}
$article = $created.Content | ConvertFrom-Json
$articleId = $article.id
try {
    if ($article.title -ne '첫 번째 실습 글') { throw '작성 데이터 불일치' }
    $list = Invoke-Check 'list-after-create' 'GET' '/api/articles' 200
    if (@($list.Content | ConvertFrom-Json).Count -ne ($initialCount + 1)) { throw '목록 개수 불일치' }
    $single = Invoke-Check 'find-one' 'GET' "/api/articles/$articleId" 200
    if (($single.Content | ConvertFrom-Json).content -ne '블로그 API 실습입니다.') { throw '조회 데이터 불일치' }
    $null = Invoke-Check 'update' 'PUT' "/api/articles/$articleId" 200 @{
        title = '수정한 제목'; content = '수정한 내용입니다.'
    }
    $updated = Invoke-Check 'find-after-update' 'GET' "/api/articles/$articleId" 200
    $updatedArticle = $updated.Content | ConvertFrom-Json
    if ($updatedArticle.title -ne '수정한 제목' -or $updatedArticle.content -ne '수정한 내용입니다.') {
        throw '수정 데이터 불일치'
    }
} finally {
    $null = Invoke-Check 'delete' 'DELETE' "/api/articles/$articleId" 200
}
$after = Invoke-Check 'list-after-delete' 'GET' '/api/articles' 200
if (@($after.Content | ConvertFrom-Json).Count -ne $initialCount) { throw '삭제 후 개수 불일치' }
$outputDir = Join-Path $PSScriptRoot '..\docs'
New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
$results | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath (Join-Path $outputDir 'http-verification.json') -Encoding utf8
$results | Select-Object name, method, path, status | Format-Table
Write-Output 'PASS: 작성, 목록 조회, 단일 조회, 수정, 삭제 및 데이터 검증 완료'
