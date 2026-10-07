[CmdletBinding()]
param(
    [string]$Path = "src/test/resources/drivers/win",

    [string[]]$Drivers = @(
        "chromedriver.exe",
        "geckodriver.exe",
        "msedgedriver.exe"
    ),

    [ValidateSet("win32", "win64")]
    [string]$DriverType = "win64"
)

$ErrorActionPreference = "Stop"

# Resolve the output directory relative to the current working directory.
$OutputPath = [System.IO.Path]::GetFullPath($Path)

Write-Host "Selenium driver updater"
Write-Host "Output directory: $OutputPath"
Write-Host "Driver type:      $DriverType"
Write-Host "Drivers:          $($Drivers -join ', ')"
Write-Host ""

# Ensure output directory exists.
New-Item -ItemType Directory -Path $OutputPath -Force | Out-Null

function Download-And-ExtractDriver {
    param(
        [string]$Name,
        [string]$Url,
        [string]$ExecutableName
    )

    $tempDirectory = Join-Path $env:TEMP ("selenium-driver-" + [Guid]::NewGuid())
    $zipPath = Join-Path $tempDirectory "$Name.zip"

    try {
        New-Item -ItemType Directory -Path $tempDirectory -Force | Out-Null

        Write-Host "Downloading $Name..."
        Write-Host "  $Url"

        Invoke-WebRequest `
            -Uri $Url `
            -OutFile $zipPath `
            -UseBasicParsing

        Write-Host "Extracting $Name..."

        Expand-Archive `
            -Path $zipPath `
            -DestinationPath $tempDirectory `
            -Force

        $extractedDriver = Get-ChildItem `
            -Path $tempDirectory `
            -Filter $ExecutableName `
            -File `
            -Recurse |
            Select-Object -First 1

        if ($null -eq $extractedDriver) {
            throw "Could not find '$ExecutableName' in downloaded archive."
        }

        $destination = Join-Path $OutputPath $ExecutableName

        # Remove the old driver before replacing it.
        if (Test-Path $destination) {
            Write-Host "Removing existing $ExecutableName..."
            Remove-Item -Path $destination -Force
        }

        Move-Item `
            -Path $extractedDriver.FullName `
            -Destination $destination `
            -Force

        Write-Host "Updated: $destination" -ForegroundColor Green
        Write-Host ""
    }
    finally {
        if (Test-Path $tempDirectory) {
            Remove-Item `
                -Path $tempDirectory `
                -Recurse `
                -Force `
                -ErrorAction SilentlyContinue
        }
    }
}

function Update-ChromeDriver {
    Write-Host "Resolving latest stable ChromeDriver..."

    $metadataUrl = "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json"

    $metadata = Invoke-RestMethod -Uri $metadataUrl

    $stable = $metadata.channels.Stable

    if ($null -eq $stable) {
        throw "Could not resolve the latest stable Chrome version."
    }

    $platform = if ($DriverType -eq "win64") {
        "win64"
    }
    else {
        "win32"
    }

    $driver = $stable.downloads.chromedriver |
        Where-Object { $_.platform -eq $platform } |
        Select-Object -First 1

    if ($null -eq $driver) {
        throw "Could not find a ChromeDriver download for platform '$platform'."
    }

    Write-Host "Chrome version: $($stable.version)"

    Download-And-ExtractDriver `
        -Name "ChromeDriver-$($stable.version)" `
        -Url $driver.url `
        -ExecutableName "chromedriver.exe"
}

function Update-GeckoDriver {
    Write-Host "Resolving latest stable GeckoDriver..."

    $releaseUrl = "https://api.github.com/repos/mozilla/geckodriver/releases/latest"

    $release = Invoke-RestMethod `
        -Uri $releaseUrl `
        -Headers @{
            "Accept" = "application/vnd.github+json"
        }

    $version = $release.tag_name

    if ([string]::IsNullOrWhiteSpace($version)) {
        throw "Could not resolve the latest GeckoDriver version."
    }

    # Mozilla uses v<version> in the release asset names.
    $platform = if ($DriverType -eq "win64") {
        "win64"
    }
    else {
        "win32"
    }

    $assetName = "geckodriver-$version-$platform.zip"

    $asset = $release.assets |
        Where-Object { $_.name -eq $assetName } |
        Select-Object -First 1

    if ($null -eq $asset) {
        throw "Could not find GeckoDriver asset '$assetName'."
    }

    Write-Host "GeckoDriver version: $version"

    Download-And-ExtractDriver `
        -Name "GeckoDriver-$version" `
        -Url $asset.browser_download_url `
        -ExecutableName "geckodriver.exe"
}

function Update-EdgeDriver {
    Write-Host "Resolving latest stable EdgeDriver..."

    $webdriverPageUrl = "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver"

    try {
        $page = Invoke-WebRequest `
            -Uri $webdriverPageUrl `
            -UseBasicParsing

        $html = $page.Content
    }
    catch {
        throw "Could not access Microsoft Edge WebDriver download page. $($_.Exception.Message)"
    }

    # Determine the EdgeDriver archive according to the requested driver type.
    switch ($DriverType) {
        "win64" {
            $driverArchive = "edgedriver_win64.zip"
        }

        "win32" {
            $driverArchive = "edgedriver_win32.zip"
        }

        default {
            throw "Unsupported EdgeDriver type '$DriverType'."
        }
    }

    Write-Host "Requested EdgeDriver type: $DriverType"
    Write-Host "Expected archive: $driverArchive"

    # Find the Stable Channel section.
    $stableIndex = $html.IndexOf("Stable Channel")

    if ($stableIndex -lt 0) {
        throw "Could not find 'Stable Channel' on Microsoft's Edge WebDriver page."
    }

    # The Stable Channel download links are located shortly after
    # the Stable Channel heading.
    $stableSectionLength = [Math]::Min(
        10000,
        $html.Length - $stableIndex
    )

    $stableSection = $html.Substring(
        $stableIndex,
        $stableSectionLength
    )

    # Find the requested platform's download URL.
    $pattern = 'https://msedgedriver\.microsoft\.com/[^"]+/' +
               [regex]::Escape($driverArchive)

    $match = [regex]::Match(
        $stableSection,
        $pattern,
        [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
    )

    if (-not $match.Success) {
        throw "Could not find the stable EdgeDriver download for '$DriverType'."
    }

    $downloadUrl = $match.Value

    # Extract the driver version from the URL.
    $versionPattern = 'msedgedriver\.microsoft\.com/([^/]+)/'

    $versionMatch = [regex]::Match(
        $downloadUrl,
        $versionPattern,
        [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
    )

    if ($versionMatch.Success) {
        $driverVersion = $versionMatch.Groups[1].Value
    }
    else {
        $driverVersion = "latest"
    }

    Write-Host "Latest stable EdgeDriver: $driverVersion"
    Write-Host "Download URL: $downloadUrl"

    Download-And-ExtractDriver `
        -Name "EdgeDriver-$driverVersion" `
        -Url $downloadUrl `
        -ExecutableName "msedgedriver.exe"
}


try {
    foreach ($driver in $Drivers) {
        switch ($driver.ToLowerInvariant()) {
            "chromedriver.exe" {
                Update-ChromeDriver
            }

            "geckodriver.exe" {
                Update-GeckoDriver
            }

            "msedgedriver.exe" {
                Update-EdgeDriver
            }

            default {
                throw "Unsupported driver '$driver'. Supported drivers: chromedriver.exe, geckodriver.exe, msedgedriver.exe"
            }
        }
    }

    Write-Host "All requested Selenium drivers have been updated successfully." `
        -ForegroundColor Green
}
catch {
    Write-Error "Selenium driver update failed: $($_.Exception.Message)"
    exit 1
}

