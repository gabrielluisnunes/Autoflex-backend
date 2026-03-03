param(
    [string]$BaseUrl = "http://localhost:8081",
    [string]$AdminUser = "admin",
    [string]$AdminPassword = "admin123"
)

$ErrorActionPreference = "Stop"

function Write-Step($message) {
    Write-Host "`n==> $message" -ForegroundColor Cyan
}

function Assert-True($condition, $message) {
    if (-not $condition) {
        throw $message
    }
}

try {
    Write-Step "Health check do backend"
    $apiDocs = Invoke-WebRequest -Uri "$BaseUrl/api-docs" -UseBasicParsing -TimeoutSec 15
    Assert-True ($apiDocs.StatusCode -eq 200) "Backend não respondeu 200 em /api-docs"
    Write-Host "OK: /api-docs = 200"

    Write-Step "Login ADMIN e geração de JWT"
    $loginBody = @{ username = $AdminUser; password = $AdminPassword } | ConvertTo-Json
    $login = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -TimeoutSec 15
    Assert-True (-not [string]::IsNullOrWhiteSpace($login.token)) "Token JWT não retornado no login"
    $token = $login.token
    $headers = @{ Authorization = "Bearer $token" }
    Write-Host "OK: Login ADMIN realizado"

    Write-Step "Register USER"
    $stamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
    $newUser = "case.smoke.$stamp"
    $registerBody = @{ username = $newUser; password = "case12345" } | ConvertTo-Json
    $register = Invoke-RestMethod -Uri "$BaseUrl/api/auth/register" -Method POST -ContentType "application/json" -Body $registerBody -TimeoutSec 15
    Assert-True ($register.username -eq $newUser) "Register não retornou o usuário esperado"
    Write-Host "OK: Register USER ($newUser)"

    Write-Step "GET sugestões de produção (protegido)"
    $suggestions = Invoke-RestMethod -Uri "$BaseUrl/api/production/suggestions" -Headers $headers -Method GET -TimeoutSec 15
    Assert-True ($null -ne $suggestions.suggestions) "Resposta de sugestões inválida"
    $count = @($suggestions.suggestions).Count
    Write-Host "OK: sugestões retornadas ($count itens)"

    Write-Step "GET produção sem token deve negar"
    $unauthorizedBlocked = $false
    try {
        Invoke-WebRequest -Uri "$BaseUrl/api/production/suggestions" -UseBasicParsing -TimeoutSec 15 | Out-Null
    }
    catch {
        if ($_.Exception.Response -and $_.Exception.Response.StatusCode.value__ -eq 401) {
            $unauthorizedBlocked = $true
        }
    }
    Assert-True $unauthorizedBlocked "Endpoint protegido não retornou 401 sem token"
    Write-Host "OK: sem token retorna 401"

    Write-Step "Smoke test finalizado com sucesso"
    Write-Host "Todos os checks principais passaram." -ForegroundColor Green
}
catch {
    Write-Host "FALHA NO SMOKE TEST: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
