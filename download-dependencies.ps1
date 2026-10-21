$ErrorActionPreference = 'Stop'

# Required dependencies of Apache POI 5.5.1; optional PDF/SVG/signing libraries are omitted.
$artifacts = @(
    'org/apache/poi/poi/5.5.1/poi-5.5.1',
    'org/apache/poi/poi-ooxml/5.5.1/poi-ooxml-5.5.1',
    'org/apache/poi/poi-ooxml-lite/5.5.1/poi-ooxml-lite-5.5.1',
    'org/apache/xmlbeans/xmlbeans/5.3.0/xmlbeans-5.3.0',
    'org/apache/commons/commons-compress/1.28.0/commons-compress-1.28.0',
    'commons-io/commons-io/2.21.0/commons-io-2.21.0',
    'commons-codec/commons-codec/1.20.0/commons-codec-1.20.0',
    'org/apache/commons/commons-collections4/4.5.0/commons-collections4-4.5.0',
    'org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1',
    'org/apache/commons/commons-lang3/3.18.0/commons-lang3-3.18.0',
    'com/zaxxer/SparseBitSet/1.3/SparseBitSet-1.3',
    'com/github/virtuald/curvesapi/1.08/curvesapi-1.08',
    'org/apache/logging/log4j/log4j-api/2.24.3/log4j-api-2.24.3'
)

$libraryDirectory = Join-Path $PSScriptRoot 'lib'
New-Item -ItemType Directory -Path $libraryDirectory -Force | Out-Null
foreach ($artifact in $artifacts) {
    $url = "https://repo.maven.apache.org/maven2/$artifact.jar"
    $destination = Join-Path $libraryDirectory (($artifact.Split('/')[-1]) + '.jar')
    try {
        $expectedHash = ((Invoke-WebRequest -Uri "$url.sha512" -UseBasicParsing).Content.Trim() -split '\s+')[0]
    } catch {
        if ($_.Exception.Response.StatusCode.value__ -ne 404) { throw }
        # Older releases have no SHA512 sidecar. Compare SHA512 with a second
        # HTTPS copy from Google's Maven Central mirror instead of using SHA1.
        $mirrorFile = Join-Path $libraryDirectory 'mirror-verification.tmp'
        try {
            Invoke-WebRequest -Uri "https://maven-central.storage-download.googleapis.com/maven2/$artifact.jar" -OutFile $mirrorFile -UseBasicParsing
            $expectedHash = (Get-FileHash -LiteralPath $mirrorFile -Algorithm SHA512).Hash
        } finally {
            if (Test-Path -LiteralPath $mirrorFile) { Remove-Item -LiteralPath $mirrorFile }
        }
    }
    if (!(Test-Path -LiteralPath $destination) -or
        (Get-FileHash -LiteralPath $destination -Algorithm SHA512).Hash -ne $expectedHash) {
        Invoke-WebRequest -Uri $url -OutFile $destination -UseBasicParsing
    }
    if ((Get-FileHash -LiteralPath $destination -Algorithm SHA512).Hash -ne $expectedHash) {
        throw "SHA512 mismatch: $destination"
    }
    Write-Host "Verified: $(Split-Path $destination -Leaf)"
}
