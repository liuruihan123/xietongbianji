#!/bin/bash

# 运行时配置测试脚本
# 用于验证 Docker 容器中的配置是否正确生成

set -e

CONTAINER_NAME="${1:-coedit-frontend}"

echo "========================================="
echo "  运行时配置测试"
echo "========================================="
echo ""

# 检查容器是否运行
if ! docker ps | grep -q "$CONTAINER_NAME"; then
    echo "❌ 容器 $CONTAINER_NAME 未运行"
    echo ""
    echo "请先启动容器:"
    echo "  ./deploy.sh"
    echo "或:"
    echo "  docker-compose up -d"
    exit 1
fi

echo "✅ 容器 $CONTAINER_NAME 正在运行"
echo ""

# 显示配置文件内容
echo "📄 配置文件内容:"
echo "========================================="
docker exec "$CONTAINER_NAME" cat /usr/share/nginx/html/config.js
echo "========================================="
echo ""

# 检查关键配置项
echo "🔍 检查关键配置项:"
echo ""

API_BASE_URL=$(docker exec "$CONTAINER_NAME" cat /usr/share/nginx/html/config.js | grep "API_BASE_URL:" | sed "s/.*API_BASE_URL: '\(.*\)'.*/\1/")
ONLYOFFICE_URL=$(docker exec "$CONTAINER_NAME" cat /usr/share/nginx/html/config.js | grep "ONLYOFFICE_URL:" | sed "s/.*ONLYOFFICE_URL: '\(.*\)'.*/\1/")
WS_URL=$(docker exec "$CONTAINER_NAME" cat /usr/share/nginx/html/config.js | grep "WS_URL:" | sed "s/.*WS_URL: '\(.*\)'.*/\1/")

echo "  API_BASE_URL    = $API_BASE_URL"
echo "  ONLYOFFICE_URL  = $ONLYOFFICE_URL"
echo "  WS_URL          = $WS_URL"
echo ""

# 检查是否包含未替换的占位符
if docker exec "$CONTAINER_NAME" cat /usr/share/nginx/html/config.js | grep -q '\${'; then
    echo "⚠️  警告: 配置文件中包含未替换的占位符"
    echo ""
    echo "未替换的占位符:"
    docker exec "$CONTAINER_NAME" cat /usr/share/nginx/html/config.js | grep '\${' || true
    echo ""
    echo "这可能是因为对应的环境变量未设置"
else
    echo "✅ 所有占位符已正确替换"
fi

echo ""
echo "========================================="
echo "  测试完成"
echo "========================================="
echo ""
echo "访问配置测试页面:"
echo "  http://localhost/config-test"
echo ""

