# 验证本地资源脚本
# 用于检查所有外部资源是否已成功下载到本地

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  本地资源验证脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$allPassed = $true

# 定义需要检查的文件
$requiredFiles = @(
    # Inter字体
    @{path="src/assets/fonts/inter/inter.css"; name="Inter CSS"; minSize=500},
    @{path="src/assets/fonts/inter/inter-300.ttf"; name="Inter Light (300)"; minSize=300000},
    @{path="src/assets/fonts/inter/inter-400.ttf"; name="Inter Regular (400)"; minSize=300000},
    @{path="src/assets/fonts/inter/inter-500.ttf"; name="Inter Medium (500)"; minSize=300000},
    @{path="src/assets/fonts/inter/inter-600.ttf"; name="Inter SemiBold (600)"; minSize=300000},
    @{path="src/assets/fonts/inter/inter-700.ttf"; name="Inter Bold (700)"; minSize=300000},
    
    # Font Awesome
    @{path="src/assets/fonts/font-awesome/css/font-awesome.min.css"; name="Font Awesome CSS"; minSize=25000},
    @{path="src/assets/fonts/font-awesome/fonts/fontawesome-webfont.woff2"; name="Font Awesome WOFF2"; minSize=70000},
    @{path="src/assets/fonts/font-awesome/fonts/fontawesome-webfont.woff"; name="Font Awesome WOFF"; minSize=90000},
    @{path="src/assets/fonts/font-awesome/fonts/fontawesome-webfont.ttf"; name="Font Awesome TTF"; minSize=150000},
    @{path="src/assets/fonts/font-awesome/fonts/fontawesome-webfont.eot"; name="Font Awesome EOT"; minSize=150000},
    @{path="src/assets/fonts/font-awesome/fonts/fontawesome-webfont.svg"; name="Font Awesome SVG"; minSize=400000}
)

Write-Host "1. 检查文件完整性..." -ForegroundColor Yellow
Write-Host ""

foreach ($file in $requiredFiles) {
    if (Test-Path $file.path) {
        $size = (Get-Item $file.path).Length
        $sizeKB = [math]::Round($size / 1KB, 2)
        
        if ($size -ge $file.minSize) {
            Write-Host "  ✓ $($file.name)" -ForegroundColor Green -NoNewline
            Write-Host " ($sizeKB KB)" -ForegroundColor Gray
        } else {
            Write-Host "  ✗ $($file.name)" -ForegroundColor Red -NoNewline
            Write-Host " (文件太小: $sizeKB KB)" -ForegroundColor Red
            $allPassed = $false
        }
    } else {
        Write-Host "  ✗ $($file.name)" -ForegroundColor Red -NoNewline
        Write-Host " (文件不存在)" -ForegroundColor Red
        $allPassed = $false
    }
}

Write-Host ""
Write-Host "2. 检查index.html配置..." -ForegroundColor Yellow
Write-Host ""

$indexHtml = Get-Content "index.html" -Raw

# 检查是否移除了外部CDN链接
$hasGoogleFonts = $indexHtml -match "fonts\.googleapis\.com"
$hasFontAwesomeCDN = $indexHtml -match "cdnjs\.cloudflare\.com.*font-awesome"

# 检查是否添加了本地链接
$hasLocalInter = $indexHtml -match "/src/assets/fonts/inter/inter\.css"
$hasLocalFontAwesome = $indexHtml -match "/src/assets/fonts/font-awesome/css/font-awesome\.min\.css"

if (-not $hasGoogleFonts) {
    Write-Host "  ✓ Google Fonts CDN链接已移除" -ForegroundColor Green
} else {
    Write-Host "  ✗ 仍存在Google Fonts CDN链接" -ForegroundColor Red
    $allPassed = $false
}

if (-not $hasFontAwesomeCDN) {
    Write-Host "  ✓ Font Awesome CDN链接已移除" -ForegroundColor Green
} else {
    Write-Host "  ✗ 仍存在Font Awesome CDN链接" -ForegroundColor Red
    $allPassed = $false
}

if ($hasLocalInter) {
    Write-Host "  ✓ 本地Inter字体已引用" -ForegroundColor Green
} else {
    Write-Host "  ✗ 未引用本地Inter字体" -ForegroundColor Red
    $allPassed = $false
}

if ($hasLocalFontAwesome) {
    Write-Host "  ✓ 本地Font Awesome已引用" -ForegroundColor Green
} else {
    Write-Host "  ✗ 未引用本地Font Awesome" -ForegroundColor Red
    $allPassed = $false
}

Write-Host ""
Write-Host "3. 检查文档..." -ForegroundColor Yellow
Write-Host ""

if (Test-Path "docs/本地资源说明.md") {
    Write-Host "  ✓ 本地资源说明文档已创建" -ForegroundColor Green
} else {
    Write-Host "  ✗ 本地资源说明文档缺失" -ForegroundColor Red
    $allPassed = $false
}

if (Test-Path "public/test-fonts.html") {
    Write-Host "  ✓ 字体测试页面已创建" -ForegroundColor Green
} else {
    Write-Host "  ✗ 字体测试页面缺失" -ForegroundColor Red
    $allPassed = $false
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

if ($allPassed) {
    Write-Host "  ✅ 所有检查通过!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "下一步:" -ForegroundColor Yellow
    Write-Host "  1. 运行 'npm run dev' 启动开发服务器" -ForegroundColor White
    Write-Host "  2. 访问 http://localhost:5173/test-fonts.html 测试字体" -ForegroundColor White
    Write-Host "  3. 检查主应用页面,确认字体和图标正常显示" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "  ❌ 部分检查未通过" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "请检查上述错误并重新运行此脚本" -ForegroundColor Yellow
    Write-Host ""
}

