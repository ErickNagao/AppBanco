# Cleanup build artifacts and generated folders for AppBanco
Set-Location -Path (Split-Path -Parent $MyInvocation.MyCommand.Definition)
Write-Host "Removing target/ and bin/ directories (if present)..."
if (Test-Path .\target) { Remove-Item -Recurse -Force .\target; Write-Host "Removed target/" }
if (Test-Path .\bin) { Remove-Item -Recurse -Force .\bin; Write-Host "Removed bin/" }
if (Test-Path .\data) { Write-Host "Note: data/ exists. Remove manually if you want to delete DB files." }
Write-Host "Cleanup complete."
