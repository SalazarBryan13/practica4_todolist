#!/bin/bash
# Deploy script for TodoList EPN - Release 1.4.0
# Compatible with Azure App Service deployment
# Date: August 3, 2025

set -e  # Exit on any error

# Configuration
APP_NAME="todolist-epn-prod"
RESOURCE_GROUP="rg-todolist-prod"
SUBSCRIPTION="Azure-Subscription-TodoList"
LOCATION="East US"
RUNTIME="JAVA|8-jre8"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 TodoList EPN - Production Deployment Script${NC}"
echo -e "${BLUE}================================================${NC}"
echo "App Name: $APP_NAME"
echo "Resource Group: $RESOURCE_GROUP"
echo "Runtime: $RUNTIME"
echo "Location: $LOCATION"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo -e "${YELLOW}🔍 Checking prerequisites...${NC}"

if ! command_exists az; then
    echo -e "${RED}❌ Azure CLI not found. Please install Azure CLI first.${NC}"
    exit 1
fi

if ! command_exists mvn; then
    echo -e "${RED}❌ Maven not found. Please install Maven first.${NC}"
    exit 1
fi

if ! command_exists java; then
    echo -e "${RED}❌ Java not found. Please install Java 8+ first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ All prerequisites satisfied${NC}"

# Login check
echo -e "${YELLOW}🔐 Checking Azure login status...${NC}"
if ! az account show >/dev/null 2>&1; then
    echo -e "${YELLOW}🔑 Please login to Azure...${NC}"
    az login
fi

# Set subscription
echo -e "${YELLOW}📋 Setting Azure subscription...${NC}"
az account set --subscription "$SUBSCRIPTION"
echo -e "${GREEN}✅ Subscription set to: $(az account show --query name -o tsv)${NC}"

# Pre-deployment backup
echo -e "${YELLOW}💾 Creating pre-deployment backup...${NC}"
BACKUP_DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./backups/pre-deployment-$BACKUP_DATE"
mkdir -p "$BACKUP_DIR"

# Download current app settings for backup
az webapp config appsettings list \
    --name "$APP_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --output json > "$BACKUP_DIR/appsettings-backup.json"

echo -e "${GREEN}✅ Backup created at: $BACKUP_DIR${NC}"

# Build application
echo -e "${YELLOW}🔨 Building application...${NC}"
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Application built successfully${NC}"

# Run tests
echo -e "${YELLOW}🧪 Running tests...${NC}"
mvn test
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Tests failed!${NC}"
    read -p "Do you want to continue with deployment? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${RED}❌ Deployment aborted${NC}"
        exit 1
    fi
fi
echo -e "${GREEN}✅ Tests completed${NC}"

# Check if App Service exists
echo -e "${YELLOW}🔍 Checking App Service existence...${NC}"
if ! az webapp show --name "$APP_NAME" --resource-group "$RESOURCE_GROUP" >/dev/null 2>&1; then
    echo -e "${YELLOW}📱 Creating App Service...${NC}"
    
    # Create App Service Plan if it doesn't exist
    PLAN_NAME="${APP_NAME}-plan"
    if ! az appservice plan show --name "$PLAN_NAME" --resource-group "$RESOURCE_GROUP" >/dev/null 2>&1; then
        echo -e "${YELLOW}📋 Creating App Service Plan...${NC}"
        az appservice plan create \
            --name "$PLAN_NAME" \
            --resource-group "$RESOURCE_GROUP" \
            --location "$LOCATION" \
            --sku B1 \
            --is-linux
    fi
    
    # Create Web App
    az webapp create \
        --name "$APP_NAME" \
        --resource-group "$RESOURCE_GROUP" \
        --plan "$PLAN_NAME" \
        --runtime "$RUNTIME"
        
    echo -e "${GREEN}✅ App Service created${NC}"
else
    echo -e "${GREEN}✅ App Service exists${NC}"
fi

