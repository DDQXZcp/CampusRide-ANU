# CampusRide IoT Real-Time Visualization Dashboard

[CampusRide Website](https://campusride.herman-tang.com)

CampusRide is an IoT dashboard for real-time visualization of scooter operations. It retrieves scooter status — including location, speed, and battery level — and sends the data via a public MQTT broker to the Spring Boot backend. The frontend communicates with the backend over WebSocket, allowing the backend to push the latest data to the frontend and update the UI instantly.

![Architecture](./apps/frontend/public/images/CampusRide-Architecture.png)

## Overview

CampusRide delivers a fully functional dashboard for monitoring scooter status in real time. It is built on:

**Frontend**
- React 19
- TypeScript
- Tailwind CSS
- S3 + CloudFront (with ACM certificates)

**Backend**
- Spring Boot
- Nginx reverse proxy
- EMQX Cloud
- AWS EC2

**MQTT Broker**
- EMQX Cloud

**End Device**
- Raspberry Pi + GPS module + speed encoder

![CampusRide Frontend](./apps/frontend/public/images/CampusRide-Frontend.png)

## Local Installation

### Prerequisites

Ensure the following are installed and set up:
- JDK 17
- Maven

**Start the Backend Server**
```bash
cd apps/backend/
mvn spring-boot:run
```
**Start the Frontend Server**
```bash
cd apps/frontend
yarn install
yarn dev
```

Configure your own certificate, username, and password for the EMQX MQTT Broker by setting the credential paths.

## EMQX Cloud MQTT Broker

Cost comparison for 10 messages per second, 24/7 for 30 days:

**AWS IoT Core**
- Messaging: $1 per 1M messages → 25.92M ≈ $25.92/month
- Connection minutes: 43,200 per device → ~$0.0035/month (negligible)
- Total: ~$25.92/month

**EMQX Cloud (Serverless Free Tier)**
- Session minutes: 43,200/month → within 1M free tier → $0
- Traffic: 1 KB/message → 24.7 GB/month
- Free tier includes 1 GB → ~23.7 GB extra
- Extra traffic cost: 23.7 × $0.15 ≈ $3.56/month
- Total: ~$3.56/month

Choice: EMQX Cloud is used for lower cost. Download the certificate from the EMQX portal and configure authentication and authorization.

## Deployment (Backend)

### 1. Base Infrastructure Deployment (Manual)

**Purpose:** Provision and configure the persistent backend resources.

**Steps:**
1. **Prepare Networking**
   - Create VPC and subnet.
   - Allocate an Elastic IP (EIP).

2. **Launch EC2 Instance**
   - Instance type: `t3.micro`
   - Place it in the target VPC/subnet.
   - Create and attach a security group.

3. **DNS Configuration**
   - Associate the EIP with the EC2 instance.
   - Create a Route 53 record mapping `campusride.herman-tang.com` to the EIP.

4. **Server Configuration (Manual)**
   - Securely copy SSL certificates to the EC2 instance.
   - Store AWS keys and EMQX MQTT credentials in GitHub for CI/CD workflows.
   - Set up Nginx with SSL for `api.campusride.herman-tang.com`.
   - Configure Nginx to forward HTTPS (443) requests to HTTP (8080) on the Spring Boot backend.

**CloudFormation Template:**  
`apps/infrastructure/infrastructure-backend.yml`

> This base infrastructure is deployed manually since it rarely changes.

---

### 2. Backend Application Deployment (CI/CD)

**Workflow:** `.github/workflows/deploy-backend.yml`  
**Trigger Conditions:**
- Push to `main` affecting files in `apps/backend/`

**Automated Process:**
1. Build the Spring Boot application.
2. Copy the generated JAR file to the EC2 instance.
3. Run the JAR as a system service to ensure it restarts automatically if the instance reboots.

---

## Deploy Frontend

### CI access with GitHub OIDC and AWS STS

The Frontend CI/CD uses GitHub OpenID Connect (OIDC) to let workflows obtain temporary AWS credentials from AWS STS.

Benefits:
- No stored AWS secrets in GitHub
- Short-lived credentials
- Trust restricted to specific repository and branch

### CI/CD Frontend CloudFormation Stack Deployment
Frontend deployment:
- S3 bucket (SPA hosting)
- CloudFront distribution (with Route 53 + ACM SSL cert)
- WebSocket: campusride.herman-tang.com → EC2 public IP → Nginx → Spring Boot

Template: apps/infrastructure/infrastructure-frontend.yml

CI/CD: GitHub Actions workflow .github/workflows/deploy-frontend.yml triggers on:
- Push to main affecting apps/frontend/ or infrastructure-frontend.yml
- Manual dispatch

Process:
1. Build React frontend (yarn build)
2. Deploy/update CampusRide-Frontend stack
3. Upload build to S3
4. Invalidate CloudFront cache

API URL:
- Frontend uses VITE_API_URL. In production, it is set to an empty string so CloudFront forwards API requests to the backend.