# Configure app settings for production
echo -e "${YELLOW}⚙️ Configuring application settings...${NC}"
az webapp config appsettings set \
    --name "$APP_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --settings \
        SPRING_PROFILES_ACTIVE=postgres-prod \
        JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/urandom" \
        WEBSITES_PORT=8080 \
        WEBSITES_CONTAINER_START_TIME_LIMIT=1800

# Configure database connection (using Key Vault or environment variables)
if [ ! -z "$POSTGRES_CONNECTION_STRING" ]; then
    az webapp config appsettings set \
        --name "$APP_NAME" \
        --resource-group "$RESOURCE_GROUP" \
        --settings \
            SPRING_DATASOURCE_URL="$POSTGRES_CONNECTION_STRING" \
            SPRING_DATASOURCE_USERNAME="$POSTGRES_USERNAME" \
            SPRING_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"
else
    echo -e "${YELLOW}⚠️ Database connection strings not provided via environment variables${NC}"
    echo -e "${YELLOW}   Please configure them manually in Azure Portal${NC}"
fi

echo -e "${GREEN}✅ Application settings configured${NC}"

# Deploy application
echo -e "${YELLOW}🚀 Deploying application...${NC}"
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" | head -1)
if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}❌ No JAR file found in target directory${NC}"
    exit 1
fi

echo "Deploying JAR file: $JAR_FILE"

az webapp deploy \
    --name "$APP_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --src-path "$JAR_FILE" \
    --type jar

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Deployment failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Application deployed successfully${NC}"

# Wait for deployment to complete
echo -e "${YELLOW}⏳ Waiting for application to start...${NC}"
sleep 30

# Health check
echo -e "${YELLOW}🏥 Performing health check...${NC}"
HEALTH_URL="https://${APP_NAME}.azurewebsites.net/login"
MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" || echo "000")
    
    if [ "$HTTP_STATUS" = "200" ]; then
        echo -e "${GREEN}✅ Health check passed (HTTP $HTTP_STATUS)${NC}"
        break
    else
        echo -e "${YELLOW}⏳ Health check failed (HTTP $HTTP_STATUS), retrying... ($((RETRY_COUNT + 1))/$MAX_RETRIES)${NC}"
        sleep 15
        RETRY_COUNT=$((RETRY_COUNT + 1))
    fi
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo -e "${RED}❌ Health check failed after $MAX_RETRIES attempts${NC}"
    echo -e "${YELLOW}🔍 Check application logs:${NC}"
    echo "    az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP"
    exit 1
fi

# Get application URL
APP_URL="https://${APP_NAME}.azurewebsites.net"

# Final deployment summary
echo ""
echo -e "${GREEN}🎉 DEPLOYMENT COMPLETED SUCCESSFULLY! 🎉${NC}"
echo -e "${GREEN}=========================================${NC}"
echo -e "${BLUE}Application URL:${NC} $APP_URL"
echo -e "${BLUE}Health Check:${NC} ${GREEN}✅ Passed${NC}"
echo -e "${BLUE}Deployment Time:${NC} $(date)"
echo -e "${BLUE}JAR File:${NC} $JAR_FILE"
echo -e "${BLUE}Backup Location:${NC} $BACKUP_DIR"
echo ""
echo -e "${YELLOW}📊 Useful commands:${NC}"
echo "  View logs:    az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP"
echo "  App settings: az webapp config appsettings list --name $APP_NAME --resource-group $RESOURCE_GROUP"
echo "  Restart app:  az webapp restart --name $APP_NAME --resource-group $RESOURCE_GROUP"
echo "  SSH into app: az webapp ssh --name $APP_NAME --resource-group $RESOURCE_GROUP"
echo ""
echo -e "${GREEN}🌟 TodoList EPN v1.4.0 is now live in production! 🌟${NC}"

# Optional: Open browser
if command_exists open; then
    read -p "Do you want to open the application in your browser? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open "$APP_URL"
    fi
elif command_exists xdg-open; then
    read -p "Do you want to open the application in your browser? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        xdg-open "$APP_URL"
    fi
fi
